package com.mycompany.northwindmanager;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class SupplierDAO {

    private final EntityManagerFactory emf;

    // Il costruttore riceve l'EntityManagerFactory da HibernateUtil
    public SupplierDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    // restituisce tutti i fornitori
    public List<Supplier> findAll() {
        EntityManager em = emf.createEntityManager(); //sessione con db
        try {
            TypedQuery<Supplier> query = em.createQuery(
                "SELECT s FROM Supplier s", Supplier.class
            );
            return query.getResultList(); //esegue la query
        } finally {
            em.close();
        }
    }

    //restituisce un fornitore per ID
    public Supplier findById(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Supplier.class, id);
        } finally {
            em.close();
        }
    }

    // CREATE nuovo fornitore
    public Supplier save(Supplier supplier) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin(); //inizia a tenere traccia delle modifiche
            em.persist(supplier); //crea l'elemento
            tx.commit(); //lo aggiunge
            return supplier;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback(); //se fallisce torna a quella prima
            throw e;
        } finally {
            em.close();
        }
    }

    // UPDATE un fornitore esistente
    public Supplier update(Supplier supplier) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Supplier updated = em.merge(supplier); //aggiorna un elemento esistente
            tx.commit();
            return updated;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    // DELETE un fornitore per ID
    public boolean delete(int id) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Supplier supplier = em.find(Supplier.class, id);
            if (supplier == null) {
                tx.rollback();
                return false; //ID non trovato
            }
            em.remove(supplier);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}
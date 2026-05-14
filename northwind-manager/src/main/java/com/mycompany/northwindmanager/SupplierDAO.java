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

    // ───────────────────────────────────────────
    // READ ALL — restituisce tutti i fornitori
    // ───────────────────────────────────────────
    public List<Supplier> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Supplier> query = em.createQuery(
                "SELECT s FROM Supplier s", Supplier.class
            );
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    // ───────────────────────────────────────────
    // READ ONE — restituisce un fornitore per ID
    // ───────────────────────────────────────────
    public Supplier findById(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Supplier.class, id);
        } finally {
            em.close();
        }
    }

    // ───────────────────────────────────────────
    // CREATE — inserisce un nuovo fornitore
    // ───────────────────────────────────────────
    public Supplier save(Supplier supplier) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(supplier);
            tx.commit();
            return supplier;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    // ───────────────────────────────────────────
    // UPDATE — modifica un fornitore esistente
    // ───────────────────────────────────────────
    public Supplier update(Supplier supplier) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Supplier updated = em.merge(supplier);
            tx.commit();
            return updated;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    // ───────────────────────────────────────────
    // DELETE — elimina un fornitore per ID
    // ───────────────────────────────────────────
    public boolean delete(int id) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Supplier supplier = em.find(Supplier.class, id);
            if (supplier == null) {
                tx.rollback();
                return false; // ID non trovato
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
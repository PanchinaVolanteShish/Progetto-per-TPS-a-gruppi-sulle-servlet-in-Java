package com.mycompany.northwindmanager;

import jakarta.persistence.*;
import java.util.List;

public class TestMattissimo {

    public static void main(String[] args) {
        EntityManager em = HibernateUtil.getEntityManager();
        List<Supplier> lista = em.createQuery(
            "FROM Supplier", Supplier.class).getResultList();
        for (Supplier s : lista) {
            System.out.println(s.getCompanyName());
        }
        em.close();
        HibernateUtil.shutdown();
    }
}
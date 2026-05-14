package com.mycompany.northwindmanager;

import jakarta.persistence.*;

public class HibernateUtil {

    private static final EntityManagerFactory emf =
        Persistence.createEntityManagerFactory("northwindPU");

    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
        public static EntityManagerFactory getEntityManagerFactory() {
        return emf;
    }

    public static void shutdown() {
        emf.close();
    }
}
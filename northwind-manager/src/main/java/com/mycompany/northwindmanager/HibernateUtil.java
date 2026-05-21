package com.mycompany.northwindmanager;

import jakarta.persistence.*;

public class HibernateUtil {

    //azione eseguita solo una volta, al caricamnto
    private static final EntityManagerFactory emf =
        Persistence.createEntityManagerFactory("northwindPU");
        //serve a capire a quale db connettersi

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
package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.util;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class JPAUtil {

    private static final String PERSISTENCE_UNIT_NAME = "MasterAnnonce";
    private static EntityManagerFactory entityManagerFactory;

    // Bloc statique pour initialiser l'EMF au chargement de la classe
    static {
        try {
            entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
            System.out.println(" EntityManagerFactory créée avec succès");
        } catch (Exception e) {
            System.err.println(" Erreur lors de la création de l'EntityManagerFactory");
            e.printStackTrace();
            throw new ExceptionInInitializerError(e);
        }
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    public static EntityManager getEntityManager() {
        if (entityManagerFactory == null) {
            throw new IllegalStateException("EntityManagerFactory n'est pas initialisée");
        }
        return entityManagerFactory.createEntityManager();
    }

    public static void shutdown() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
            System.out.println("✅ EntityManagerFactory fermée");
        }
    }

    public static void executeInTransaction(TransactionCallback callback) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            callback.execute(em);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Erreur lors de l'exécution de la transaction", e);
        } finally {
            em.close();
        }
    }

    @FunctionalInterface
    public interface TransactionCallback {
        void execute(EntityManager em);
    }
}
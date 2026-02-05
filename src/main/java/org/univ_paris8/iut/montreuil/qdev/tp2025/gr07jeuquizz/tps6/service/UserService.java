package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.service;



import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.DAO.UserDAO;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.model.User;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.util.JPAUtil;

import javax.persistence.EntityManager;
import java.util.Optional;

public class UserService {

    /**
     * Créer un utilisateur
     * Validation : username et email uniques
     */
    public User createUser(String username, String email, String password) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();

            UserDAO userDAO = new UserDAO(em);

            // Validation métier
            if (userDAO.existsByUsername(username)) {
                throw new IllegalArgumentException("Ce nom d'utilisateur existe déjà");
            }

            if (userDAO.existsByEmail(email)) {
                throw new IllegalArgumentException("Cet email est déjà utilisé");
            }

            // Création
            User user = new User(username, email, password);
            userDAO.save(user);

            em.getTransaction().commit();
            return user;

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Erreur lors de la création de l'utilisateur", e);
        } finally {
            em.close();
        }
    }

    /**
     * Authentification
     */
    public Optional<User> authenticate(String username, String password) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            UserDAO userDAO = new UserDAO(em);
            return userDAO.authenticate(username, password);
        } finally {
            em.close();
        }
    }

    /**
     * Récupérer un utilisateur par ID
     */
    public Optional<User> getUserById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            UserDAO userDAO = new UserDAO(em);
            return userDAO.findById(id);
        } finally {
            em.close();
        }
    }
}

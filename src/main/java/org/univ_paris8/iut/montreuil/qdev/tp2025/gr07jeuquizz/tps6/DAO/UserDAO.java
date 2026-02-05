package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.DAO;



import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.model.User;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.Optional;

/**
 * DAO pour l'entité User
 * Hérite de GenericDAO et ajoute des méthodes spécifiques
 */
public class UserDAO extends GenericDAO<User> {

    /**
     * Constructeur
     * @param em EntityManager fourni par le Service
     */
    public UserDAO(EntityManager em) {
        super(em, User.class);
    }

    /**
     * Trouver un utilisateur par son username (JPQL)
     */
    public Optional<User> findByUsername(String username) {
        try {
            User user = em.createQuery(
                            "SELECT u FROM User u WHERE u.username = :username",
                            User.class)
                    .setParameter("username", username)
                    .getSingleResult();
            return Optional.of(user);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    /**
     * Trouver un utilisateur par son email (JPQL)
     */
    public Optional<User> findByEmail(String email) {
        try {
            User user = em.createQuery(
                            "SELECT u FROM User u WHERE u.email = :email",
                            User.class)
                    .setParameter("email", email)
                    .getSingleResult();
            return Optional.of(user);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    /**
     * Vérifier si un username existe déjà
     */
    public boolean existsByUsername(String username) {
        Long count = em.createQuery(
                        "SELECT COUNT(u) FROM User u WHERE u.username = :username",
                        Long.class)
                .setParameter("username", username)
                .getSingleResult();
        return count > 0;
    }

    /**
     * Vérifier si un email existe déjà
     */
    public boolean existsByEmail(String email) {
        Long count = em.createQuery(
                        "SELECT COUNT(u) FROM User u WHERE u.email = :email",
                        Long.class)
                .setParameter("email", email)
                .getSingleResult();
        return count > 0;
    }

    public Optional<User> authenticate(String username, String password) {
        try {
            User user = em.createQuery(
                            "SELECT u FROM User u WHERE u.username = :username AND u.password = :password",
                            User.class)
                    .setParameter("username", username)
                    .setParameter("password", password)
                    .getSingleResult();
            return Optional.of(user);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}

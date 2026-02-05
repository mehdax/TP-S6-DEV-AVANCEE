package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.DAO;


import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.model.Annonce;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.model.AnnonceStatus;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.model.Category;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.model.User;

import javax.persistence.EntityManager;
import java.util.List;

public class AnnonceDAO extends GenericDAO<Annonce> {

    public AnnonceDAO(EntityManager em) {
        super(em, Annonce.class);
    }

    /**
     * Trouver toutes les annonces par statut
     */
    public List<Annonce> findByStatus(AnnonceStatus status) {
        return em.createQuery(
                        "SELECT a FROM Annonce a WHERE a.status = :status ORDER BY a.date DESC",
                        Annonce.class)
                .setParameter("status", status)
                .getResultList();
    }

    /**
     * Trouver les annonces d'un utilisateur
     */
    public List<Annonce> findByAuthor(User author) {
        return em.createQuery(
                        "SELECT a FROM Annonce a WHERE a.author = :author ORDER BY a.date DESC",
                        Annonce.class)
                .setParameter("author", author)
                .getResultList();
    }




    /**
     * Trouver les annonces d'une catégorie
     */
    public List<Annonce> findByCategory(Category category) {
        return em.createQuery(
                        "SELECT a FROM Annonce a WHERE a.category = :category ORDER BY a.date DESC",
                        Annonce.class)
                .setParameter("category", category)
                .getResultList();
    }

    /**
     * Recherche par mot-clé (titre OU description)
     */
    public List<Annonce> searchByKeyword(String keyword) {
        return em.createQuery(
                        "SELECT a FROM Annonce a WHERE " +
                                "LOWER(a.title) LIKE LOWER(:keyword) OR " +
                                "LOWER(a.description) LIKE LOWER(:keyword) " +
                                "ORDER BY a.date DESC",
                        Annonce.class)
                .setParameter("keyword", "%" + keyword + "%")
                .getResultList();
    }

    /**
     * Recherche avec filtres (catégorie + statut)
     */
    public List<Annonce> findByCategoryAndStatus(Category category, AnnonceStatus status) {
        return em.createQuery(
                        "SELECT a FROM Annonce a WHERE a.category = :category AND a.status = :status " +
                                "ORDER BY a.date DESC",
                        Annonce.class)
                .setParameter("category", category)
                .setParameter("status", status)
                .getResultList();
    }

    /**
     * Récupérer les annonces avec pagination
     */
    public List<Annonce> findAllPaginated(int page, int pageSize) {
        return em.createQuery(
                        "SELECT a FROM Annonce a ORDER BY a.date DESC",
                        Annonce.class)
                .setFirstResult(page * pageSize)
                .setMaxResults(pageSize)
                .getResultList();
    }

    /**
     * Annonces publiées avec pagination
     */
    public List<Annonce> findPublishedPaginated(int page, int pageSize) {
        return em.createQuery(
                        "SELECT a FROM Annonce a WHERE a.status = :status ORDER BY a.date DESC",
                        Annonce.class)
                .setParameter("status", AnnonceStatus.PUBLISHED)
                .setFirstResult(page * pageSize)
                .setMaxResults(pageSize)
                .getResultList();
    }


}
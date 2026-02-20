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
    public Annonce findByIdWithRelations(Long id) {
        List<Annonce> results = em.createQuery(
                "SELECT DISTINCT a FROM Annonce a LEFT JOIN FETCH a.author LEFT JOIN FETCH a.category WHERE a.id = :id",
                Annonce.class)
                .setParameter("id", id)
                .getResultList();
        return results.isEmpty() ? null : results.get(0);
    }
    public List<Annonce> findByStatus(AnnonceStatus status) {
        return em.createQuery(
                "SELECT DISTINCT a FROM Annonce a LEFT JOIN FETCH a.author LEFT JOIN FETCH a.category WHERE a.status = :status ORDER BY a.date DESC",
                Annonce.class)
                .setParameter("status", status)
                .getResultList();
    }
    public List<Annonce> findByAuthor(User author) {
        return em.createQuery(
                "SELECT a FROM Annonce a WHERE a.author = :author ORDER BY a.date DESC",
                Annonce.class)
                .setParameter("author", author)
                .getResultList();
    }
    public List<Annonce> findByAuthorIdAndStatus(Long authorId, AnnonceStatus status) {
        return em.createQuery(
                "SELECT DISTINCT a FROM Annonce a LEFT JOIN FETCH a.author LEFT JOIN FETCH a.category " +
                        "WHERE a.author.id = :authorId AND a.status = :status ORDER BY a.date DESC",
                Annonce.class)
                .setParameter("authorId", authorId)
                .setParameter("status", status)
                .getResultList();
    }
    public List<Annonce> findByCategory(Category category) {
        return em.createQuery(
                "SELECT a FROM Annonce a WHERE a.category = :category ORDER BY a.date DESC",
                Annonce.class)
                .setParameter("category", category)
                .getResultList();
    }
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
    public List<Annonce> findByCategoryAndStatus(Category category, AnnonceStatus status) {
        return em.createQuery(
                "SELECT a FROM Annonce a WHERE a.category = :category AND a.status = :status " +
                        "ORDER BY a.date DESC",
                Annonce.class)
                .setParameter("category", category)
                .setParameter("status", status)
                .getResultList();
    }
    public List<Annonce> findAllPaginated(int page, int pageSize) {
        return em.createQuery(
                "SELECT DISTINCT a FROM Annonce a LEFT JOIN FETCH a.author LEFT JOIN FETCH a.category ORDER BY a.date DESC",
                Annonce.class)
                .setFirstResult(page * pageSize)
                .setMaxResults(pageSize)
                .getResultList();
    }
    public List<Annonce> findPublishedPaginated(int page, int pageSize) {
        return em.createQuery(
                "SELECT DISTINCT a FROM Annonce a LEFT JOIN FETCH a.author LEFT JOIN FETCH a.category WHERE a.status = :status ORDER BY a.date DESC",
                Annonce.class)
                .setParameter("status", AnnonceStatus.PUBLISHED)
                .setFirstResult(page * pageSize)
                .setMaxResults(pageSize)
                .getResultList();
    }

}

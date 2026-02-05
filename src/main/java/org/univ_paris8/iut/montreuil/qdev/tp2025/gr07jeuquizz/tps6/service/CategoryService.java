package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.service;



import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.DAO.CategoryDAO;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.model.Category;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.util.JPAUtil;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

public class CategoryService {

    /**
     * Créer une catégorie
     */
    public Category createCategory(String label) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();

            CategoryDAO categoryDAO = new CategoryDAO(em);

            // Validation
            if (categoryDAO.existsByLabel(label)) {
                throw new IllegalArgumentException("Cette catégorie existe déjà");
            }

            Category category = new Category(label);
            categoryDAO.save(category);

            em.getTransaction().commit();
            return category;

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Erreur création catégorie", e);
        } finally {
            em.close();
        }
    }

    /**
     * Récupérer toutes les catégories
     */
    public List<Category> getAllCategories() {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            CategoryDAO categoryDAO = new CategoryDAO(em);
            return categoryDAO.findAll();
        } finally {
            em.close();
        }
    }

    /**
     * Récupérer par ID
     */
    public Optional<Category> getCategoryById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            CategoryDAO categoryDAO = new CategoryDAO(em);
            return categoryDAO.findById(id);
        } finally {
            em.close();
        }
    }
}
package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.DAO;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.model.Category;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.Optional;

public class CategoryDAO extends GenericDAO<Category> {

    public CategoryDAO(EntityManager em) {
        super(em, Category.class);
    }
    public Optional<Category> findByLabel(String label) {
        try {
            Category category = em.createQuery(
                            "SELECT c FROM Category c WHERE c.label = :label",
                            Category.class)
                    .setParameter("label", label)
                    .getSingleResult();
            return Optional.of(category);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
    public boolean existsByLabel(String label) {
        Long count = em.createQuery(
                        "SELECT COUNT(c) FROM Category c WHERE c.label = :label",
                        Long.class)
                .setParameter("label", label)
                .getSingleResult();
        return count > 0;
    }
}

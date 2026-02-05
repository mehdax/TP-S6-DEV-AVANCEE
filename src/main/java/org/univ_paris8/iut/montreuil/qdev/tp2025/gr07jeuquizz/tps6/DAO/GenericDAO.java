package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.DAO;


import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

/**
 * DAO générique avec opérations CRUD de base
 * Chaque DAO hérite de cette classe
 *
 * @param <T> Type de l'entité
 */
public abstract class GenericDAO<T> {

    protected EntityManager em;
    protected Class<T> entityClass;

    /**
     * Constructeur
     * @param em EntityManager fourni par le Service
     * @param entityClass Classe de l'entité
     */
    public GenericDAO(EntityManager em, Class<T> entityClass) {
        this.em = em;
        this.entityClass = entityClass;
    }

    /**
     * Sauvegarder (INSERT)
     * L'EntityManager doit avoir une transaction active !
     */
    public void save(T entity) {
        em.persist(entity);
    }

    /**
     * Trouver par ID (SELECT)
     */
    public Optional<T> findById(Long id) {
        T entity = em.find(entityClass, id);
        return Optional.ofNullable(entity);
    }

    /**
     * Récupérer tous les éléments (SELECT ALL)
     */
    public List<T> findAll() {
        String jpql = "SELECT e FROM " + entityClass.getSimpleName() + " e";
        return em.createQuery(jpql, entityClass).getResultList();
    }

    /**
     * Mettre à jour (UPDATE)
     * L'EntityManager doit avoir une transaction active !
     */
    public T update(T entity) {
        return em.merge(entity);
    }

    /**
     * Supprimer (DELETE)
     * L'EntityManager doit avoir une transaction active !
     */
    public void delete(T entity) {
        // Si l'entité n'est pas managée, on la fusionne d'abord
        if (!em.contains(entity)) {
            entity = em.merge(entity);
        }
        em.remove(entity);
    }

    /**
     * Supprimer par ID
     */
    public void deleteById(Long id) {
        findById(id).ifPresent(this::delete);
    }

    /**
     * Compter le nombre total d'entités
     */
    public long count() {
        String jpql = "SELECT COUNT(e) FROM " + entityClass.getSimpleName() + " e";
        return em.createQuery(jpql, Long.class).getSingleResult();
    }
}
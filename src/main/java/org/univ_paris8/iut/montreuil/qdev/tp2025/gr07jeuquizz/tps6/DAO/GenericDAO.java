package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.DAO;
import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
public abstract class GenericDAO<T> {

    protected EntityManager em;
    protected Class<T> entityClass;
    public GenericDAO(EntityManager em, Class<T> entityClass) {
        this.em = em;
        this.entityClass = entityClass;
    }
    public void save(T entity) {
        em.persist(entity);
    }
    public Optional<T> findById(Long id) {
        T entity = em.find(entityClass, id);
        return Optional.ofNullable(entity);
    }
    public List<T> findAll() {
        String jpql = "SELECT e FROM " + entityClass.getSimpleName() + " e";
        return em.createQuery(jpql, entityClass).getResultList();
    }
    public T update(T entity) {
        return em.merge(entity);
    }
    public void delete(T entity) {
        
        if (!em.contains(entity)) {
            entity = em.merge(entity);
        }
        em.remove(entity);
    }
    public void deleteById(Long id) {
        findById(id).ifPresent(this::delete);
    }
    public long count() {
        String jpql = "SELECT COUNT(e) FROM " + entityClass.getSimpleName() + " e";
        return em.createQuery(jpql, Long.class).getSingleResult();
    }
}

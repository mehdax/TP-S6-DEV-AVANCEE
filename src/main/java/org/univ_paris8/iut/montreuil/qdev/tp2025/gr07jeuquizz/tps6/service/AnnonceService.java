package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.service;

import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.DAO.*;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.exception.AnnonceNotFoundException;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.exception.BusinessRuleException;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.exception.ForbiddenException;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.model.*;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.util.JPAUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

/**
 * Service métier pour les annonces.
 * Contient toute la logique TP3 (règles métier avancées, Exercice 7).
 */
public class AnnonceService {

    private static final Logger logger = LoggerFactory.getLogger(AnnonceService.class);

    /**
     * Créer une annonce (statut DRAFT par défaut).
     */
    public Annonce createAnnonce(String title, String description, String adress,
            String mail, Long authorId, Long categoryId) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();

            User author = new UserDAO(em).findById(authorId)
                    .orElseThrow(() -> new AnnonceNotFoundException(authorId));
            Category category = new CategoryDAO(em).findById(categoryId)
                    .orElseThrow(() -> new BusinessRuleException("Catégorie introuvable : " + categoryId));

            Annonce annonce = new Annonce();
            annonce.setTitle(title);
            annonce.setDescription(description);
            annonce.setAdress(adress);
            annonce.setMail(mail);
            annonce.setAuthor(author);
            annonce.setCategory(category);
            // Statut DRAFT par défaut (pas directement PUBLISHED comme dans TP2)

            new AnnonceDAO(em).save(annonce);
            em.getTransaction().commit();
            logger.info("Annonce créée : title={}, authorId={}", title, authorId);
            return annonce;

        } catch (Exception e) {
            rollback(em);
            if (e instanceof AnnonceNotFoundException || e instanceof BusinessRuleException
                    || e instanceof ForbiddenException)
                throw e;
            throw new RuntimeException("Erreur création annonce", e);
        } finally {
            em.close();
        }
    }

    /**
     * Publier une annonce (DRAFT → PUBLISHED).
     */
    public void publishAnnonce(Long annonceId, Long requesterId) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();

            Annonce annonce = findOrThrow(em, annonceId);
            checkAuthor(annonce, requesterId);

            if (annonce.getStatus() != AnnonceStatus.DRAFT) {
                throw new BusinessRuleException("Seules les annonces en brouillon peuvent être publiées.");
            }

            annonce.publish();
            new AnnonceDAO(em).update(annonce);
            em.getTransaction().commit();
            logger.info("Annonce {} publiée", annonceId);

        } catch (Exception e) {
            rollback(em);
            rethrowDomainException(e);
        } finally {
            em.close();
        }
    }

    /**
     * Archiver une annonce (PUBLISHED → ARCHIVED).
     */
    public void archiveAnnonce(Long annonceId, Long requesterId) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();

            Annonce annonce = findOrThrow(em, annonceId);
            checkAuthor(annonce, requesterId);

            if (annonce.getStatus() != AnnonceStatus.PUBLISHED) {
                throw new BusinessRuleException("Seules les annonces publiées peuvent être archivées.");
            }

            annonce.archive();
            new AnnonceDAO(em).update(annonce);
            em.getTransaction().commit();
            logger.info("Annonce {} archivée", annonceId);

        } catch (Exception e) {
            rollback(em);
            rethrowDomainException(e);
        } finally {
            em.close();
        }
    }

    /**
     * Modifier complètement une annonce (PUT).
     * Règles : seul l'auteur peut modifier, une annonce PUBLISHED est verrouillée.
     */
    public Annonce updateAnnonce(Long annonceId, String title, String description,
            String adress, String mail, Long categoryId, Long requesterId) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();

            Annonce annonce = findOrThrow(em, annonceId);
            checkAuthor(annonce, requesterId);
            checkNotPublished(annonce);

            Category category = new CategoryDAO(em).findById(categoryId)
                    .orElseThrow(() -> new BusinessRuleException("Catégorie introuvable : " + categoryId));

            annonce.setTitle(title);
            annonce.setDescription(description);
            annonce.setAdress(adress);
            annonce.setMail(mail);
            annonce.setCategory(category);

            Annonce updated = new AnnonceDAO(em).update(annonce);
            em.getTransaction().commit();
            logger.info("Annonce {} mise à jour", annonceId);
            return updated;

        } catch (Exception e) {
            rollback(em);
            rethrowDomainException(e);
            return null; // unreachable
        } finally {
            em.close();
        }
    }

    /**
     * Mise à jour partielle (PATCH) – seuls les champs non-null sont appliqués.
     */
    public Annonce patchAnnonce(Long annonceId, String title, String description,
            String adress, String mail, Long categoryId, Long requesterId) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();

            Annonce annonce = findOrThrow(em, annonceId);
            checkAuthor(annonce, requesterId);
            checkNotPublished(annonce);

            if (title != null)
                annonce.setTitle(title);
            if (description != null)
                annonce.setDescription(description);
            if (adress != null)
                annonce.setAdress(adress);
            if (mail != null)
                annonce.setMail(mail);

            if (categoryId != null) {
                Category category = new CategoryDAO(em).findById(categoryId)
                        .orElseThrow(() -> new BusinessRuleException("Catégorie introuvable : " + categoryId));
                annonce.setCategory(category);
            }

            Annonce updated = new AnnonceDAO(em).update(annonce);
            em.getTransaction().commit();
            logger.info("Annonce {} mise à jour partiellement (PATCH)", annonceId);
            return updated;

        } catch (Exception e) {
            rollback(em);
            rethrowDomainException(e);
            return null;
        } finally {
            em.close();
        }
    }

    /**
     * Supprimer une annonce.
     * Règles : seul l'auteur, et seulement si ARCHIVED (archivage obligatoire avant
     * suppression).
     */
    public void deleteAnnonce(Long annonceId, Long requesterId) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();

            Annonce annonce = findOrThrow(em, annonceId);
            checkAuthor(annonce, requesterId);

            if (annonce.getStatus() != AnnonceStatus.ARCHIVED) {
                throw new BusinessRuleException(
                        "L'annonce doit être archivée avant d'être supprimée. " +
                                "Statut actuel : " + annonce.getStatus());
            }

            new AnnonceDAO(em).deleteById(annonceId);
            em.getTransaction().commit();
            logger.info("Annonce {} supprimée", annonceId);

        } catch (Exception e) {
            rollback(em);
            rethrowDomainException(e);
        } finally {
            em.close();
        }
    }

    /**
     * Liste paginée de toutes les annonces (admin) ou publiées (public).
     */
    public List<Annonce> getAllAnnonces(int page, int pageSize) {
        EntityManager em = getEntityManager();
        try {
            return new AnnonceDAO(em).findAllPaginated(page, pageSize);
        } finally {
            em.close();
        }
    }

    /**
     * Nombre total d'annonces (pour pagination).
     */
    public long countAllAnnonces() {
        EntityManager em = getEntityManager();
        try {
            return new AnnonceDAO(em).count();
        } finally {
            em.close();
        }
    }

    /**
     * Récupérer les annonces publiées (pagination).
     */
    public List<Annonce> getPublishedAnnonces(int page, int pageSize) {
        EntityManager em = getEntityManager();
        try {
            return new AnnonceDAO(em).findPublishedPaginated(page, pageSize);
        } finally {
            em.close();
        }
    }

    /**
     * Recherche par mot-clé.
     */
    public List<Annonce> searchAnnonces(String keyword) {
        EntityManager em = getEntityManager();
        try {
            return new AnnonceDAO(em).searchByKeyword(keyword);
        } finally {
            em.close();
        }
    }

    /**
     * Récupérer une annonce par ID avec ses relations.
     */
    public Optional<Annonce> getAnnonceById(Long id) {
        EntityManager em = getEntityManager();
        try {
            return Optional.ofNullable(new AnnonceDAO(em).findByIdWithRelations(id));
        } finally {
            em.close();
        }
    }

    /**
     * Récupérer les annonces d'un utilisateur.
     */
    public List<Annonce> getUserAnnonces(Long userId) {
        EntityManager em = getEntityManager();
        try {
            User user = new UserDAO(em).findById(userId)
                    .orElseThrow(() -> new BusinessRuleException("Utilisateur introuvable"));
            return new AnnonceDAO(em).findByAuthor(user);
        } finally {
            em.close();
        }
    }

    // ========================
    // Helpers pour les tests (Seam)
    // ========================
    protected EntityManager getEntityManager() {
        return JPAUtil.getEntityManager();
    }

    // ========================
    // Helpers privés
    // ========================

    private Annonce findOrThrow(EntityManager em, Long id) {
        return new AnnonceDAO(em).findByIdWithRelations(id) != null
                ? new AnnonceDAO(em).findByIdWithRelations(id)
                : throwNotFound(id);
    }

    private Annonce throwNotFound(Long id) {
        throw new AnnonceNotFoundException(id);
    }

    private void checkAuthor(Annonce annonce, Long requesterId) {
        if (annonce.getAuthor() == null || !annonce.getAuthor().getId().equals(requesterId)) {
            throw new ForbiddenException("Seul l'auteur de l'annonce peut effectuer cette action.");
        }
    }

    private void checkNotPublished(Annonce annonce) {
        if (annonce.getStatus() == AnnonceStatus.PUBLISHED) {
            throw new BusinessRuleException(
                    "Une annonce PUBLISHED ne peut pas être modifiée. " +
                            "Archivez-la d'abord si nécessaire.");
        }
    }

    private void rollback(EntityManager em) {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
    }

    private void rethrowDomainException(Exception e) {
        if (e instanceof AnnonceNotFoundException)
            throw (AnnonceNotFoundException) e;
        if (e instanceof BusinessRuleException)
            throw (BusinessRuleException) e;
        if (e instanceof ForbiddenException)
            throw (ForbiddenException) e;
        throw new RuntimeException(e.getMessage(), e);
    }
}
package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.service;

import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.DAO.*;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.model.*;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.util.JPAUtil;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

public class AnnonceService {

    /**
     * Créer une annonce
     */
    public Annonce createAnnonce(String title, String description, String adress,
                                 String mail, Long authorId, Long categoryId) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();

            AnnonceDAO annonceDAO = new AnnonceDAO(em);
            UserDAO userDAO = new UserDAO(em);
            CategoryDAO categoryDAO = new CategoryDAO(em);

            // Récupérer l'auteur et la catégorie
            User author = userDAO.findById(authorId)
                    .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));

            Category category = categoryDAO.findById(categoryId)
                    .orElseThrow(() -> new IllegalArgumentException("Catégorie introuvable"));

            // Créer l'annonce - directement publiée
            Annonce annonce = new Annonce();
            annonce.setTitle(title);
            annonce.setDescription(description);
            annonce.setAdress(adress);
            annonce.setMail(mail);
            annonce.setAuthor(author);
            annonce.setCategory(category);
            annonce.setStatus(AnnonceStatus.PUBLISHED);  // Publié directement

            annonceDAO.save(annonce);

            em.getTransaction().commit();
            return annonce;

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Erreur création annonce", e);
        } finally {
            em.close();
        }
    }

    /**
     * Publier une annonce
     */
    public void publishAnnonce(Long annonceId) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();

            AnnonceDAO annonceDAO = new AnnonceDAO(em);
            Annonce annonce = annonceDAO.findById(annonceId)
                    .orElseThrow(() -> new IllegalArgumentException("Annonce introuvable"));

            // Logique métier
            if (annonce.getStatus() != AnnonceStatus.DRAFT) {
                throw new IllegalStateException("Seules les annonces en brouillon peuvent être publiées");
            }

            annonce.publish();
            annonceDAO.update(annonce);

            em.getTransaction().commit();

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Erreur publication", e);
        } finally {
            em.close();
        }
    }

    /**
     * Archiver une annonce
     */
    public void archiveAnnonce(Long annonceId) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();

            AnnonceDAO annonceDAO = new AnnonceDAO(em);
            Annonce annonce = annonceDAO.findById(annonceId)
                    .orElseThrow(() -> new IllegalArgumentException("Annonce introuvable"));

            if (annonce.getStatus() != AnnonceStatus.PUBLISHED) {
                throw new IllegalStateException("Seules les annonces publiées peuvent être archivées");
            }

            annonce.archive();
            annonceDAO.update(annonce);

            em.getTransaction().commit();

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Erreur archivage", e);
        } finally {
            em.close();
        }
    }

    /**
     * Modifier une annonce
     */
    public void updateAnnonce(Long annonceId, String title, String description,
                              String adress, String mail, Long categoryId) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();

            AnnonceDAO annonceDAO = new AnnonceDAO(em);
            CategoryDAO categoryDAO = new CategoryDAO(em);

            Annonce annonce = annonceDAO.findById(annonceId)
                    .orElseThrow(() -> new IllegalArgumentException("Annonce introuvable"));

            Category category = categoryDAO.findById(categoryId)
                    .orElseThrow(() -> new IllegalArgumentException("Catégorie introuvable"));

            // Mise à jour
            annonce.setTitle(title);
            annonce.setDescription(description);
            annonce.setAdress(adress);
            annonce.setMail(mail);
            annonce.setCategory(category);

            annonceDAO.update(annonce);

            em.getTransaction().commit();

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Erreur mise à jour", e);
        } finally {
            em.close();
        }
    }

    /**
     * Supprimer une annonce
     */
    public void deleteAnnonce(Long annonceId) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();

            AnnonceDAO annonceDAO = new AnnonceDAO(em);
            annonceDAO.deleteById(annonceId);

            em.getTransaction().commit();

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Erreur suppression", e);
        } finally {
            em.close();
        }
    }

    /**
     * Récupérer les annonces publiées (pagination)
     */
    public List<Annonce> getPublishedAnnonces(int page, int pageSize) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            AnnonceDAO annonceDAO = new AnnonceDAO(em);
            return annonceDAO.findPublishedPaginated(page, pageSize);
        } finally {
            em.close();
        }
    }

    /**
     * Recherche par mot-clé
     */
    public List<Annonce> searchAnnonces(String keyword) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            AnnonceDAO annonceDAO = new AnnonceDAO(em);
            return annonceDAO.searchByKeyword(keyword);
        } finally {
            em.close();
        }
    }

    /**
     * Récupérer une annonce par ID
     */
    public Optional<Annonce> getAnnonceById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            AnnonceDAO annonceDAO = new AnnonceDAO(em);
            Annonce annonce = annonceDAO.findByIdWithRelations(id);
            return Optional.ofNullable(annonce);
        } finally {
            em.close();
        }
    }

    /**
     * Récupérer les annonces d'un utilisateur
     */
    public List<Annonce> getUserAnnonces(Long userId) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            AnnonceDAO annonceDAO = new AnnonceDAO(em);
            UserDAO userDAO = new UserDAO(em);
            User user = userDAO.findById(userId).get();
            return annonceDAO.findByAuthor(user);
        } finally {
            em.close();
        }
    }
}
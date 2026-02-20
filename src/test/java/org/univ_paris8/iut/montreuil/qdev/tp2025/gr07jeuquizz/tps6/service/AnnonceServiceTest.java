package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.DAO.AnnonceDAO;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.model.Annonce;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.model.AnnonceStatus;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Exercice 9 – Tests unitaires de la couche Service
 *
 * Tests unitaires (Mockito) pour AnnonceService.
 * Le service crée son propre EntityManager via JPAUtil, donc on ne peut pas
 * mocker le DAO directement. On teste ici la LOGIQUE MÉTIER directe du
 * modèle Annonce (publish/archive/status transitions) et la couche Service
 * via un EntityManager mocké passé directement aux DAOs.
 *
 * Tag : "unit" → lancés avec : mvn test -P unit-tests
 */
@Tag("unit")
@DisplayName("AnnonceService – Tests unitaires")
@ExtendWith(MockitoExtension.class)
class AnnonceServiceTest {

    // ─── Tests logique métier directe (modèle Annonce) ────────────────

    @Nested
    @DisplayName("Publish – transitions de statut")
    class PublishTests {

        @Test
        @DisplayName("publish() – DRAFT → PUBLISHED (succès)")
        void testPublish_fromDraft_success() {
            Annonce annonce = new Annonce("Titre Test", "Description suffisamment longue", "75001 Paris",
                    "test@test.com");
            annonce.setStatus(AnnonceStatus.DRAFT);

            // ACT
            annonce.publish();

            // ASSERT
            assertEquals(AnnonceStatus.PUBLISHED, annonce.getStatus(),
                    "Une annonce DRAFT doit passer à PUBLISHED après publish()");
        }

        @Test
        @DisplayName("publish() – PUBLISHED → IllegalStateException")
        void testPublish_alreadyPublished_throwsException() {
            Annonce annonce = new Annonce("Titre Test", "Description suffisamment longue", "75001 Paris",
                    "test@test.com");
            annonce.setStatus(AnnonceStatus.PUBLISHED);

            // ACT + ASSERT
            IllegalStateException exception = assertThrows(
                    IllegalStateException.class,
                    () -> annonce.publish(),
                    "Publier une annonce déjà publiée doit lever une IllegalStateException");

            assertTrue(exception.getMessage().contains("brouillon"),
                    "Le message d'erreur doit mentionner 'brouillon'");
        }

        @Test
        @DisplayName("publish() – ARCHIVED → IllegalStateException")
        void testPublish_archived_throwsException() {
            Annonce annonce = new Annonce("Titre Test", "Description suffisamment longue", "75001 Paris",
                    "test@test.com");
            annonce.setStatus(AnnonceStatus.ARCHIVED);

            assertThrows(IllegalStateException.class, () -> annonce.publish(),
                    "Publier une annonce archivée doit lever une IllegalStateException");
        }
    }

    @Nested
    @DisplayName("Archive – transitions de statut")
    class ArchiveTests {

        @Test
        @DisplayName("archive() – PUBLISHED → ARCHIVED (succès)")
        void testArchive_fromPublished_success() {
            Annonce annonce = new Annonce("Titre Test", "Description suffisamment longue", "75001 Paris",
                    "test@test.com");
            annonce.setStatus(AnnonceStatus.PUBLISHED);

            // ACT
            annonce.archive();

            // ASSERT
            assertEquals(AnnonceStatus.ARCHIVED, annonce.getStatus(),
                    "Une annonce PUBLISHED doit passer à ARCHIVED après archive()");
        }

        @Test
        @DisplayName("archive() – DRAFT → IllegalStateException")
        void testArchive_fromDraft_throwsException() {
            Annonce annonce = new Annonce("Titre Test", "Description suffisamment longue", "75001 Paris",
                    "test@test.com");
            annonce.setStatus(AnnonceStatus.DRAFT);

            IllegalStateException exception = assertThrows(
                    IllegalStateException.class,
                    () -> annonce.archive(),
                    "Archiver une annonce en brouillon doit lever une IllegalStateException");

            assertTrue(exception.getMessage().contains("publiées"),
                    "Le message d'erreur doit mentionner 'publiées'");
        }

        @Test
        @DisplayName("archive() – ARCHIVED → IllegalStateException")
        void testArchive_alreadyArchived_throwsException() {
            Annonce annonce = new Annonce("Titre Test", "Description suffisamment longue", "75001 Paris",
                    "test@test.com");
            annonce.setStatus(AnnonceStatus.ARCHIVED);

            assertThrows(IllegalStateException.class, () -> annonce.archive(),
                    "Archiver une annonce déjà archivée doit lever une IllegalStateException");
        }
    }

    // ─── Tests couche DAO via EntityManager mocké ────────────────────

    @Nested
    @DisplayName("AnnonceDAO – opérations via mock EntityManager")
    class DAOMockTests {

        @Mock
        private EntityManager em;

        @Mock
        private EntityTransaction transaction;

        @Test
        @DisplayName("save() – appelle em.persist()")
        void testSave_callsPersist() {
            AnnonceDAO dao = new AnnonceDAO(em);
            Annonce annonce = new Annonce("Titre", "Description longue de test", "Paris", "test@mail.com");

            // ACT
            dao.save(annonce);

            // ASSERT
            verify(em, times(1)).persist(annonce);
        }

        @Test
        @DisplayName("findById() – retourne Optional.of(...) si trouvé")
        void testFindById_found() {
            Annonce expected = new Annonce("Titre", "Description longue de test", "Paris", "test@mail.com");
            when(em.find(Annonce.class, 1L)).thenReturn(expected);

            AnnonceDAO dao = new AnnonceDAO(em);

            // ACT
            Optional<Annonce> result = dao.findById(1L);

            // ASSERT
            assertTrue(result.isPresent(), "findById doit retourner un Optional non vide");
            assertEquals("Titre", result.get().getTitle());
        }

        @Test
        @DisplayName("findById() – retourne Optional.empty() si non trouvé")
        void testFindById_notFound() {
            when(em.find(Annonce.class, 999L)).thenReturn(null);

            AnnonceDAO dao = new AnnonceDAO(em);

            // ACT
            Optional<Annonce> result = dao.findById(999L);

            // ASSERT
            assertFalse(result.isPresent(), "findById doit retourner Optional.empty()");
        }

        @Test
        @DisplayName("update() – appelle em.merge()")
        void testUpdate_callsMerge() {
            Annonce annonce = new Annonce("Titre", "Description longue de test", "Paris", "test@mail.com");
            when(em.merge(annonce)).thenReturn(annonce);

            AnnonceDAO dao = new AnnonceDAO(em);

            // ACT
            Annonce result = dao.update(annonce);

            // ASSERT
            verify(em, times(1)).merge(annonce);
            assertNotNull(result);
        }

        @Test
        @DisplayName("delete() – appelle em.remove() sur entité managée")
        void testDelete_callsRemove() {
            Annonce annonce = new Annonce("Titre", "Description longue de test", "Paris", "test@mail.com");
            when(em.contains(annonce)).thenReturn(true);

            AnnonceDAO dao = new AnnonceDAO(em);

            // ACT
            dao.delete(annonce);

            // ASSERT
            verify(em, times(1)).remove(annonce);
        }

        @Test
        @DisplayName("delete() – merge avant remove si entité détachée")
        void testDelete_mergeBeforeRemove_whenDetached() {
            Annonce annonce = new Annonce("Titre", "Description longue de test", "Paris", "test@mail.com");
            Annonce merged = new Annonce("Titre", "Description longue de test", "Paris", "test@mail.com");

            when(em.contains(annonce)).thenReturn(false);
            when(em.merge(annonce)).thenReturn(merged);

            AnnonceDAO dao = new AnnonceDAO(em);

            // ACT
            dao.delete(annonce);

            // ASSERT
            verify(em, times(1)).merge(annonce);
            verify(em, times(1)).remove(merged);
        }
    }

    // ─── Tests de validation du modèle ──────────────────────────────

    @Nested
    @DisplayName("Modèle Annonce – comportement")
    class AnnonceModelTests {

        @Test
        @DisplayName("isPublished() – retourne true si PUBLISHED")
        void testIsPublished_true() {
            Annonce annonce = new Annonce();
            annonce.setStatus(AnnonceStatus.PUBLISHED);

            assertTrue(annonce.isPublished());
        }

        @Test
        @DisplayName("isPublished() – retourne false si DRAFT")
        void testIsPublished_falseForDraft() {
            Annonce annonce = new Annonce();
            annonce.setStatus(AnnonceStatus.DRAFT);

            assertFalse(annonce.isPublished());
        }

        @Test
        @DisplayName("Constructeur avec paramètres – initialise correctement les champs")
        void testConstructorWithParams() {
            Annonce annonce = new Annonce("Mon titre", "Ma description longue", "75001 Paris", "test@test.com");

            assertEquals("Mon titre", annonce.getTitle());
            assertEquals("Ma description longue", annonce.getDescription());
            assertEquals("75001 Paris", annonce.getAdress());
            assertEquals("test@test.com", annonce.getMail());
        }

        @Test
        @DisplayName("setStatus + getStatus – fonctionne correctement")
        void testSetGetStatus() {
            Annonce annonce = new Annonce();

            annonce.setStatus(AnnonceStatus.DRAFT);
            assertEquals(AnnonceStatus.DRAFT, annonce.getStatus());

            annonce.setStatus(AnnonceStatus.PUBLISHED);
            assertEquals(AnnonceStatus.PUBLISHED, annonce.getStatus());

            annonce.setStatus(AnnonceStatus.ARCHIVED);
            assertEquals(AnnonceStatus.ARCHIVED, annonce.getStatus());
        }
    }
}

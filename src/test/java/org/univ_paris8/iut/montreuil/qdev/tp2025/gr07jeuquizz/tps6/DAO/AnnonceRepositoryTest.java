package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.DAO;

import org.junit.jupiter.api.*;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.model.Annonce;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.model.AnnonceStatus;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.model.Category;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.model.User;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Exercice 8 – Tests Repository (intégration)
 *
 * Tests d'intégration JPA avec la base H2 In-Memory (persistence unit
 * "MasterAnnonceTest").
 * Ces tests vérifient le comportement réel du DAO via JPA/Hibernate sans
 * dépendre de PostgreSQL.
 *
 * Tag : "integration" → lancés avec : mvn test -P integration-tests
 *
 * Stratégie de fixtures :
 * - @BeforeAll : création de l'EntityManagerFactory H2
 * - @BeforeEach : chargement du jeu de données (1 User, 1 Category, 5 Annonces)
 * - @AfterEach : purge des données pour garantir l'isolation des tests
 * - @AfterAll : fermeture de l'EntityManagerFactory
 */
@Tag("integration")
@DisplayName("AnnonceDAO – Tests d'intégration avec H2")
class AnnonceRepositoryTest {

    private static EntityManagerFactory emf;
    private EntityManager em;
    private AnnonceDAO annonceDAO;

    // ─── Entités de fixture ───────────────────────────────────────────────
    private User testUser;
    private Category testCategory;

    // ─── Lifecycle ────────────────────────────────────────────────────────

    @BeforeAll
    static void setUpFactory() {
        // Utilise la persistence unit H2 définie dans
        // src/test/resources/META-INF/persistence.xml
        emf = Persistence.createEntityManagerFactory("MasterAnnonceTest");
    }

    @AfterAll
    static void tearDownFactory() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }

    @BeforeEach
    void setUp() {
        em = emf.createEntityManager();
        annonceDAO = new AnnonceDAO(em);
        loadFixtures();
    }

    @AfterEach
    void tearDown() {
        purgeData();
        if (em != null && em.isOpen()) {
            em.close();
        }
    }

    // ─── Chargement du jeu de données ────────────────────────────────────

    /**
     * Charge un jeu de données de référence avant chaque test :
     * - 1 utilisateur : "alice"
     * - 1 catégorie : "Informatique"
     * - 5 annonces : 2 DRAFT, 2 PUBLISHED, 1 ARCHIVED
     */
    private void loadFixtures() {
        em.getTransaction().begin();

        // Créer l'utilisateur de test
        testUser = new User("alice", "alice@test.com", "password123");
        em.persist(testUser);

        // Créer la catégorie de test
        testCategory = new Category("Informatique");
        em.persist(testCategory);

        // Créer 5 annonces
        persistAnnonce("Annonce DRAFT 1", "Description longue pour la première annonce draft", AnnonceStatus.DRAFT);
        persistAnnonce("Annonce DRAFT 2", "Description longue pour la deuxième annonce draft", AnnonceStatus.DRAFT);
        persistAnnonce("Annonce PUBLISHED 1", "Description longue pour la première publiée", AnnonceStatus.PUBLISHED);
        persistAnnonce("Annonce PUBLISHED 2", "Description longue pour la deuxième publiée", AnnonceStatus.PUBLISHED);
        persistAnnonce("Annonce ARCHIVED 1", "Description longue pour l'annonce archivée", AnnonceStatus.ARCHIVED);

        em.getTransaction().commit();
    }

    /** Helper : crée et persiste une annonce avec le statut donné. */
    private void persistAnnonce(String title, String description, AnnonceStatus status) {
        Annonce a = new Annonce(title, description, "75001 Paris", "contact@test.com");
        a.setStatus(status);
        a.setAuthor(testUser);
        a.setCategory(testCategory);
        em.persist(a);
    }

    /** Purge toutes les entités en fin de test pour garantir l'isolation. */
    private void purgeData() {
        em.getTransaction().begin();
        em.createQuery("DELETE FROM Annonce").executeUpdate();
        em.createQuery("DELETE FROM Category").executeUpdate();
        em.createQuery("DELETE FROM User").executeUpdate();
        em.getTransaction().commit();
    }

    // ─── Exercice 8.1 – Pagination ───────────────────────────────────────

    @Test
    @DisplayName("findAllPaginated – page 0, taille 3 → retourne 3 annonces")
    void testFindAllPaginated_page0_size3() {
        List<Annonce> result = annonceDAO.findAllPaginated(0, 3);

        assertNotNull(result, "La liste ne doit pas être nulle");
        assertEquals(3, result.size(), "La page 0 de taille 3 doit contenir 3 annonces");
    }

    @Test
    @DisplayName("findAllPaginated – page 1, taille 3 → retourne 2 annonces (5 au total)")
    void testFindAllPaginated_page1_size3() {
        List<Annonce> result = annonceDAO.findAllPaginated(1, 3);

        assertNotNull(result);
        assertEquals(2, result.size(), "La page 1 doit contenir les 2 annonces restantes");
    }

    @Test
    @DisplayName("findAllPaginated – page hors limite → liste vide")
    void testFindAllPaginated_outOfRange() {
        List<Annonce> result = annonceDAO.findAllPaginated(99, 10);

        assertNotNull(result);
        assertTrue(result.isEmpty(), "Une page hors limite doit retourner une liste vide");
    }

    // ─── Exercice 8.2 – Annonces publiées avec pagination ────────────────

    @Test
    @DisplayName("findPublishedPaginated – retourne uniquement les annonces PUBLISHED")
    void testFindPublishedPaginated_onlyPublished() {
        List<Annonce> result = annonceDAO.findPublishedPaginated(0, 10);

        assertNotNull(result);
        assertEquals(2, result.size(), "Il doit y avoir exactement 2 annonces PUBLISHED");
        result.forEach(a -> assertEquals(AnnonceStatus.PUBLISHED, a.getStatus(),
                "Toutes les annonces retournées doivent être PUBLISHED"));
    }

    // ─── Exercice 8.3 – Comptage ─────────────────────────────────────────

    @Test
    @DisplayName("count – retourne le nombre total d'annonces")
    void testCount_retourneTotalAnnonces() {
        long count = annonceDAO.count();

        assertEquals(5L, count, "Il doit y avoir 5 annonces au total dans la fixture");
    }

    // ─── Exercice 8.4 – Filtrage par statut ──────────────────────────────

    @Test
    @DisplayName("findByStatus – DRAFT → retourne 2 brouillons")
    void testFindByStatus_DRAFT() {
        List<Annonce> drafts = annonceDAO.findByStatus(AnnonceStatus.DRAFT);

        assertNotNull(drafts);
        assertEquals(2, drafts.size(), "Il doit y avoir 2 annonces DRAFT");
    }

    @Test
    @DisplayName("findByStatus – ARCHIVED → retourne 1 archivée")
    void testFindByStatus_ARCHIVED() {
        List<Annonce> archived = annonceDAO.findByStatus(AnnonceStatus.ARCHIVED);

        assertNotNull(archived);
        assertEquals(1, archived.size(), "Il doit y avoir 1 annonce ARCHIVED");
    }

    // ─── Exercice 8.5 – Sauvegarde et recherche ──────────────────────────

    @Test
    @DisplayName("save + findById – une annonce persistée est récupérable par ID")
    void testSaveAndFindById() {
        em.getTransaction().begin();
        Annonce newAnnonce = new Annonce("Titre test", "Description suffisamment longue", "13001 Marseille",
                "test@example.com");
        newAnnonce.setStatus(AnnonceStatus.DRAFT);
        newAnnonce.setAuthor(testUser);
        newAnnonce.setCategory(testCategory);
        annonceDAO.save(newAnnonce);
        em.getTransaction().commit();

        Long id = (long) newAnnonce.getId();
        em.clear(); // Détacher pour forcer un vrai SELECT

        Annonce found = annonceDAO.findById(id).orElse(null);

        assertNotNull(found, "L'annonce doit être retrouvée par son ID");
        assertEquals("Titre test", found.getTitle());
        assertEquals(AnnonceStatus.DRAFT, found.getStatus());
    }

    @Test
    @DisplayName("findByIdWithRelations – charge author et category via JOIN FETCH")
    void testFindByIdWithRelations_chargeRelations() {
        em.getTransaction().begin();
        Annonce a = new Annonce("Annonce relations", "Description avec relations chargées", "69001 Lyon",
                "lyon@example.com");
        a.setStatus(AnnonceStatus.PUBLISHED);
        a.setAuthor(testUser);
        a.setCategory(testCategory);
        annonceDAO.save(a);
        em.getTransaction().commit();

        Long id = (long) a.getId();
        em.clear();

        Annonce found = annonceDAO.findByIdWithRelations(id);

        assertNotNull(found);
        assertNotNull(found.getAuthor(), "L'auteur doit être chargé (JOIN FETCH)");
        assertNotNull(found.getCategory(), "La catégorie doit être chargée (JOIN FETCH)");
        assertEquals("alice", found.getAuthor().getUsername());
        assertEquals("Informatique", found.getCategory().getLabel());
    }
}

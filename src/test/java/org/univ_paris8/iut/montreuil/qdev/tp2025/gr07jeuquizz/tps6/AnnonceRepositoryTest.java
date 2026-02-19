package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6;

import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.DAO.AnnonceDAO;

import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.model.Annonce;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.model.AnnonceStatus;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.model.Category;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.model.User;
import org.junit.jupiter.api.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests d'intégration du Repository (Exercice 8).
 * Utilise H2 in-memory via persistence-unit "masterannonce-test".
 *
 * Ces tests vérifient le comportement réel des DAOs sans mocker la BDD.
 * Ils sont plus lents que les tests unitaires mais plus fiables.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AnnonceRepositoryTest {

    private static EntityManagerFactory emf;
    private EntityManager em;

    // ==============================================
    // Setup / Teardown
    // ==============================================

    @BeforeAll
    static void setUpFactory() {
        emf = Persistence.createEntityManagerFactory("masterannonce-test");
    }

    @AfterAll
    static void tearDownFactory() {
        if (emf != null && emf.isOpen())
            emf.close();
    }

    @BeforeEach
    void setUp() {
        em = emf.createEntityManager();
        // Chargement du jeu de données (Exercice 8)
        loadDataSet();
    }

    @AfterEach
    void tearDown() {
        if (em.getTransaction().isActive())
            em.getTransaction().rollback();
        // Nettoyage de la BDD après chaque test
        em.getTransaction().begin();
        em.createQuery("DELETE FROM Annonce a").executeUpdate();
        em.createQuery("DELETE FROM User u").executeUpdate();
        em.createQuery("DELETE FROM Category c").executeUpdate();
        em.getTransaction().commit();
        em.close();
    }

    /**
     * Chargement du jeu de données initial (Exercice 8).
     */
    private void loadDataSet() {
        em.getTransaction().begin();

        // Catégories
        Category cat1 = new Category("Immobilier");
        Category cat2 = new Category("Voiture");
        em.persist(cat1);
        em.persist(cat2);

        // Utilisateurs
        User user1 = new User("alice", "alice@test.com", "password123");
        User user2 = new User("bob", "bob@test.com", "password456");
        em.persist(user1);
        em.persist(user2);

        // Annonces
        Annonce a1 = new Annonce();
        a1.setTitle("Appartement Paris");
        a1.setDescription("Bel appartement au coeur de Paris avec vue sur la Seine");
        a1.setAdress("75001 Paris");
        a1.setMail("alice@test.com");
        a1.setAuthor(user1);
        a1.setCategory(cat1);
        a1.setStatus(AnnonceStatus.PUBLISHED);
        em.persist(a1);

        Annonce a2 = new Annonce();
        a2.setTitle("Peugeot 308");
        a2.setDescription("Excellente voiture en très bon état avec faible kilométrage");
        a2.setAdress("69000 Lyon");
        a2.setMail("bob@test.com");
        a2.setAuthor(user2);
        a2.setCategory(cat2);
        a2.setStatus(AnnonceStatus.DRAFT);
        em.persist(a2);

        Annonce a3 = new Annonce();
        a3.setTitle("Studio Lyon");
        a3.setDescription("Studio meublé idéal pour étudiant proche université et transports");
        a3.setAdress("69003 Lyon");
        a3.setMail("alice@test.com");
        a3.setAuthor(user1);
        a3.setCategory(cat1);
        a3.setStatus(AnnonceStatus.ARCHIVED);
        em.persist(a3);

        em.getTransaction().commit();
    }

    // ==============================================
    // Tests CRUD
    // ==============================================

    @Test
    @Order(1)
    @DisplayName("Doit sauvegarder une annonce et la retrouver par ID")
    void testSaveAndFindById() {
        em.getTransaction().begin();

        Category cat = em.createQuery("SELECT c FROM Category c", Category.class).setMaxResults(1).getSingleResult();
        User user = em.createQuery("SELECT u FROM User u", User.class).setMaxResults(1).getSingleResult();

        Annonce annonce = new Annonce();
        annonce.setTitle("Test annonce");
        annonce.setDescription("Description test suffisamment longue");
        annonce.setAdress("75000 Paris");
        annonce.setMail("test@test.com");
        annonce.setAuthor(user);
        annonce.setCategory(cat);

        em.persist(annonce);
        em.getTransaction().commit();

        Long id = annonce.getId();
        assertNotNull(id, "L'ID doit être généré après persist");

        Annonce found = em.find(Annonce.class, id);
        assertNotNull(found, "L'annonce doit être trouvée par ID");
        assertEquals("Test annonce", found.getTitle());
    }

    @Test
    @Order(2)
    @DisplayName("findAll doit retourner toutes les annonces du jeu de données")
    void testFindAll() {
        AnnonceDAO dao = new AnnonceDAO(em);
        List<Annonce> annonces = dao.findAll();
        assertEquals(3, annonces.size(), "Le jeu de données contient 3 annonces");
    }

    @Test
    @Order(3)
    @DisplayName("Pagination : page 0 avec taille 2 doit retourner 2 annonces")
    void testPagination() {
        AnnonceDAO dao = new AnnonceDAO(em);
        List<Annonce> page0 = dao.findAllPaginated(0, 2);
        List<Annonce> page1 = dao.findAllPaginated(1, 2);

        assertEquals(2, page0.size(), "Page 0 doit contenir 2 annonces");
        assertEquals(1, page1.size(), "Page 1 doit contenir 1 annonce");
    }

    @Test
    @Order(4)
    @DisplayName("findPublishedPaginated doit retourner uniquement les published")
    void testFindPublished() {
        AnnonceDAO dao = new AnnonceDAO(em);
        List<Annonce> published = dao.findPublishedPaginated(0, 10);

        assertEquals(1, published.size(), "1 seule annonce est PUBLISHED");
        assertEquals(AnnonceStatus.PUBLISHED, published.get(0).getStatus());
    }

    @Test
    @Order(5)
    @DisplayName("Recherche par mot-clé doit trouver les annonces correspondantes")
    void testSearchByKeyword() {
        AnnonceDAO dao = new AnnonceDAO(em);

        List<Annonce> results = dao.searchByKeyword("Paris");
        assertFalse(results.isEmpty(), "La recherche 'Paris' doit retourner des résultats");

        List<Annonce> noResults = dao.searchByKeyword("xyzxyzxyz_impossible");
        assertTrue(noResults.isEmpty(), "La recherche inexistante doit retourner une liste vide");
    }

    @Test
    @Order(6)
    @DisplayName("Suppression d'une annonce")
    void testDelete() {
        AnnonceDAO dao = new AnnonceDAO(em);

        em.getTransaction().begin();
        List<Annonce> before = dao.findAll();
        Long idToDelete = before.get(0).getId();
        dao.deleteById(idToDelete);
        em.getTransaction().commit();

        List<Annonce> after = dao.findAll();
        assertEquals(before.size() - 1, after.size(), "Il doit y avoir une annonce de moins");
        assertNull(em.find(Annonce.class, idToDelete), "L'annonce supprimée ne doit plus exister");
    }

    @Test
    @Order(7)
    @DisplayName("findByIdWithRelations doit charger author et category")
    void testFindByIdWithRelations() {
        AnnonceDAO dao = new AnnonceDAO(em);
        List<Annonce> all = dao.findAll();

        Annonce withRelations = dao.findByIdWithRelations(all.get(0).getId());
        assertNotNull(withRelations, "L'annonce doit être trouvée");
        assertNotNull(withRelations.getAuthor(), "L'auteur doit être chargé");
        assertNotNull(withRelations.getCategory(), "La catégorie doit être chargée");
    }

    @Test
    @Order(8)
    @DisplayName("count() doit retourner le nombre total d'entités")
    void testCount() {
        AnnonceDAO dao = new AnnonceDAO(em);
        long count = dao.count();
        assertEquals(3, count, "Il doit y avoir 3 annonces dans le jeu de données");
    }
}

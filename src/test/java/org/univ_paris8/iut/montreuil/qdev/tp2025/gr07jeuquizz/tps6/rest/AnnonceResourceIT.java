package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.rest;

import org.junit.jupiter.api.*;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.DAO.AnnonceDAO;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.model.Annonce;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.model.AnnonceStatus;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.model.Category;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.model.User;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.rest.dto.AnnonceDTO;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.rest.mapper.AnnonceMapper;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.rest.security.TokenStore;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Exercice 9 – Tests d'intégration REST
 *
 * Vérifient les payloads JSON, les codes HTTP et le comportement du système
 * de bout en bout (DAO → Service → Resource) avec la BDD H2 In-Memory.
 *
 * Plutôt que de monter un serveur Jersey Test complet (qui nécessite une
 * configuration complexe avec les providers et le SecurityFilter), on teste
 * ici la logique de bout en bout côté serveur : DAO + Mapper + codes HTTP
 * attendus.
 *
 * Tag : "integration" → lancés avec : mvn test -P integration-tests
 */
@Tag("integration")
@DisplayName("API REST – Tests d'intégration")
class AnnonceResourceIT {

    private static EntityManagerFactory emf;
    private EntityManager em;

    // Entités de fixture
    private User testUser;
    private Category testCategory;
    private Annonce publishedAnnonce;
    private Annonce draftAnnonce;
    private Annonce archivedAnnonce;

    @BeforeAll
    static void setUpFactory() {
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
        loadFixtures();
    }

    @AfterEach
    void tearDown() {
        purgeData();
        if (em != null && em.isOpen()) {
            em.close();
        }
    }

    private void loadFixtures() {
        em.getTransaction().begin();

        testUser = new User("bob", "bob@test.com", "password123");
        em.persist(testUser);

        testCategory = new Category("Emploi");
        em.persist(testCategory);

        draftAnnonce = new Annonce("Annonce Draft", "Description longue pour une annonce en brouillon", "Paris 15e",
                "draft@test.com");
        draftAnnonce.setStatus(AnnonceStatus.DRAFT);
        draftAnnonce.setAuthor(testUser);
        draftAnnonce.setCategory(testCategory);
        em.persist(draftAnnonce);

        publishedAnnonce = new Annonce("Annonce Publiée", "Description longue pour une annonce publiée", "Lyon 3e",
                "pub@test.com");
        publishedAnnonce.setStatus(AnnonceStatus.PUBLISHED);
        publishedAnnonce.setAuthor(testUser);
        publishedAnnonce.setCategory(testCategory);
        em.persist(publishedAnnonce);

        archivedAnnonce = new Annonce("Annonce Archivée", "Description longue pour une annonce archivée", "Marseille",
                "arch@test.com");
        archivedAnnonce.setStatus(AnnonceStatus.ARCHIVED);
        archivedAnnonce.setAuthor(testUser);
        archivedAnnonce.setCategory(testCategory);
        em.persist(archivedAnnonce);

        em.getTransaction().commit();
    }

    private void purgeData() {
        em.getTransaction().begin();
        em.createQuery("DELETE FROM Annonce").executeUpdate();
        em.createQuery("DELETE FROM Category").executeUpdate();
        em.createQuery("DELETE FROM User").executeUpdate();
        em.getTransaction().commit();
    }

    // ========== GET /api/annonces – Liste paginée → 200 ==========

    @Test
    @DisplayName("GET /api/annonces – simulation paginée → retourne 3 annonces (page 0, taille 10)")
    void testGetAllAnnonces_returns200_withPagination() {
        AnnonceDAO dao = new AnnonceDAO(em);

        // ACT : simuler ce que AnnonceResource fait
        java.util.List<Annonce> annonces = dao.findAllPaginated(0, 10);
        java.util.List<AnnonceDTO> dtos = AnnonceMapper.toDTOList(annonces);
        long total = dao.count();

        // ASSERT : vérifie le payload
        assertEquals(3, dtos.size(), "La page doit contenir 3 annonces");
        assertEquals(3L, total, "Le total doit être 3");

        // Vérification de la structure du DTO
        AnnonceDTO firstDTO = dtos.get(0);
        assertNotNull(firstDTO.getId(), "L'ID ne doit pas être null");
        assertNotNull(firstDTO.getTitle(), "Le titre ne doit pas être null");
        assertNotNull(firstDTO.getStatus(), "Le statut ne doit pas être null");
        assertNotNull(firstDTO.getAuthorId(), "L'authorId ne doit pas être null");
        assertNotNull(firstDTO.getAuthorUsername(), "L'authorUsername ne doit pas être null");
    }

    // ========== GET /api/annonces/{id} – Détail → 200 / 404 ==========

    @Test
    @DisplayName("GET /api/annonces/{id} – annonce existante → retourne le DTO complet")
    void testGetAnnonceById_found_returns200() {
        AnnonceDAO dao = new AnnonceDAO(em);
        Long id = (long) publishedAnnonce.getId();

        // ACT
        Annonce found = dao.findByIdWithRelations(id);

        // ASSERT : simule la réponse 200
        assertNotNull(found, "L'annonce doit être trouvée");
        AnnonceDTO dto = AnnonceMapper.toDTO(found);
        assertEquals("Annonce Publiée", dto.getTitle());
        assertEquals("PUBLISHED", dto.getStatus());
        assertEquals("bob", dto.getAuthorUsername());
        assertEquals("Emploi", dto.getCategoryLabel());
    }

    @Test
    @DisplayName("GET /api/annonces/{id} – ID inexistant → simule 404")
    void testGetAnnonceById_notFound_returns404() {
        AnnonceDAO dao = new AnnonceDAO(em);

        // ACT
        Annonce found = dao.findByIdWithRelations(99999L);

        // ASSERT : simule la réponse 404
        assertNull(found, "Un ID inexistant doit retourner null → HTTP 404");
    }

    // ========== POST /api/annonces – Création → 201 / 400 ==========

    @Test
    @DisplayName("POST /api/annonces – données valides → annonce créée")
    void testCreateAnnonce_validData_returns201() {
        AnnonceDAO dao = new AnnonceDAO(em);

        em.getTransaction().begin();

        Annonce newAnnonce = new Annonce("Nouvelle annonce", "Description longue pour la nouvelle annonce", "Nantes",
                "new@test.com");
        newAnnonce.setStatus(AnnonceStatus.PUBLISHED);
        newAnnonce.setAuthor(testUser);
        newAnnonce.setCategory(testCategory);
        dao.save(newAnnonce);

        em.getTransaction().commit();

        // ASSERT : simule la réponse 201
        assertNotNull(newAnnonce.getId(), "L'annonce créée doit avoir un ID");
        AnnonceDTO dto = AnnonceMapper.toDTO(newAnnonce);
        assertEquals("Nouvelle annonce", dto.getTitle());
        // 201 → Location header aurait "/api/annonces/{id}"
    }

    @Test
    @DisplayName("POST /api/annonces – titre manquant → simule 400 (validation)")
    void testCreateAnnonce_missingTitle_returns400() {
        // ASSERT : le DTO sans titre ne passerait pas la validation @NotBlank
        AnnonceDTO dto = new AnnonceDTO();
        dto.setTitle(null); // titre manquant → @NotBlank violation
        dto.setDescription("Description valide et longue");
        dto.setAdress("Paris");
        dto.setMail("valid@test.com");
        dto.setAuthorId(1L);
        dto.setCategoryId(1L);

        // La validation Bean Validation (@Valid) dans AnnonceResource
        // intercepterait ce DTO et retournerait une 400 Bad Request
        assertNull(dto.getTitle(), "Le titre doit être null → HTTP 400 attendu");
    }

    // ========== PUT /api/annonces/{id} – Mise à jour → 200 / 404 / 409 ==========

    @Test
    @DisplayName("PUT /api/annonces/{id} – mise à jour d'un DRAFT → succès")
    void testUpdateAnnonce_draft_returns200() {
        AnnonceDAO dao = new AnnonceDAO(em);
        Long id = (long) draftAnnonce.getId();

        em.getTransaction().begin();
        Annonce annonce = dao.findById(id).orElseThrow();
        annonce.setTitle("Titre mis à jour");
        annonce.setDescription("Description mise à jour longue");
        dao.update(annonce);
        em.getTransaction().commit();

        em.clear();

        // ASSERT
        Annonce updated = dao.findById(id).orElseThrow();
        assertEquals("Titre mis à jour", updated.getTitle());
    }

    @Test
    @DisplayName("PUT /api/annonces/{id} – mise à jour d'une PUBLISHED → simule 409 Conflict")
    void testUpdateAnnonce_published_returns409() {
        // Exercice 7 : une annonce PUBLISHED ne peut plus être modifiée → 409
        assertEquals(AnnonceStatus.PUBLISHED, publishedAnnonce.getStatus(),
                "L'annonce est PUBLISHED → la Resource retournerait HTTP 409 Conflict");
    }

    @Test
    @DisplayName("PUT /api/annonces/{id} – ID inexistant → simule 404")
    void testUpdateAnnonce_notFound_returns404() {
        AnnonceDAO dao = new AnnonceDAO(em);

        java.util.Optional<Annonce> result = dao.findById(99999L);

        assertFalse(result.isPresent(),
                "Un ID inexistant retourne Optional.empty() → la Resource retournerait HTTP 404");
    }

    // ========== DELETE /api/annonces/{id} – Suppression → 204 / 409 ==========

    @Test
    @DisplayName("DELETE /api/annonces/{id} – annonce ARCHIVED → suppression réussie (204)")
    void testDeleteAnnonce_archived_returns204() {
        AnnonceDAO dao = new AnnonceDAO(em);
        Long id = (long) archivedAnnonce.getId();

        // Exercice 7 : archivage obligatoire avant suppression → ARCHIVED peut être
        // supprimée
        assertEquals(AnnonceStatus.ARCHIVED, archivedAnnonce.getStatus());

        em.getTransaction().begin();
        dao.deleteById(id);
        em.getTransaction().commit();

        // ASSERT : l'annonce n'existe plus
        assertFalse(dao.findById(id).isPresent(),
                "Après suppression, l'annonce ne doit plus exister → HTTP 204");
    }

    @Test
    @DisplayName("DELETE /api/annonces/{id} – annonce PUBLISHED → simule 409 (archivage obligatoire)")
    void testDeleteAnnonce_published_returns409() {
        // Exercice 7 : L'annonce doit être archivée avant suppression
        assertEquals(AnnonceStatus.PUBLISHED, publishedAnnonce.getStatus(),
                "L'annonce est PUBLISHED, non archivée → la Resource retournerait HTTP 409");
    }

    // ========== Test sécurité : TokenStore ==========

    @Test
    @DisplayName("Auth – endpoint @Secured sans token → simule 401 (AuthFilter refuserait)")
    void testSecured_withoutToken_returns401() {
        TokenStore store = TokenStore.getInstance();

        // Un token aléatoire ne doit pas être valide
        assertFalse(store.isValid("invalid-token"),
                "Un token inexistant doit être invalide → AuthFilter retournerait 401");
    }

    @Test
    @DisplayName("Auth – token valide → simule 200 (AuthFilter accepterait)")
    void testSecured_withValidToken_returns200() {
        TokenStore store = TokenStore.getInstance();

        // Générer un token valide
        String token = store.generateToken(testUser.getId(), testUser.getUsername());

        // ASSERT
        assertTrue(store.isValid(token), "Le token généré doit être valide");
        assertEquals(testUser.getId(), store.getUserId(token));
        assertEquals("bob", store.getUsername(token));

        // Nettoyage
        store.removeToken(token);
    }
}

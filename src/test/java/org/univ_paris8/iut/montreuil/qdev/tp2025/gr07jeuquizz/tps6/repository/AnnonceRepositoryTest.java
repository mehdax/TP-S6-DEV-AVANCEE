package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.repository;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.model.Annonce;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.model.AnnonceStatus;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.model.Category;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("AnnonceRepository – Tests d'intégration avec H2")
class AnnonceRepositoryTest {

    @Autowired
    private AnnonceRepository annonceRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User testUser;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        loadFixtures();
    }

    private void loadFixtures() {
        testUser = User.builder()
                .username("alice")
                .email("alice@test.com")
                .password("password123")
                .build();
        entityManager.persist(testUser);

        testCategory = Category.builder()
                .label("Informatique")
                .build();
        entityManager.persist(testCategory);

        persistAnnonce("Annonce DRAFT 1", "Description longue pour la première annonce draft", AnnonceStatus.DRAFT);
        persistAnnonce("Annonce DRAFT 2", "Description longue pour la deuxième annonce draft", AnnonceStatus.DRAFT);
        persistAnnonce("Annonce PUBLISHED 1", "Description longue pour la première publiée", AnnonceStatus.PUBLISHED);
        persistAnnonce("Annonce PUBLISHED 2", "Description longue pour la deuxième publiée", AnnonceStatus.PUBLISHED);
        persistAnnonce("Annonce ARCHIVED 1", "Description longue pour l'annonce archivée", AnnonceStatus.ARCHIVED);

        entityManager.flush();
    }

    private void persistAnnonce(String title, String description, AnnonceStatus status) {
        Annonce a = Annonce.builder()
                .title(title)
                .description(description)
                .adress("75001 Paris")
                .mail("contact@test.com")
                .status(status)
                .author(testUser)
                .category(testCategory)
                .build();
        entityManager.persist(a);
    }

    @Test
    @DisplayName("findAll – retourne 5 annonces")
    void testFindAll() {
        List<Annonce> result = annonceRepository.findAll();
        assertEquals(5, result.size());
    }

    @Test
    @DisplayName("count – retourne 5")
    void testCount() {
        assertEquals(5L, annonceRepository.count());
    }

    @Test
    @DisplayName("save + findById")
    void testSaveAndFindById() {
        Annonce newAnnonce = Annonce.builder()
                .title("Titre test")
                .description("Description suffisamment longue")
                .adress("13001 Marseille")
                .mail("test@example.com")
                .status(AnnonceStatus.DRAFT)
                .author(testUser)
                .category(testCategory)
                .build();

        Annonce saved = annonceRepository.save(newAnnonce);
        assertNotNull(saved.getId());

        Annonce found = annonceRepository.findById(saved.getId()).orElse(null);
        assertNotNull(found);
        assertEquals("Titre test", found.getTitle());
    }
}

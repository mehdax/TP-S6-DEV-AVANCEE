package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.model.Annonce;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.model.AnnonceStatus;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.model.Category;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.model.User;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.repository.AnnonceRepository;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.repository.CategoryRepository;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnnonceServiceTest {

    @Mock
    private AnnonceRepository annonceRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private AnnonceService annonceService;

    private User testUser;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        testUser = User.builder().id(1L).username("testuser").build();
        testCategory = Category.builder().id(1L).label("Test").build();
    }

    @Test
    void createAnnonce_ShouldSucceed() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(annonceRepository.save(any(Annonce.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        Annonce result = annonceService.createAnnonce(
                "Titre Valid", 
                "Description suffisamment longue pour passer la validation",
                "75000 Paris", 
                "test@test.com", 
                1L, 
                1L
        );

        // Assert
        assertNotNull(result);
        assertEquals("Titre Valid", result.getTitle());
        assertEquals(AnnonceStatus.DRAFT, result.getStatus());
        verify(annonceRepository).save(any(Annonce.class));
    }

    @Test
    void publishAnnonce_ShouldChangeStatus() {
        // Arrange
        Annonce annonce = Annonce.builder().id(1L).status(AnnonceStatus.DRAFT).build();
        when(annonceRepository.findById(1L)).thenReturn(Optional.of(annonce));

        // Act
        annonceService.publishAnnonce(1L);

        // Assert
        assertEquals(AnnonceStatus.PUBLISHED, annonce.getStatus());
        assertNotNull(annonce.getDate());
    }

    @Test
    void archiveAnnonce_ShouldChangeStatus() {
        // Arrange
        Annonce annonce = Annonce.builder().id(1L).status(AnnonceStatus.PUBLISHED).build();
        when(annonceRepository.findById(1L)).thenReturn(Optional.of(annonce));

        // Act
        annonceService.archiveAnnonce(1L);

        // Assert
        assertEquals(AnnonceStatus.ARCHIVED, annonce.getStatus());
    }
}

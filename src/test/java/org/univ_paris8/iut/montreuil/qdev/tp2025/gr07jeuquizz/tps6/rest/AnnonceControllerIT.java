package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.model.Category;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.model.User;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.repository.CategoryRepository;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.repository.UserRepository;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AnnonceControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private User testUser;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .username("annonceuser")
                .email("annonce@test.com")
                .password("password123")
                .build();
        userRepository.save(testUser);

        testCategory = Category.builder()
                .label("IT Integration")
                .build();
        categoryRepository.save(testCategory);
    }

    @Test
    void getAllAnnonces_ShouldBePublic() throws Exception {
        mockMvc.perform(get("/api/annonces")
                .param("page", "0")
                .param("size", "10")
                .param("sortBy", "title")
                .param("direction", "ASC"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "annonceuser")
    void createAnnonce_WithAuth_ShouldSucceed() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("title", "Titre Integration");
        request.put("description", "Une description assez longue pour la validation de test");
        request.put("adress", "Paris");
        request.put("mail", "test@test.com");
        request.put("authorId", testUser.getId());
        request.put("categoryId", testCategory.getId());

        mockMvc.perform(post("/api/annonces")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Titre Integration"));
    }

    @Test
    void createAnnonce_WithoutAuth_ShouldReturn403() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("title", "Titre No Auth");

        mockMvc.perform(post("/api/annonces")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }
}

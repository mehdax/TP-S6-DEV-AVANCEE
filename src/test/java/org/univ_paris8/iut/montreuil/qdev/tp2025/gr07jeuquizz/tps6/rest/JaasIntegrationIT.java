package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.rest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.DAO.UserDAO;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.model.User;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.rest.dto.LoginDTO;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.rest.security.AuthFilter;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.rest.security.JaasConfig;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test d'intégration du flux JAAS complet (Bonus du Bonus).
 *
 * Simule le cycle de vie :
 * 1. Login avec credentials (via AuthResource) -> Token généré via JAAS
 * (DbLoginModule)
 * 2. Accès protégé avec Token (via AuthFilter) -> Identité reconstituée via
 * JAAS (TokenLoginModule)
 */
@Tag("integration")
class JaasIntegrationIT {

    private static EntityManagerFactory emf;
    private EntityManager em;
    private AuthResource authResource;
    private AuthFilter authFilter;

    @BeforeAll
    static void setUpGlobal() {
        // Charger la config JAAS
        new JaasConfig().contextInitialized(null);

        // Créer l'EMF de test (H2) et l'injecter dans JPAUtil pour que DbLoginModule
        // l'utilise
        emf = Persistence.createEntityManagerFactory("MasterAnnonceTest");
        org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.util.JPAUtil.setEntityManagerFactory(emf);
    }

    @BeforeEach
    void setUp() {
        em = emf.createEntityManager();
        authResource = new AuthResource();
        authFilter = new AuthFilter();

        // Créer un user de test
        em.getTransaction().begin();
        User user = new User("jaasUser", "jaas@test.com", "passwordJAAS");
        // Nettoyer si existe déjà
        try {
            em.createQuery("DELETE FROM User u WHERE u.username = 'jaasUser'").executeUpdate();
        } catch (Exception ignored) {
        }
        em.persist(user);
        em.getTransaction().commit();
    }

    @AfterEach
    void tearDown() {
        if (em != null && em.isOpen()) {
            em.close();
        }
    }

    @org.junit.jupiter.api.AfterAll
    static void tearDownGlobal() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }

    @Test
    void testFullJaasFlow() throws Exception {
        // --- ÉTAPE 1 : LOGIN (AuthResource -> DbLoginModule -> Token) ---

        // GIVEN
        LoginDTO loginDTO = new LoginDTO("jaasUser", "passwordJAAS");

        // WHEN
        Response loginResponse = authResource.login(loginDTO);

        // THEN
        assertEquals(200, loginResponse.getStatus());
        Map<String, Object> body = (Map<String, Object>) loginResponse.getEntity();
        String token = (String) body.get("token");
        assertNotNull(token, "Le token doit être généré");
        System.out.println("Token généré par JAAS : " + token);

        // --- ÉTAPE 2 : ACCÈS PROTÉGÉ (AuthFilter -> TokenLoginModule -> Identity) ---

        // GIVEN
        ContainerRequestContext requestContext = mock(ContainerRequestContext.class);
        when(requestContext.getHeaderString(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);

        // WHEN
        authFilter.filter(requestContext);

        // THEN
        // Vérifier que AuthFilter a injecté l'userId et username dans le context
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object> valueCaptor = ArgumentCaptor.forClass(Object.class);

        verify(requestContext, atLeastOnce()).setProperty(keyCaptor.capture(), valueCaptor.capture());

        boolean userIdFound = false;
        boolean usernameFound = false;

        for (int i = 0; i < keyCaptor.getAllValues().size(); i++) {
            String key = keyCaptor.getAllValues().get(i);
            Object value = valueCaptor.getAllValues().get(i);

            if ("userId".equals(key)) {
                assertNotNull(value);
                userIdFound = true;
            }
            if ("username".equals(key)) {
                assertEquals("jaasUser", value);
                usernameFound = true;
            }
        }

        assertTrue(userIdFound, "userId doit être injecté dans le contexte");
        assertTrue(usernameFound, "username doit être injecté dans le contexte");
    }

    @Test
    void testJaasLoginFailure() {
        // GIVEN
        LoginDTO loginDTO = new LoginDTO("jaasUser", "wrongPassword");

        // WHEN
        Response response = authResource.login(loginDTO);

        // THEN
        assertEquals(401, response.getStatus()); // Unauthorized
    }
}

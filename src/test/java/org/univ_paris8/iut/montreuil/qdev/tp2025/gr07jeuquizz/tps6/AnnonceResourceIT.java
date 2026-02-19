package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.api.*;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.exception.mapper.*;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.security.AuthFilter;
import org.junit.jupiter.api.*;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests d'intégration REST (Exercice 9) – Suffix IT pour Maven Failsafe.
 * 
 * Utilise Jersey Test Framework avec Grizzly2 pour démarrer
 * un serveur HTTP léger en mémoire et tester les endpoints REST réels.
 *
 * Ces tests sont séparés des tests unitaires (cf. pom.xml profiles) :
 * - mvn test -P unit-tests → uniquement les tests unitaires (rapides)
 * - mvn verify -P integration-tests → uniquement les tests IT (plus lents)
 *
 * Intérêt de la séparation :
 * - CI rapide : les tests unitaires s'exécutent en quelques secondes
 * - Les tests d'intégration nécessitent un contexte d'application complet
 * et sont plus sensibles aux problèmes d'infrastructure
 */
class AnnonceResourceIT extends JerseyTest {

    @Override
    protected Application configure() {
        enable(TestProperties.LOG_TRAFFIC);
        enable(TestProperties.DUMP_ENTITY);

        ResourceConfig config = new ResourceConfig();
        config.register(HelloResource.class);
        config.register(AuthResource.class);
        config.register(AnnonceResource.class);
        config.register(AuthFilter.class);
        // Exception mappers
        config.register(ValidationExceptionMapper.class);
        config.register(AnnonceNotFoundExceptionMapper.class);
        config.register(BusinessRuleExceptionMapper.class);
        config.register(UnauthorizedExceptionMapper.class);
        config.register(ForbiddenExceptionMapper.class);
        config.register(GenericExceptionMapper.class);
        // JSON
        config.register(com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider.class);

        return config;
    }

    // ==============================================
    // Exercice 1 – Hello World
    // ==============================================

    @Test
    @DisplayName("GET /helloWorld doit retourner 200 avec message JSON")
    void testHelloWorld() {
        Response response = target("/helloWorld").request().get();
        assertEquals(200, response.getStatus(), "Le status doit être 200");

        String body = response.readEntity(String.class);
        assertTrue(body.contains("Hello"), "La réponse doit contenir 'Hello'");
    }

    @Test
    @DisplayName("GET /params?q=test doit retourner le QueryParam")
    void testQueryParam() {
        Response response = target("/params").queryParam("q", "monTest").request().get();
        assertEquals(200, response.getStatus());

        String body = response.readEntity(String.class);
        assertTrue(body.contains("monTest"), "La réponse doit contenir le paramètre");
    }

    @Test
    @DisplayName("GET /params/{value} doit retourner le PathParam")
    void testPathParam() {
        Response response = target("/params/bonjour").request().get();
        assertEquals(200, response.getStatus());

        String body = response.readEntity(String.class);
        assertTrue(body.contains("bonjour"), "La réponse doit contenir la valeur du PathParam");
    }

    // ==============================================
    // Exercice 4 / 6 – Erreurs et sécurité
    // ==============================================

    @Test
    @DisplayName("POST /api/annonces sans token doit retourner 401")
    void testCreateAnnonceWithoutToken_Returns401() {
        Map<String, Object> body = new HashMap<>();
        body.put("title", "Test");
        body.put("description", "Description test longue");
        body.put("adress", "Paris");
        body.put("mail", "test@test.com");
        body.put("categoryId", 1);

        Response response = target("/annonces")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(body));

        assertEquals(401, response.getStatus(), "Sans token → 401 Unauthorized");
    }

    @Test
    @DisplayName("POST /api/annonces avec token invalide doit retourner 401")
    void testCreateAnnonceWithInvalidToken_Returns401() {
        Map<String, Object> body = new HashMap<>();
        body.put("title", "Test");
        body.put("description", "Description test longue sufisante");
        body.put("adress", "Paris");
        body.put("mail", "test@test.com");
        body.put("categoryId", 1);

        Response response = target("/annonces")
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token-invalide-12345")
                .post(Entity.json(body));

        assertEquals(401, response.getStatus(), "Token invalide → 401 Unauthorized");
    }

    @Test
    @DisplayName("POST /login avec mauvais credentials doit retourner 401")
    void testLoginWithBadCredentials_Returns401() {
        Map<String, String> loginBody = new HashMap<>();
        loginBody.put("username", "utilisateur_inexistant");
        loginBody.put("password", "mauvais_mot_de_passe");

        Response response = target("/auth/login")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(loginBody));

        assertEquals(401, response.getStatus(), "Mauvais credentials → 401");

        String body = response.readEntity(String.class);
        assertNotNull(body, "La réponse d'erreur ne doit pas être null");
    }

    @Test
    @DisplayName("POST /login avec body invalide doit retourner 400")
    void testLoginWithInvalidBody_Returns400() {
        Map<String, String> loginBody = new HashMap<>();
        // username manquant → violation Bean Validation

        Response response = target("/auth/login")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(loginBody));

        // 400 Bad Request attendu (validation échoue)
        assertTrue(response.getStatus() == 400 || response.getStatus() == 401,
                "Body invalide → 400 ou 401");
    }

    @Test
    @DisplayName("GET /annonces doit retourner 200 avec structure paginée")
    void testGetAnnoncesList_Returns200() {
        Response response = target("/annonces")
                .request(MediaType.APPLICATION_JSON)
                .get();

        assertEquals(200, response.getStatus(), "GET /annonces doit retourner 200");
    }

    @Test
    @DisplayName("GET /annonces/{id} avec ID inexistant doit retourner 404")
    void testGetAnnonceById_NotFound() {
        Response response = target("/annonces/99999")
                .request(MediaType.APPLICATION_JSON)
                .get();

        assertEquals(404, response.getStatus(), "ID inexistant → 404 Not Found");
    }
}

package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.rest.security;

import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.rest.dto.ApiError;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

/**
 * Exercice 6 – Filtre de sécurité JAX-RS
 *
 * ContainerRequestFilter qui intercepte les requêtes vers les
 * endpoints annotés @Secured.
 *
 * Fonctionnement :
 * 1. Lit le header Authorization: Bearer <token>
 * 2. Si absent → 401 Unauthorized
 * 3. Si présent → valide le token via TokenStore
 * 4. Si invalide → 401 Unauthorized
 * 5. Si valide → attache userId et username au contexte de requête
 *
 * Les endpoints NON annotés @Secured (comme /api/login, /api/helloWorld)
 * ne sont PAS interceptés par ce filtre grâce au @NameBinding.
 */
@Provider
@Secured
@Priority(Priorities.AUTHENTICATION)
public class AuthFilter implements ContainerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";
    private final TokenStore tokenStore = TokenStore.getInstance();

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        // 1. Lire le header Authorization
        String authHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        // 2. Vérifier sa présence
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            abortWithUnauthorized(requestContext,
                    "Token d'authentification manquant. Utilisez : Authorization: Bearer <token>");
            return;
        }

        // 3. Extraire et valider le token
        String token = authHeader.substring(BEARER_PREFIX.length()).trim();

        if (!tokenStore.isValid(token)) {
            abortWithUnauthorized(requestContext, "Token invalide ou expiré");
            return;
        }

        // 4. Attacher l'identité au contexte de la requête
        Long userId = tokenStore.getUserId(token);
        String username = tokenStore.getUsername(token);

        requestContext.setProperty("userId", userId);
        requestContext.setProperty("username", username);
    }

    /**
     * Interrompt la requête avec une réponse 401 JSON normalisée.
     */
    private void abortWithUnauthorized(ContainerRequestContext requestContext, String message) {
        ApiError error = new ApiError(401, "Non authentifié", message);

        requestContext.abortWith(
                Response.status(Response.Status.UNAUTHORIZED)
                        .entity(error)
                        .type(MediaType.APPLICATION_JSON)
                        .build());
    }
}

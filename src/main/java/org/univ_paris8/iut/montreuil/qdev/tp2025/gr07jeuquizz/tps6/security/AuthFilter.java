package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.security;

import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.security.Principal;
import java.util.Optional;

/**
 * Filtre de sécurité JAX-RS (Exercice 6).
 *
 * Intercepte toutes les requêtes marquées @Secured :
 * 1. Lit le header Authorization: Bearer <token>
 * 2. Valide le token via TokenStore
 * 3. Si valide → injecte un SecurityContext dans la requête
 * 4. Si invalide ou absent → répond immédiatement HTTP 401
 */
@Provider
@Secured
public class AuthFilter implements ContainerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(AuthFilter.class);
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String authHeader = requestContext.getHeaderString(AUTHORIZATION_HEADER);

        // Vérification de la présence du header
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            logger.warn("Requête sans token sur endpoint protégé: {}", requestContext.getUriInfo().getPath());
            abortWithUnauthorized(requestContext, "Header 'Authorization: Bearer <token>' manquant.");
            return;
        }

        String token = authHeader.substring(BEARER_PREFIX.length()).trim();

        // Validation du token
        Optional<TokenStore.TokenInfo> tokenInfo = TokenStore.getInstance().validate(token);

        if (tokenInfo.isEmpty()) {
            logger.warn("Token invalide ou expiré");
            abortWithUnauthorized(requestContext, "Token invalide ou expiré. Reconnectez-vous.");
            return;
        }

        // Injection du SecurityContext avec l'identité de l'utilisateur
        final TokenStore.TokenInfo info = tokenInfo.get();
        final SecurityContext originalContext = requestContext.getSecurityContext();

        requestContext.setSecurityContext(new SecurityContext() {
            @Override
            public Principal getUserPrincipal() {
                return info::getUsername;
            }

            @Override
            public boolean isUserInRole(String role) {
                return false; // Rôles non gérés dans la version simple
            }

            @Override
            public boolean isSecure() {
                return originalContext.isSecure();
            }

            @Override
            public String getAuthenticationScheme() {
                return "Bearer";
            }
        });

        // Stocker le userId dans les propriétés de la requête pour les services
        requestContext.setProperty("userId", info.getUserId());
        requestContext.setProperty("username", info.getUsername());

        logger.debug("Requête authentifiée pour userId={}", info.getUserId());
    }

    private void abortWithUnauthorized(ContainerRequestContext ctx, String message) {
        ErrorResponse error = new ErrorResponse(
                Response.Status.UNAUTHORIZED.getStatusCode(),
                "Unauthorized",
                message);
        ctx.abortWith(
                Response.status(Response.Status.UNAUTHORIZED)
                        .header("WWW-Authenticate", "Bearer realm=\"MasterAnnonce\"")
                        .type(MediaType.APPLICATION_JSON)
                        .entity(error)
                        .build());
    }
}

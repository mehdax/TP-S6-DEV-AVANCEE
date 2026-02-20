package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.rest.security;

import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.rest.dto.ApiError;

import javax.annotation.Priority;
import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

import java.util.Set;
@Provider
@Secured
@Priority(Priorities.AUTHENTICATION)
public class AuthFilter implements ContainerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        
        String authHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            abortWithUnauthorized(requestContext,
                    "Token d'authentification manquant. Utilisez : Authorization: Bearer <token>");
            return;
        }

        
        String token = authHeader.substring(BEARER_PREFIX.length()).trim();

        try {
            
            
            
            SimpleCallbackHandler callbackHandler = new SimpleCallbackHandler(token, "");

            LoginContext loginContext = new LoginContext("MasterAnnonceToken", callbackHandler);
            loginContext.login();

            
            Subject subject = loginContext.getSubject();
            Set<UserPrincipal> principals = subject.getPrincipals(UserPrincipal.class);

            if (principals.isEmpty()) {
                throw new LoginException("Aucune identité trouvée dans le Subject");
            }

            UserPrincipal userPrincipal = principals.iterator().next();

            
            requestContext.setProperty("userId", userPrincipal.getUserId());
            requestContext.setProperty("username", userPrincipal.getName());
        } catch (LoginException e) {
            abortWithUnauthorized(requestContext, "Token invalide ou expiré (JAAS refusé)");
        } catch (SecurityException e) {
            abortWithUnauthorized(requestContext, "Erreur de configuration de sécurité : " + e.getMessage());
        }
    }
    private void abortWithUnauthorized(ContainerRequestContext requestContext, String message) {
        ApiError error = new ApiError(401, "Non authentifié", message);

        requestContext.abortWith(
                Response.status(Response.Status.UNAUTHORIZED)
                        .entity(error)
                        .type(MediaType.APPLICATION_JSON)
                        .build());
    }
}


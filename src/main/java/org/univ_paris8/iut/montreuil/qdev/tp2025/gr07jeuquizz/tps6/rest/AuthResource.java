package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.rest;

import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.rest.dto.LoginDTO;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.rest.security.SimpleCallbackHandler;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.rest.security.TokenStore;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.rest.security.UserPrincipal;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
@Path("/login")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    private final TokenStore tokenStore = TokenStore.getInstance();

    @POST
    public Response login(@Valid LoginDTO loginDTO) {
        try {
            
            SimpleCallbackHandler callbackHandler = new SimpleCallbackHandler(
                    loginDTO.getUsername(), loginDTO.getPassword());

            
            LoginContext loginContext = new LoginContext("MasterAnnonceLogin", callbackHandler);

            
            loginContext.login();

            
            Subject subject = loginContext.getSubject();

            
            Set<UserPrincipal> userPrincipals = subject.getPrincipals(UserPrincipal.class);
            if (userPrincipals.isEmpty()) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(Map.of("error", "Erreur interne : UserPrincipal absent du Subject"))
                        .build();
            }

            UserPrincipal userPrincipal = userPrincipals.iterator().next();

            
            String token = tokenStore.generateToken(
                    userPrincipal.getUserId(),
                    userPrincipal.getName());

            
            Map<String, Object> result = new HashMap<>();
            result.put("token", token);
            result.put("userId", userPrincipal.getUserId());
            result.put("username", userPrincipal.getName());

            return Response.ok(result).build();

        } catch (LoginException e) {
            
            Map<String, String> error = new HashMap<>();
            error.put("error", "Identifiants invalides");
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(error)
                    .build();
        }
    }
}


package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.rest;

import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.model.User;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.rest.dto.LoginDTO;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.rest.security.TokenStore;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.service.UserService;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Exercice 5 – Authentification stateless
 *
 * POST /api/login
 * Entrée : { "username": "...", "password": "..." }
 * Sortie : { "token": "uuid-...", "userId": 1, "username": "..." }
 * Erreur : 401 Unauthorized si credentials invalides
 *
 * Le token est stocké en mémoire (TokenStore) et doit être
 * envoyé dans le header Authorization: Bearer <token> pour
 * accéder aux endpoints protégés.
 */
@Path("/login")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    private final UserService userService = new UserService();
    private final TokenStore tokenStore = TokenStore.getInstance();

    @POST
    public Response login(@Valid LoginDTO loginDTO) {
        Optional<User> userOpt = userService.authenticate(
                loginDTO.getUsername(), loginDTO.getPassword());

        if (!userOpt.isPresent()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Identifiants invalides");
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(error)
                    .build();
        }

        User user = userOpt.get();
        String token = tokenStore.generateToken(user.getId(), user.getUsername());

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userId", user.getId());
        result.put("username", user.getUsername());

        return Response.ok(result).build();
    }
}

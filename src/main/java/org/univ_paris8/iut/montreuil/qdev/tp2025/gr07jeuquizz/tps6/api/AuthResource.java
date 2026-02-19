package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.dto.LoginDTO;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.dto.TokenDTO;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.exception.UnauthorizedException;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.model.User;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.security.TokenStore;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.Optional;
import java.util.UUID;

/**
 * Ressource d'authentification (Exercice 5).
 *
 * POST /api/login → génère et retourne un token Bearer
 * POST /api/logout → révoque le token courant
 */
@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Authentification", description = "Login et logout stateless")
public class AuthResource {

    private static final Logger logger = LoggerFactory.getLogger(AuthResource.class);
    private final UserService userService = new UserService();

    /**
     * POST /api/auth/login
     * Authentifie l'utilisateur et retourne un token UUID.
     */
    @POST
    @Path("/login")
    @Operation(summary = "Login – génère un token Bearer stateless")
    public Response login(@Valid LoginDTO dto) {
        Optional<User> userOpt = userService.authenticate(dto.getUsername(), dto.getPassword());

        if (userOpt.isEmpty()) {
            throw new UnauthorizedException(
                    "Identifiants invalides. Vérifiez votre nom d'utilisateur et mot de passe.");
        }

        User user = userOpt.get();

        // Génération d'un token opaque UUID
        String token = UUID.randomUUID().toString();
        TokenStore.getInstance().store(token, user.getId(), user.getUsername());

        logger.info("Login réussi pour userId={}", user.getId());

        return Response.ok(new TokenDTO(token, user.getId(), user.getUsername())).build();
    }

    /**
     * POST /api/auth/logout
     * Révoque le token courant (invalide immédiatement côté serveur).
     */
    @POST
    @Path("/logout")
    @Operation(summary = "Logout – révoque le token Bearer")
    public Response logout(@HeaderParam("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7).trim();
            TokenStore.getInstance().revoke(token);
            logger.info("Token révoqué");
        }

        return Response.ok()
                .entity("{\"message\": \"Déconnexion réussie\"}")
                .build();
    }

    /**
     * POST /api/auth/register
     * Inscription d'un nouvel utilisateur.
     */
    @POST
    @Path("/register")
    @Operation(summary = "Inscription d'un nouvel utilisateur")
    public Response register(@Valid LoginDTO dto) {
        try {
            userService.createUser(dto.getUsername(), dto.getUsername() + "@app.local", dto.getPassword());
            return Response.status(Response.Status.CREATED)
                    .entity("{\"message\": \"Utilisateur créé avec succès\"}")
                    .build();
        } catch (RuntimeException e) {
            throw new javax.ws.rs.BadRequestException(e.getMessage());
        }
    }
}

package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.rest;

import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.model.User;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.service.UserService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

/**
 * Endpoint d'inscription REST.
 * POST /api/register → crée un utilisateur
 */
@Path("/register")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RegisterResource {

    private final UserService userService = new UserService();

    @POST
    public Response register(Map<String, String> body) {
        String username = body.get("username");
        String email = body.get("email");
        String password = body.get("password");

        if (username == null || email == null || password == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Champs obligatoires : username, email, password");
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }

        try {
            User user = userService.createUser(username, email, password);

            Map<String, Object> result = new HashMap<>();
            result.put("id", user.getId());
            result.put("username", user.getUsername());
            result.put("email", user.getEmail());

            return Response.status(Response.Status.CREATED).entity(result).build();

        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getCause() != null ? e.getCause().getMessage() : e.getMessage());
            return Response.status(Response.Status.CONFLICT).entity(error).build();
        }
    }
}

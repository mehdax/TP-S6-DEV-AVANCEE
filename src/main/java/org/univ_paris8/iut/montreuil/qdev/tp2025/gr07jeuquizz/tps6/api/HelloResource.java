package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

/**
 * Exercice 1 – Endpoints de démonstration JAX-RS.
 *
 * GET /api/helloWorld → message simple
 * GET /api/params?q=... → QueryParam
 * GET /api/params/{value} → PathParam
 */
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Hello", description = "Endpoints de démonstration JAX-RS")
public class HelloResource {

    /**
     * GET /api/helloWorld
     * Endpoint simple de vérification que l'API est opérationnelle.
     */
    @GET
    @Path("/helloWorld")
    @Operation(summary = "Hello World", description = "Vérifie que l'API JAX-RS fonctionne")
    public Response helloWorld() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Hello World depuis MasterAnnonce API !");
        response.put("framework", "Jersey JAX-RS");
        response.put("version", "1.0");
        return Response.ok(response).build();
    }

    /**
     * GET /api/params?q=valeur
     * Démonstration d'un QueryParam.
     */
    @GET
    @Path("/params")
    @Operation(summary = "Query Parameter", description = "Démonstration d'un QueryParam ?q=...")
    public Response queryParam(@QueryParam("q") @DefaultValue("(aucun)") String q) {
        Map<String, String> response = new HashMap<>();
        response.put("type", "QueryParam");
        response.put("parametre", "q");
        response.put("valeur", q);
        response.put("exemple", "GET /api/params?q=bonjour");
        return Response.ok(response).build();
    }

    /**
     * GET /api/params/{value}
     * Démonstration d'un PathParam.
     */
    @GET
    @Path("/params/{value}")
    @Operation(summary = "Path Parameter", description = "Démonstration d'un PathParam /{value}")
    public Response pathParam(@PathParam("value") String value) {
        Map<String, String> response = new HashMap<>();
        response.put("type", "PathParam");
        response.put("parametre", "value");
        response.put("valeur", value);
        response.put("exemple", "GET /api/params/bonjour");
        return Response.ok(response).build();
    }
}

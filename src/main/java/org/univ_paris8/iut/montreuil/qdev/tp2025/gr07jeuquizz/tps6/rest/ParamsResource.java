package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

/**
 * Ressource REST démontrant le passage de paramètres avec JAX-RS.
 *
 * Deux types de paramètres sont illustrés :
 * a) QueryParams : GET /api/params?name=xxx&age=yyy
 * b) PathParams : GET /api/params/{id}
 */
@Path("/params")
public class ParamsResource {

    /**
     * Démonstration des @QueryParam.
     *
     * Exemple d'appel : GET /api/params?name=Jean&age=25
     * Réponse JSON : {"name":"Jean", "age":"25", "type":"QueryParam"}
     *
     * @param name le nom passé en query parameter (défaut : "inconnu")
     * @param age  l'âge passé en query parameter (défaut : "0")
     * @return Response JSON contenant les paramètres reçus
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getWithQueryParams(
            @QueryParam("name") @DefaultValue("inconnu") String name,
            @QueryParam("age") @DefaultValue("0") String age) {

        Map<String, String> response = new HashMap<>();
        response.put("type", "QueryParam");
        response.put("name", name);
        response.put("age", age);
        return Response.ok(response).build();
    }

    /**
     * Démonstration des @PathParam.
     *
     * Exemple d'appel : GET /api/params/42
     * Réponse JSON : {"id":"42", "type":"PathParam"}
     *
     * @param id l'identifiant extrait du chemin de l'URI
     * @return Response JSON contenant le paramètre reçu
     */
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getWithPathParam(@PathParam("id") String id) {
        Map<String, String> response = new HashMap<>();
        response.put("type", "PathParam");
        response.put("id", id);
        return Response.ok(response).build();
    }
}

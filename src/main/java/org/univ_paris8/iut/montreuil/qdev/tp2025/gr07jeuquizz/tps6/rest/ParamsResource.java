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
@Path("/params")
public class ParamsResource {
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


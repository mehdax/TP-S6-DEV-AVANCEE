package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;
@Path("/helloWorld")
public class HelloWorldResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response helloWorld() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Hello World!");
        return Response.ok(response).build();
    }
}


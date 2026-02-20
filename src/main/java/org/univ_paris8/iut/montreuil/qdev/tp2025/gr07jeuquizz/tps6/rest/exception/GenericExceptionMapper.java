package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.rest.exception;

import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.rest.dto.ApiError;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable exception) {
        
        System.err.println("[API ERROR] Exception non gérée : " + exception.getMessage());
        exception.printStackTrace();

        ApiError error = new ApiError(
                500,
                "Erreur interne du serveur",
                "Une erreur inattendue s'est produite. Veuillez réessayer.");

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(error)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}


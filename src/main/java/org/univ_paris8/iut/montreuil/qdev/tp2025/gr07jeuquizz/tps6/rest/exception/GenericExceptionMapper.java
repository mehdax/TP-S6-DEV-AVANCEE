package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.rest.exception;

import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.rest.dto.ApiError;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Intercepte TOUTES les exceptions non gérées par les autres mappers.
 * Garantit qu'aucune stacktrace Java ne fuit dans la réponse API.
 *
 * → 500 Internal Server Error : erreur interne
 *
 * 💥 Douleur volontaire (TP) : Sans ce mapper, une exception non interceptée
 * rend l'API totalement inutilisable (page HTML Tomcat au lieu de JSON).
 */
@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable exception) {
        // Log côté serveur pour débogage
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

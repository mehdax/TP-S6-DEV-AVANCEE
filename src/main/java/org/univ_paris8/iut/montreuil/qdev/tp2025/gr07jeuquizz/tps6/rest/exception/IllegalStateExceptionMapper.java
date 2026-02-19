package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.rest.exception;

import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.rest.dto.ApiError;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Intercepte les IllegalStateException (conflits métier)
 * et retourne une réponse 409 normalisée.
 *
 * → 409 Conflict : conflit métier
 * Ex : publier une annonce qui n'est pas en DRAFT,
 * archiver une annonce qui n'est pas PUBLISHED, etc.
 */
@Provider
public class IllegalStateExceptionMapper implements ExceptionMapper<IllegalStateException> {

    @Override
    public Response toResponse(IllegalStateException exception) {
        ApiError error = new ApiError(
                409,
                "Conflit métier",
                exception.getMessage());

        return Response.status(Response.Status.CONFLICT)
                .entity(error)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}

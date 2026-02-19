package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.exception.mapper;

import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.dto.ErrorResponse;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.exception.AnnonceNotFoundException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Mappe AnnonceNotFoundException → HTTP 404.
 */
@Provider
public class AnnonceNotFoundExceptionMapper implements ExceptionMapper<AnnonceNotFoundException> {

    @Override
    public Response toResponse(AnnonceNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
                Response.Status.NOT_FOUND.getStatusCode(),
                "Not Found",
                ex.getMessage());
        return Response.status(Response.Status.NOT_FOUND)
                .type(MediaType.APPLICATION_JSON)
                .entity(error)
                .build();
    }
}

package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.exception.mapper;

import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.dto.ErrorResponse;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.exception.UnauthorizedException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Mappe UnauthorizedException → HTTP 401 Unauthorized.
 */
@Provider
public class UnauthorizedExceptionMapper implements ExceptionMapper<UnauthorizedException> {

    @Override
    public Response toResponse(UnauthorizedException ex) {
        ErrorResponse error = new ErrorResponse(
                Response.Status.UNAUTHORIZED.getStatusCode(),
                "Unauthorized",
                ex.getMessage());
        return Response.status(Response.Status.UNAUTHORIZED)
                .header("WWW-Authenticate", "Bearer realm=\"MasterAnnonce\"")
                .type(MediaType.APPLICATION_JSON)
                .entity(error)
                .build();
    }
}

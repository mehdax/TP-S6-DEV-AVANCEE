package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.exception.mapper;

import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.dto.ErrorResponse;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.exception.BusinessRuleException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Mappe BusinessRuleException → HTTP 409 Conflict.
 */
@Provider
public class BusinessRuleExceptionMapper implements ExceptionMapper<BusinessRuleException> {

    @Override
    public Response toResponse(BusinessRuleException ex) {
        ErrorResponse error = new ErrorResponse(
                Response.Status.CONFLICT.getStatusCode(),
                "Conflict",
                ex.getMessage());
        return Response.status(Response.Status.CONFLICT)
                .type(MediaType.APPLICATION_JSON)
                .entity(error)
                .build();
    }
}

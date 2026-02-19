package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.rest.exception;

import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.rest.dto.ApiError;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Intercepte les ConstraintViolationException (Bean Validation)
 * et retourne une réponse 400 normalisée avec le détail de chaque violation.
 *
 * Exemple de réponse :
 * {
 * "status": 400,
 * "error": "Erreur de validation",
 * "message": "Les données envoyées sont invalides",
 * "details": [
 * "title : ne doit pas être vide",
 * "mail : doit être un email valide"
 * ],
 * "timestamp": "2026-02-19T..."
 * }
 */
@Provider
public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        List<String> details = exception.getConstraintViolations()
                .stream()
                .map(this::formatViolation)
                .collect(Collectors.toList());

        ApiError error = new ApiError(
                400,
                "Erreur de validation",
                "Les données envoyées sont invalides",
                details);

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(error)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    private String formatViolation(ConstraintViolation<?> violation) {
        // Extraire le nom du champ depuis le chemin de la propriété
        String field = "";
        for (javax.validation.Path.Node node : violation.getPropertyPath()) {
            field = node.getName();
        }
        return field + " : " + violation.getMessage();
    }
}

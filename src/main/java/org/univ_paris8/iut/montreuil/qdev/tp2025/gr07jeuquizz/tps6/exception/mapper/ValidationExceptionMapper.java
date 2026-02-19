package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.exception.mapper;

import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mappe les violations de Bean Validation → HTTP 400 Bad Request.
 * Retourne la liste détaillée des champs invalides.
 *
 * Exercice 3 – Douleur volontaire évitée grâce à ce mapper centralisé :
 * sans ce provider, une ConstraintViolationException non interceptée renvoie
 * une 500 avec une trace stacktrace XML/HTML, rendant l'API inexploitable.
 */
@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    private static final Logger logger = LoggerFactory.getLogger(ValidationExceptionMapper.class);

    @Override
    public Response toResponse(ConstraintViolationException ex) {
        logger.warn("Validation échouée: {}", ex.getMessage());

        List<String> details = ex.getConstraintViolations().stream()
                .map(cv -> extractFieldName(cv) + " : " + cv.getMessage())
                .sorted()
                .collect(Collectors.toList());

        ErrorResponse error = new ErrorResponse(
                Response.Status.BAD_REQUEST.getStatusCode(),
                "Bad Request",
                "Données invalides. Corrigez les erreurs de validation.",
                details);

        return Response.status(Response.Status.BAD_REQUEST)
                .type(MediaType.APPLICATION_JSON)
                .entity(error)
                .build();
    }

    private String extractFieldName(ConstraintViolation<?> cv) {
        String path = cv.getPropertyPath().toString();
        // Extraire seulement le nom du champ (ex: "createAnnonce.dto.title" → "title")
        String[] parts = path.split("\\.");
        return parts[parts.length - 1];
    }
}

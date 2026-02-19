package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.exception.mapper;

import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Catch-all : intercepte toute exception non gérée → HTTP 500.
 *
 * Exercice 4 – Douleur volontaire évitée :
 * Sans ce mapper, une exception non interceptée produit une réponse HTML/XML
 * illisible du conteneur Tomcat, rendant l'API inutilisable pour les clients
 * JSON.
 */
@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger logger = LoggerFactory.getLogger(GenericExceptionMapper.class);

    @Override
    public Response toResponse(Throwable ex) {
        logger.error("Erreur interne non gérée", ex);

        ErrorResponse error = new ErrorResponse(
                Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                "Internal Server Error",
                "Une erreur interne inattendue s'est produite. Veuillez réessayer.");

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .type(MediaType.APPLICATION_JSON)
                .entity(error)
                .build();
    }
}

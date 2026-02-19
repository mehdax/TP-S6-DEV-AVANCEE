package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.exception;

/**
 * Exception levée lors d'une tentative d'accès interdit (authentifié mais non
 * autorisé) → HTTP 403.
 */
public class ForbiddenException extends RuntimeException {

    public ForbiddenException(String message) {
        super(message);
    }

    public ForbiddenException() {
        super("Accès refusé : vous n'êtes pas autorisé à effectuer cette action.");
    }
}

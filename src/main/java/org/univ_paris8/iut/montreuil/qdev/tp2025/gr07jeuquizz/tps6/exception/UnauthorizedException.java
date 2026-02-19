package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.exception;

/**
 * Exception levée lors d'une tentative d'accès non authentifié → HTTP 401.
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException() {
        super("Authentification requise. Veuillez vous connecter.");
    }
}

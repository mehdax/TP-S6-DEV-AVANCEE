package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.exception;

/**
 * Exception levée lors d'une violation de règle métier → HTTP 409 Conflict.
 */
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);
    }

    public BusinessRuleException(String message, Throwable cause) {
        super(message, cause);
    }
}

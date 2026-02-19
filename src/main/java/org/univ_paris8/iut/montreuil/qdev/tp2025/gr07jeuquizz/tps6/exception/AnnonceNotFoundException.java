package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.exception;

/**
 * Exception levée quand une annonce n'est pas trouvée → HTTP 404.
 */
public class AnnonceNotFoundException extends RuntimeException {

    private final Long annonceId;

    public AnnonceNotFoundException(Long id) {
        super("Annonce introuvable avec l'identifiant : " + id);
        this.annonceId = id;
    }

    public Long getAnnonceId() {
        return annonceId;
    }
}

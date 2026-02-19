package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Stockage en mémoire des tokens d'authentification.
 * Implémentation thread-safe via ConcurrentHashMap.
 *
 * Exercice 5 – Authentification stateless :
 * Le token est généré au login et envoyé à chaque requête dans l'header
 * Authorization.
 * Aucune session HTTP n'est utilisée.
 *
 * Note : En production, ce store serait remplacé par Redis ou une BDD.
 */
public class TokenStore {

    private static final Logger logger = LoggerFactory.getLogger(TokenStore.class);

    // Durée de vie des tokens : 24 heures
    private static final long TOKEN_LIFETIME_SECONDS = 24 * 60 * 60;

    // Singleton thread-safe
    private static final TokenStore INSTANCE = new TokenStore();

    // Map <token, TokenInfo>
    private final Map<String, TokenInfo> tokens = new ConcurrentHashMap<>();

    private TokenStore() {
    }

    public static TokenStore getInstance() {
        return INSTANCE;
    }

    /**
     * Enregistre un nouveau token pour un utilisateur.
     */
    public void store(String token, Long userId, String username) {
        Instant expiration = Instant.now().plusSeconds(TOKEN_LIFETIME_SECONDS);
        tokens.put(token, new TokenInfo(userId, username, expiration));
        logger.debug("Token stocké pour userId={}, expiration={}", userId, expiration);
    }

    /**
     * Valide un token et retourne ses informations si valide.
     */
    public Optional<TokenInfo> validate(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }

        TokenInfo info = tokens.get(token);
        if (info == null) {
            return Optional.empty();
        }

        // Vérification de l'expiration
        if (Instant.now().isAfter(info.getExpiration())) {
            tokens.remove(token);
            logger.debug("Token expiré supprimé pour userId={}", info.getUserId());
            return Optional.empty();
        }

        return Optional.of(info);
    }

    /**
     * Révoque un token (logout).
     */
    public void revoke(String token) {
        tokens.remove(token);
        logger.debug("Token révoqué");
    }

    /**
     * Informations associées à un token.
     */
    public static class TokenInfo {
        private final Long userId;
        private final String username;
        private final Instant expiration;

        public TokenInfo(Long userId, String username, Instant expiration) {
            this.userId = userId;
            this.username = username;
            this.expiration = expiration;
        }

        public Long getUserId() {
            return userId;
        }

        public String getUsername() {
            return username;
        }

        public Instant getExpiration() {
            return expiration;
        }
    }
}

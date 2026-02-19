package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.rest.security;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Stockage en mémoire des tokens d'authentification.
 *
 * Exercice 5 : Authentification stateless
 * - Chaque login génère un UUID unique (le token)
 * - Le token est associé au userId en mémoire
 * - Le token est renvoyé au client qui le passe à chaque requête
 * - Pas de session HTTP → stateless
 *
 * Note : En production, on utiliserait JWT ou Redis.
 * Ici, ConcurrentHashMap suffit pour le TP.
 */
public class TokenStore {

    // Singleton
    private static final TokenStore INSTANCE = new TokenStore();

    // token → userId
    private final Map<String, Long> tokenToUserId = new ConcurrentHashMap<>();
    // token → username
    private final Map<String, String> tokenToUsername = new ConcurrentHashMap<>();

    private TokenStore() {
    }

    public static TokenStore getInstance() {
        return INSTANCE;
    }

    /**
     * Génère un nouveau token pour un utilisateur authentifié.
     *
     * @param userId   l'identifiant de l'utilisateur
     * @param username le nom d'utilisateur
     * @return le token UUID généré
     */
    public String generateToken(Long userId, String username) {
        String token = UUID.randomUUID().toString();
        tokenToUserId.put(token, userId);
        tokenToUsername.put(token, username);
        return token;
    }

    /**
     * Valide un token et retourne le userId associé.
     *
     * @param token le token à valider
     * @return le userId ou null si le token est invalide
     */
    public Long getUserId(String token) {
        return tokenToUserId.get(token);
    }

    /**
     * Retourne le username associé à un token.
     */
    public String getUsername(String token) {
        return tokenToUsername.get(token);
    }

    /**
     * Vérifie si un token est valide.
     */
    public boolean isValid(String token) {
        return token != null && tokenToUserId.containsKey(token);
    }

    /**
     * Invalide un token (logout).
     */
    public void removeToken(String token) {
        tokenToUserId.remove(token);
        tokenToUsername.remove(token);
    }
}

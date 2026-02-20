package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.rest.security;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
public class TokenStore {

    
    private static final TokenStore INSTANCE = new TokenStore();

    
    private final Map<String, Long> tokenToUserId = new ConcurrentHashMap<>();
    
    private final Map<String, String> tokenToUsername = new ConcurrentHashMap<>();

    private TokenStore() {
    }

    public static TokenStore getInstance() {
        return INSTANCE;
    }
    public String generateToken(Long userId, String username) {
        String token = UUID.randomUUID().toString();
        tokenToUserId.put(token, userId);
        tokenToUsername.put(token, username);
        return token;
    }
    public Long getUserId(String token) {
        return tokenToUserId.get(token);
    }
    public String getUsername(String token) {
        return tokenToUsername.get(token);
    }
    public boolean isValid(String token) {
        return token != null && tokenToUserId.containsKey(token);
    }
    public void removeToken(String token) {
        tokenToUserId.remove(token);
        tokenToUsername.remove(token);
    }
}


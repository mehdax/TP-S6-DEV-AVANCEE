package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.dto;

/**
 * DTO de réponse pour le login (token JWT simplifié).
 */
public class TokenDTO {

    private String token;
    private Long userId;
    private String username;

    public TokenDTO() {
    }

    public TokenDTO(String token, Long userId, String username) {
        this.token = token;
        this.userId = userId;
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

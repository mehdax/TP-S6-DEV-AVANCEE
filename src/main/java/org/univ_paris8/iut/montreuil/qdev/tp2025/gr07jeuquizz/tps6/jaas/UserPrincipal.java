package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.jaas;

import java.security.Principal;

/**
 * Principal JAAS représentant un utilisateur authentifié.
 * Implémente java.security.Principal.
 */
public class UserPrincipal implements Principal {

    private final String name;
    private final Long userId;

    public UserPrincipal(String username, Long userId) {
        this.name = username;
        this.userId = userId;
    }

    @Override
    public String getName() {
        return name;
    }

    public Long getUserId() {
        return userId;
    }

    @Override
    public String toString() {
        return "UserPrincipal{username='" + name + "', userId=" + userId + "}";
    }
}

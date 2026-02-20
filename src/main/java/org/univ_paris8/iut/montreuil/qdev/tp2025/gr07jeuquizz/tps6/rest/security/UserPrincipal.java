package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.rest.security;

import java.io.Serializable;
import java.security.Principal;
import java.util.Objects;
public class UserPrincipal implements Principal, Serializable {

    private static final long serialVersionUID = 1L;

    private final String username;
    private final Long userId;

    public UserPrincipal(String username, Long userId) {
        if (username == null || userId == null) {
            throw new IllegalArgumentException("username et userId ne peuvent pas être null");
        }
        this.username = username;
        this.userId = userId;
    }

    @Override
    public String getName() {
        return username;
    }

    public Long getUserId() {
        return userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserPrincipal that = (UserPrincipal) o;
        return Objects.equals(username, that.username) && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, userId);
    }

    @Override
    public String toString() {
        return "UserPrincipal{username='" + username + "', userId=" + userId + "}";
    }
}


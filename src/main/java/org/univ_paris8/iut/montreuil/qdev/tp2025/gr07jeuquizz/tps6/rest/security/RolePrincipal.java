package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.rest.security;

import java.io.Serializable;
import java.security.Principal;
import java.util.Objects;
public class RolePrincipal implements Principal, Serializable {

    private static final long serialVersionUID = 1L;

    private final String role;

    public RolePrincipal(String role) {
        if (role == null || role.isBlank()) {
            throw new IllegalArgumentException("Le rôle ne peut pas être null ou vide");
        }
        this.role = role;
    }

    @Override
    public String getName() {
        return role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        RolePrincipal that = (RolePrincipal) o;
        return Objects.equals(role, that.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(role);
    }

    @Override
    public String toString() {
        return "RolePrincipal{role='" + role + "'}";
    }
}


package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.jaas;

import java.security.Principal;

/**
 * Principal JAAS représentant un rôle d'un utilisateur authentifié.
 */
public class RolePrincipal implements Principal {

    private final String name;

    public RolePrincipal(String roleName) {
        this.name = roleName;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "RolePrincipal{role='" + name + "'}";
    }
}

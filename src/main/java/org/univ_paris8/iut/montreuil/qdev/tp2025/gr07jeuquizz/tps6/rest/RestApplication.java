package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.rest;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * Point d'entrée JAX-RS.
 *
 * L'annotation @ApplicationPath("/api") définit le préfixe de toutes les
 * ressources REST exposées par l'application.
 *
 * Choix de configuration :
 * - Jersey (implémentation de référence JAX-RS) est utilisé car c'est
 * l'implémentation la plus mature et la mieux documentée.
 * - La configuration est faite via web.xml (ServletContainer) pour un
 * contrôle explicite du mapping, ce qui est plus lisible et facile à
 * déboguer qu'une configuration purement par annotation.
 * - Jersey est enregistré avec le mapping /api/* dans web.xml.
 */
@ApplicationPath("/api")
public class RestApplication extends Application {
    // Jersey scanne automatiquement les classes annotées @Path
    // dans le même package et ses sous-packages.
}

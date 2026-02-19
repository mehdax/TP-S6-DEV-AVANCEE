package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.api;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;

/**
 * Point d'entrée JAX-RS de l'application.
 *
 * Choix de Jersey comme implémentation JAX-RS :
 * - Implémentation de référence d'Oracle (RI de JAX-RS)
 * - Intégration native avec Tomcat via jersey-container-servlet
 * - Excellent support de Jackson pour JSON
 * - Framework de tests intégré (jersey-test-framework)
 * - Documentation et communauté très actives
 *
 * Configuration via ResourceConfig (scan automatique du package) plutôt
 * qu'un web.xml verbeux, ce qui simplifie la maintenance.
 */
@ApplicationPath("/api")
@OpenAPIDefinition(info = @Info(title = "MasterAnnonce API", version = "1.0", description = "API REST pour la gestion d'annonces - TP3 Dev Avancé"))
public class JaxRsApplication extends ResourceConfig {

    public JaxRsApplication() {
        // Scan automatique de tous les providers et resources du package
        packages("org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6");
    }
}

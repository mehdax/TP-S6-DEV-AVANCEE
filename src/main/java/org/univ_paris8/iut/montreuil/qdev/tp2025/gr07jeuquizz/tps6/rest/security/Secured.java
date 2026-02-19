package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.rest.security;

import javax.ws.rs.NameBinding;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation de marquage pour les endpoints protégés.
 *
 * Exercice 6 : Filtre de sécurité
 * Les endpoints annotés @Secured nécessitent un token valide
 * dans le header Authorization: Bearer <token>.
 *
 * Utilisation :
 * 
 * @Secured // sur la classe ou la méthode
 * @GET
 *      public Response getProtectedResource() { ... }
 */
@NameBinding
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
public @interface Secured {
}

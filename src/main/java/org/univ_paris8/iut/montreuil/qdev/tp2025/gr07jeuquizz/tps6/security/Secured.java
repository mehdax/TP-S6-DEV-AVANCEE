package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.security;

import java.lang.annotation.*;
import javax.ws.rs.NameBinding;

/**
 * Annotation de Name Binding JAX-RS.
 * Marquer un endpoint avec @Secured force le passage par AuthFilter.
 *
 * Utilisation : @Secured sur la classe Resource ou sur une méthode spécifique.
 */
@NameBinding
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
public @interface Secured {
}

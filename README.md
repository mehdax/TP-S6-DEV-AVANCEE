# MasterAnnonce – API REST Sécurisée (TP3)

## Architecture

```
src/main/java/.../tps6/
├── api/                   ← Ressources JAX-RS (entrée HTTP)
│   ├── JaxRsApplication   ← Point d'entrée @ApplicationPath("/api")
│   ├── HelloResource      ← /api/helloWorld, /api/params
│   ├── AnnonceResource    ← /api/annonces (CRUD complet)
│   └── AuthResource       ← /api/auth/login, /api/auth/logout
├── dto/                   ← Objets de transfert (Pattern Builder)
├── mapper/                ← Conversion Entity ↔ DTO
├── model/                 ← Entités JPA (Annonce, User, Category)
├── DAO/                   ← Couche accès données (GenericDAO + spécialisés)
├── service/               ← Logique métier (AnnonceService, UserService)
├── security/              ← Sécurité stateless (TokenStore, AuthFilter, @Secured)
├── jaas/                  ← Modules JAAS (Bonus)
└── exception/             ← Exceptions métier + ExceptionMappers JAX-RS
```

### Choix de Jersey comme implémentation JAX-RS

Jersey a été choisi car :
- **Implémentation de référence** d'Oracle pour JAX-RS 2.x
- **Intégration Tomcat** native via `jersey-container-servlet` (pas de configuration complexe)
- **Jersey Test Framework** intégré avec Grizzly2 pour les tests REST sans déploiement
- **Support Jackson** excellent via `jersey-media-json-jackson`
- **Documentation** très complète et communauté active

## Endpoints API

| Verbe  | URI                          | Auth | Description                              |
|--------|------------------------------|------|------------------------------------------|
| GET    | `/api/helloWorld`            | ❌   | Test de l'API                            |
| GET    | `/api/params?q=...`          | ❌   | Démonstration QueryParam                 |
| GET    | `/api/params/{value}`        | ❌   | Démonstration PathParam                  |
| POST   | `/api/auth/login`            | ❌   | Login → token Bearer                     |
| POST   | `/api/auth/logout`           | ❌   | Logout → révocation du token             |
| POST   | `/api/auth/register`         | ❌   | Inscription                              |
| GET    | `/api/annonces`              | ❌   | Liste paginée (`?page=0&pageSize=10`)    |
| GET    | `/api/annonces/{id}`         | ❌   | Détail d'une annonce                     |
| POST   | `/api/annonces`              | ✅   | Créer une annonce (DRAFT)                |
| PUT    | `/api/annonces/{id}`         | ✅   | Mise à jour complète (auteur, non-PUBL.) |
| PATCH  | `/api/annonces/{id}`         | ✅   | Mise à jour partielle                    |
| DELETE | `/api/annonces/{id}`         | ✅   | Suppression (ARCHIVED obligatoire)       |
| POST   | `/api/annonces/{id}/publish` | ✅   | DRAFT → PUBLISHED                        |
| POST   | `/api/annonces/{id}/archive` | ✅   | PUBLISHED → ARCHIVED                     |

## Codes HTTP

| Code | Scénario                                           |
|------|----------------------------------------------------|
| 200  | Succès (GET, PUT, PATCH, publish, archive)         |
| 201  | Création réussie (POST)                            |
| 204  | Suppression réussie (DELETE)                       |
| 400  | Données invalides (Bean Validation)                |
| 401  | Token manquant ou expiré                           |
| 403  | Authentifié mais pas l'auteur                      |
| 404  | Annonce ou ressource introuvable                   |
| 409  | Règle métier violée (ex: PUBLISHED non modifiable) |
| 500  | Erreur interne inattendue                          |

## Format de réponse d'erreur

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Données invalides. Corrigez les erreurs de validation.",
  "timestamp": "2024-01-01T12:00:00",
  "details": ["title : Le titre est obligatoire"]
}
```

## Flux d'authentification

```
Client                     Serveur
  │                            │
  │── POST /api/auth/login ───▶│ Vérification username/password
  │◀─ { token, userId } ───────│ Génère UUID → stocké dans TokenStore
  │                            │
  │── GET /api/annonces ───────▶│ (pas besoin de token)
  │◀─ { data: [...] } ─────────│
  │                            │
  │── POST /api/annonces ──────▶│ Authorization: Bearer <token>
  │   Authorization: Bearer X  │ → AuthFilter valide le token
  │◀─ 201 Created ─────────────│ → userId injecté en propriété requête
```

## Bonus JAAS

Le flow JAAS augmente le flux standard :
1. **Login** : `LoginContext("MasterAnnonceLogin")` → `DbLoginModule` vérifie en BDD → `Subject` peuplé avec `UserPrincipal`
2. **Par requête** : `LoginContext("MasterAnnonceToken")` → `TokenLoginModule` valide le token → `Subject` reconstruit

Activation : `JAVA_OPTS="-Djava.security.auth.login.config=/chemin/vers/jaas.conf"`

## Tests

```bash
# Tests unitaires uniquement (rapides, pas de BDD)
mvn test -P unit-tests

# Tests d'intégration uniquement (Jersey Test Framework)
mvn verify -P integration-tests

# Tous les tests
mvn verify
```

**Intérêt de séparer les tests :**
- **Tests unitaires** (`*Test.java`) : s'exécutent en quelques secondes, pas d'infrastructure requise. Idéaux pour la CI rapide à chaque commit.
- **Tests d'intégration** (`*IT.java`) : démarrent un serveur HTTP Grizzly, testent le vrai pipeline JAX-RS. Plus lents (quelques secondes chacun). À exécuter avant chaque release.

## Problèmes rencontrés et solutions

### 1. `getid()` retournait `int` – incompatible avec les comparaisons `Long`
**Problème :** La méthode `getId()` retournait `int` via `id.intValue()`, causant des erreurs de comparaison de type dans les règles métier (`author.getId().equals(requesterId)`).  
**Solution :** Refactored `getId()` pour retourner `Long` directement.

### 2. persistence.xml : conflit entre unité `"default"` (PostgreSQL) et unité de test (H2)
**Problème :** Le fichier `src/test/resources/META-INF/persistence.xml` remplace le fichier principal au classpath d'exécution des tests, mais l'alias "default" était nécessaire pour `JPAUtil` sans modification.  
**Solution :** Le fichier de test déclare deux PU : `"masterannonce-test"` (H2) et `"default"` (aussi H2), permettant aux tests de fonctionner sans modifier `JPAUtil`.

### 3. Bean Validation dans Jersey : `ConstraintViolationException` non interceptée par défaut
**Problème :** Sans le `ValidationExceptionMapper`, les violations Bean Validation produisaient une réponse HTML 500 du conteneur.  
**Solution :** Implémentation d'un `@Provider ExceptionMapper<ConstraintViolationException>` retournant un JSON structuré 400.

### 4. Filtre JAX-RS `@Secured` avec Name Binding
**Problème :** Un `ContainerRequestFilter` global intercepte TOUTES les requêtes, y compris le login lui-même, provoquant une boucle.  
**Solution :** Utilisation du mécanisme de **Name Binding** JAX-RS (`@NameBinding`) avec l'annotation `@Secured` appliquée uniquement sur les endpoints qui nécessitent une authentification.

### 5. Tests IT avec Jersey Test Framework et JUnit 5
**Problème :** `JerseyTest` hérite de JUnit 4 par défaut. L'utilisation avec JUnit 5 nécessite d'éliminer l'héritage de `@RunWith` JUnit 4.  
**Solution :** Jersey 2.x supporte JUnit 5 nativement si on n'utilise pas `@RunWith(JerseyTest.class)` mais qu'on hérite directement de `JerseyTest`.

## Lancement

1. Démarrer PostgreSQL avec la BDD `masterannonce`
2. Déployer le WAR sur Tomcat 9+ :  `mvn package` puis copier `target/TP-S6-1.0-SNAPSHOT.war` dans `webapps/`
3. L'API est disponible sur `http://localhost:8080/TP-S6/api/`

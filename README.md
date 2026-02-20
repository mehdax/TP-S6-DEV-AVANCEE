# MasterAnnonce – Backend API REST sécurisé

## Architecture

```
src/main/java/.../tps6/
├── model/                   # Entités JPA (Annonce, User, Category, AnnonceStatus)
├── DAO/                     # Data Access Objects JPA (GenericDAO, AnnonceDAO, UserDAO, CategoryDAO)
├── service/                 # Couche métier (AnnonceService, UserService, CategoryService)
├── rest/                    # JAX-RS Resources + DTOs + Mapper + Exception Mappers
│   ├── dto/                 # AnnonceDTO (Pattern Builder), LoginDTO, ApiError
│   ├── mapper/              # AnnonceMapper (Entity ↔ DTO)
│   ├── exception/           # ExceptionMappers (400, 404, 409, 500)
│   └── security/            # AuthFilter, TokenStore, @Secured
├── servlet/                 # Servlets legacy (JSP)
├── util/                    # JPAUtil (EntityManagerFactory)
└── db/                      # ConnectionDB (JDBC legacy)
```

### Couches

| Couche      | Responsabilité                          | Tecnologie            |
|-------------|----------------------------------------|-----------------------|
| REST        | Exposition API, validation, sérialisation JSON | JAX-RS (Jersey 2.41) |
| Service     | Logique métier, transactions           | Java EE               |
| DAO         | Accès données, requêtes JPA            | JPA / Hibernate 5.6   |
| Model       | Entités, Bean Validation               | JPA, javax.validation  |
| Security    | Auth stateless par token               | ContainerRequestFilter |

## Endpoints

| Verbe   | URI                          | Description                    | Auth |
|---------|------------------------------|--------------------------------|------|
| GET     | /api/annonces                | Liste paginée                  | Non  |
| GET     | /api/annonces/{id}           | Détail                         | Non  |
| POST    | /api/annonces                | Création                       | Oui  |
| PUT     | /api/annonces/{id}           | Mise à jour complète           | Oui  |
| DELETE  | /api/annonces/{id}           | Suppression (archivée requis)  | Oui  |
| PATCH   | /api/annonces/{id}           | Mise à jour partielle (Bonus)  | Oui  |
| PUT     | /api/annonces/{id}/publish   | Publier (DRAFT → PUBLISHED)    | Oui  |
| PUT     | /api/annonces/{id}/archive   | Archiver (PUBLISHED → ARCHIVED)| Oui  |
| POST    | /api/login                   | Authentification               | Non  |
| POST    | /api/register                | Inscription                    | Non  |

## Tests

### Lancer tous les tests
```bash
mvn test
```

### Tests unitaires uniquement (rapides, sans BDD)
```bash
mvn test -P unit-tests
```

### Tests d'intégration uniquement (H2 In-Memory)
```bash
mvn test -P integration-tests
```

### Pourquoi séparer les tests ?

Il est intéressant de séparer l'exécution des tests unitaires et des tests d'intégration car :

- **Tests unitaires** : rapides (~1s), ne nécessitent aucune infrastructure (pas de BDD, pas de serveur). Ils peuvent être lancés à chaque commit pour valider la logique métier.
- **Tests d'intégration** : plus lents (~5-10s), montent une BDD H2 ou un serveur HTTP embarqué. On les lance avant un merge ou en CI pour valider le comportement de bout en bout.

Cette séparation évite de bloquer le développement quotidien par des tests lents tout en gardant la validation complète en CI/CD.

## Problèmes rencontrés et solutions

### 1. JPAUtil statique non mockable

**Problème** : `AnnonceService` appelle `JPAUtil.getEntityManager()` dans un bloc statique. Impossible de mocker l'`EntityManager` directement dans les tests unitaires.

**Solution** : Les tests unitaires testent la logique métier directement sur le modèle (`Annonce.publish()`, `Annonce.archive()`) et mockent les DAOs en injectant un `EntityManager` mocké dans le constructeur du DAO. Les tests d'intégration utilisent H2 pour tester la vraie persistence.

### 2. H2 vs PostgreSQL – dialecte et compatibilité

**Problème** : Certaines fonctionnalités PostgreSQL ne sont pas supportées par H2. Le mode PostgreSQL de H2 (`MODE=PostgreSQL`) atténue ce problème.

**Solution** : Utilisation de `jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL` et du dialecte `H2Dialect` dans la persistence unit de test.

### 3. BDD create-drop et isolation des tests

**Problème** : Les données d'un test peuvent polluer le suivant si on ne nettoie pas entre les tests.

**Solution** : `@BeforeEach` charge les fixtures, `@AfterEach` purge toutes les données. Le `hibernate.hbm2ddl.auto=create-drop` recrée le schéma à chaque démarrage de l'`EntityManagerFactory`.

### 4. Annonce.getId() retourne int

**Problème** : Le champ JPA `id` est de type `Long` mais `getId()` retourne `int`. Cela cause des problèmes de comparaison dans les tests.

**Solution** : Cast explicite `(long) annonce.getId()` dans les tests. À terme, il serait préférable de modifier `getId()` pour retourner `Long`.

### 5. Logging – remplacement de System.out

**Problème** : Le code utilisait `System.out.println()` pour le logging, ce qui n'est pas structuré et ne peut pas être filtré par niveau.

**Solution** : Ajout de SLF4J + Logback avec un format structuré `timestamp [thread] LEVEL logger - message`. Les logs Hibernate sont configurés en WARN par défaut.

## Technologies

- Java 11
- Jakarta EE / JAX-RS (Jersey 2.41)
- JPA / Hibernate 5.6.15.Final
- PostgreSQL (production) / H2 (tests)
- JUnit 5.10.0
- Mockito 5.12.0
- Logback 1.4.14 / SLF4J 2.0.9
- Maven 3.x

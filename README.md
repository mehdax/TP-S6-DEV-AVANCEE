# TP 2 - MasterAnnonce avec JPA/Hibernate

**Application Web Java EE modernisée** | JDBC → JPA/Hibernate | Architecture 3-couches

---

## 🚀 Démarrage Rapide

### 1️⃣ Prérequis

- **Java 11+** : `java -version`
- **Maven 3.6+** : `mvn -version`
- **PostgreSQL** sur localhost (port 5432)
- **Tomcat 9+**

### 2️⃣ Configuration BD PostgreSQL

Tu dois créer la base de données avant de déployer l'app :

```sql
CREATE DATABASE masterannonce;
```

Vérification connexion :
```sql
psql -U postgres -d masterannonce -c "SELECT 1"
```

### 3️⃣ Build & Deploy

```bash
# Build du projet
mvn clean package -DskipTests

# Copier le WAR sur Tomcat
cp target/TP-S6-1.0-SNAPSHOT.war $CATALINA_HOME/webapps/

# Démarrer Tomcat
$CATALINA_HOME/bin/startup.sh
```

### 4️⃣ ⚠️ **CHARGER LES DONNÉES DE TEST (OBLIGATOIRE !)**

**SANS CES DONNÉES, L'APP NE FONCTIONNERA PAS !**

```bash
# Charger les données initiales
psql -U postgres -d masterannonce -f data-init.sql
```

Cette commande insère :
- 4 utilisateurs de test
- 8 catégories
- 7 annonces d'exemple (publiées, brouillons, archivées)

### 5️⃣ Accès à l'Application

```
URL: http://localhost:8080/TP-S6-1.0-SNAPSHOT/
```

### 6️⃣ Identifiants de Test

| Username | Password | Rôle |
|----------|----------|------|
| admin | admin123 | Administrateur |
| user1 | password1 | Utilisateur |
| user2 | password2 | Utilisateur |
| alice | alice123 | Utilisateur |

---

## 📋 Fonctionnalités

✅ **Authentification** - Login / Register / Logout avec session  
✅ **Listing Annonces** - Paginé (5 par page), filtre par statut  
✅ **CRUD Annonces** - Créer, Lire, Modifier, Supprimer  
✅ **États** - DRAFT (brouillon) → PUBLISHED (publié) → ARCHIVED (archivé)  
✅ **Recherche** - Par mot-clé, par catégorie  
✅ **Sécurité** - Authentification obligatoire, ownership checks  
✅ **Validation** - Bean Validation, messages d'erreur

---

## 🏗️ Architecture

```
Web Layer (Servlets + JSP)
    ↓
Service Layer (Transactions)
    ↓
DAO Layer (JPQL Queries)
    ↓
JPA/Hibernate (ORM)
    ↓
PostgreSQL Database
```

- **Servlets** : 10 fichiers (authentification + CRUD + états)
- **JSPs** : 6 pages (responsive design avec CSS)
- **DAOs** : GenericDAO + 3 DAOs spécialisés
- **Services** : 3 services avec gestion des transactions

---

## 🔑 Points Clés

### JPA/Hibernate
- Entités avec `@Entity`, `@ManyToOne`, `@OneToMany`
- Validations avec `@NotBlank`, `@Email`, `@Size`
- Lazy loading avec `FETCH JOIN` pour éviter N+1 queries

### JPQL (Pas de SQL Brut)
```java
em.createQuery(
    "SELECT a FROM Annonce a WHERE a.status = :status",
    Annonce.class
).setParameter("status", AnnonceStatus.PUBLISHED)
```

### Transactions au Niveau Service
```java
em.getTransaction().begin();
try {
    // opérations
    em.getTransaction().commit();
} catch (Exception e) {
    em.getTransaction().rollback();
}
```

### GenericDAO Pattern
```java
public abstract class GenericDAO<T> {
    // CRUD générique réutilisable
    public void save(T entity) { em.persist(entity); }
    public Optional<T> findById(Long id) { ... }
    public List<T> findAll() { ... }
}
```

---

## 📂 Structure Projet

```
src/main/java/...
├── model/              # Entités JPA
│   ├── User.java
│   ├── Category.java
│   ├── Annonce.java
│   └── AnnonceStatus.java
├── DAO/                # Data Access Objects
│   ├── GenericDAO.java
│   ├── UserDAO.java
│   ├── CategoryDAO.java
│   └── AnnonceDAO.java
├── service/            # Services (transactions)
│   ├── AnnonceService.java
│   ├── UserService.java
│   └── CategoryService.java
├── servlet/            # Controllers HTTP
│   ├── LoginServlet.java
│   ├── AnnonceListServlet.java
│   ├── AnnonceAddServlet.java
│   ├── AnnonceUpdateServlet.java
│   ├── AnnonceDeleteServlet.java
│   └── ... (10 fichiers)
└── util/
    └── JPAUtil.java    # EntityManager Factory

src/main/webapp/
├── index.jsp           # Page d'accueil
├── login.jsp           # Connexion
├── register.jsp        # Inscription
├── AnnonceList.jsp     # Listing annonces
├── AnnonceAdd.jsp      # Créer annonce
└── AnnonceUpdate.jsp   # Modifier annonce

src/main/resources/META-INF/
└── persistence.xml     # Configuration JPA/PostgreSQL
```

---

## 🧪 Tester l'Application

### Scénario 1 : Consulter les Annonces
1. Accès à http://localhost:8080/TP-S6-1.0-SNAPSHOT/
2. Cliquer "Annonces" ou "Voir les annonces"
3. **Résultat** : Liste des annonces publiées

### Scénario 2 : Créer une Annonce
1. Cliquer "S'inscrire" → remplir le formulaire
2. Se connecter avec vos identifiants
3. Cliquer "Créer une annonce"
4. Remplir le formulaire + catégorie
5. **Résultat** : Annonce créée en DRAFT

### Scénario 3 : Publier une Annonce
1. Se connecter
2. Aller à "Mes Annonces" (ou dans le listing si brouillon visible)
3. Cliquer "Publier" sur une annonce DRAFT
4. **Résultat** : Annonce devient PUBLISHED, visible à tous

### Scénario 4 : Archiver une Annonce
1. Se connecter comme propriétaire
2. Cliquer "Archiver" sur une annonce PUBLISHED
3. **Résultat** : Annonce devient ARCHIVED, plus visible

---

## 🐛 Dépannage

### ❌ "Connexion BD échouée"
```bash
# Vérifier que PostgreSQL est lancé
psql -U postgres -d masterannonce -c "SELECT 1"

# Vérifier les credentials dans persistence.xml
# User: postgres, Password: 123, Host: localhost
```

### ❌ "Aucune annonce n'apparaît"
```bash
# Charger les données !
psql -U postgres -d masterannonce -f data-init.sql
```

### ❌ "Erreur LazyInitializationException"
```
Ce bug a été corrigé avec FETCH JOIN.
Si le problème persiste :
1. Redéployer le WAR
2. Vider le cache Tomcat
3. Redémarrer Tomcat
```

---

## ✅ Checklist

- [ ] PostgreSQL démarré et masterannonce créée
- [ ] `mvn clean package -DskipTests` réussit ✅
- [ ] WAR copié dans `$CATALINA_HOME/webapps/`
- [ ] Tomcat démarré
- [ ] `psql -U postgres -d masterannonce -f data-init.sql` **exécuté** 🔴 **OBLIGATOIRE !**
- [ ] `http://localhost:8080/TP-S6-1.0-SNAPSHOT/` accessible
- [ ] Connexion "admin / admin123" fonctionne
- [ ] Annonces visibles dans le listing
- [ ] Créer une annonce fonctionne

---

## 📊 Technologies

- **Langage** : Java 11 + JSP/JSTL
- **Framework Web** : Servlet 4.0
- **ORM** : Hibernate 5.6.15 + JPA 2.2
- **BDD** : PostgreSQL
- **Build** : Maven
- **Serveur** : Tomcat 9+

---

## ⚠️ IMPORTANT : CHARGER data-init.sql

**Sans les données initiales, tu ne pourras pas tester !**

```bash
psql -U postgres -d masterannonce -f data-init.sql
```

Cela charge :
- 4 utilisateurs (admin, user1, user2, alice)
- 8 catégories (Électronique, Vêtements, etc.)
- 7 annonces d'exemple

**Après cette commande, rafraîchis la page et tu verras les annonces !** 🎉

---

**Bon développement ! 🚀**
- Entity Layer (ORM Mapping)

---

## 🔗 Références

- [JPA & Hibernate Documentation](https://hibernate.org/orm/)
- [JPQL Guide](https://hibernate.org/orm/documentation/)
- [Bean Validation](https://beanvalidation.org/)
- [Jakarta Persistence (JPA 3.0)](https://jakarta.ee/specifications/persistence/)

---

## 📝 Auteur

**Étudiant IUT Paris 8**  
TP S6 - Développement Avancé  
Février 2025

---

## 🎯 Conclusion

Ce TP démontre la transition d'une application Web Java EE traditional (JDBC basique) vers une architecture moderne avec JPA/Hibernate. Les avantages sont clairement visibles:

- ✅ Code plus maintenable et réutilisable (GenericDAO)
- ✅ Séparation des responsabilités (Web/Service/DAO)
- ✅ Protection contre les injections SQL (JPQL paramétré)
- ✅ Gestion de transaction centralisée (Service Layer)
- ✅ Validation déclarative (Bean Validation)
- ✅ Relations gérées automatiquement (ORM)

Le projet est prêt pour des extensions futures comme l'ajout de tests unitaires/intégration (JUnit + Mockito) ou le passage à des frameworks web modernes (Spring Boot, Quarkus).

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>MasterAnnonce - Accueil</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            flex-direction: column;
        }

        header {
            background-color: rgba(0, 0, 0, 0.7);
            color: white;
            padding: 20px;
            text-align: center;
        }

        header h1 {
            font-size: 2.5em;
            margin-bottom: 5px;
        }

        header p {
            font-size: 1.1em;
            color: #ddd;
        }

        .container {
            flex: 1;
            display: flex;
            justify-content: center;
            align-items: center;
            padding: 40px 20px;
        }

        .content {
            background: white;
            padding: 50px;
            border-radius: 10px;
            box-shadow: 0 10px 50px rgba(0, 0, 0, 0.2);
            text-align: center;
            max-width: 600px;
            width: 100%;
        }

        .content h2 {
            color: #333;
            margin-bottom: 20px;
            font-size: 2em;
        }

        .content p {
            color: #666;
            font-size: 1.1em;
            margin-bottom: 30px;
            line-height: 1.6;
        }

        .button-group {
            display: flex;
            gap: 15px;
            justify-content: center;
            flex-wrap: wrap;
        }

        .btn {
            padding: 12px 30px;
            font-size: 1em;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            text-decoration: none;
            display: inline-block;
            transition: all 0.3s ease;
            font-weight: bold;
        }

        .btn-primary {
            background-color: #667eea;
            color: white;
        }

        .btn-primary:hover {
            background-color: #5568d3;
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
        }

        .btn-secondary {
            background-color: #764ba2;
            color: white;
        }

        .btn-secondary:hover {
            background-color: #62398f;
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(118, 75, 162, 0.4);
        }

        .btn-outline {
            background-color: transparent;
            color: #667eea;
            border: 2px solid #667eea;
        }

        .btn-outline:hover {
            background-color: #667eea;
            color: white;
        }

        footer {
            background-color: rgba(0, 0, 0, 0.7);
            color: white;
            text-align: center;
            padding: 20px;
            font-size: 0.9em;
        }

        .features {
            text-align: left;
            margin-top: 40px;
            padding-top: 30px;
            border-top: 2px solid #eee;
        }

        .features h3 {
            color: #333;
            margin-bottom: 15px;
        }

        .features ul {
            list-style: none;
            color: #666;
        }

        .features li {
            padding: 8px 0;
            border-bottom: 1px solid #eee;
        }

        .features li:before {
            content: "✓ ";
            color: #667eea;
            font-weight: bold;
            margin-right: 8px;
        }
    </style>
</head>
<body>
    <header>
        <h1>🎯 MasterAnnonce</h1>
        <p>Plateforme de gestion d'annonces moderne avec JPA/Hibernate</p>
    </header>

    <div class="container">
        <div class="content">
            <h2>Bienvenue sur MasterAnnonce</h2>
            <p>Créez, publiez et gérez vos annonces facilement. L'application combine la puissance de Java EE avec JPA/Hibernate pour une solution robuste et moderne.</p>

            <c:if test="${sessionScope.userId != null}">
                <p>Connecté en tant que <strong>${sessionScope.username}</strong></p>
                <div class="button-group">
                    <a href="AnnonceList" class="btn btn-primary">📝 Voir les annonces</a>
                    <a href="AnnonceAdd" class="btn btn-secondary">➕ Créer une annonce</a>
                    <a href="logout" class="btn btn-outline">Déconnexion</a>
                </div>
            </c:if>

            <c:if test="${sessionScope.userId == null}">
                <div class="button-group">
                    <a href="login" class="btn btn-primary">Connexion</a>
                    <a href="register" class="btn btn-secondary">Inscription</a>
                    <a href="AnnonceList" class="btn btn-outline">Consulter les annonces</a>
                </div>
            </c:if>

            <div class="features">
                <h3>Fonctionnalités principales :</h3>
                <ul>
                    <li>Création et gestion d'annonces</li>
                    <li>Système de catégories</li>
                    <li>Authentification sécurisée</li>
                    <li>Pagination des résultats</li>
                    <li>Architecture en couches (Web/Service/DAO)</li>
                    <li>Utilisation de JPA/Hibernate pour la persistence</li>
                </ul>
            </div>
        </div>
    </div>

    <footer>
        <p>&copy; 2025 MasterAnnonce - TP S6 | Partie de la refonte JPA/Hibernate</p>
    </footer>
</body>
</html>
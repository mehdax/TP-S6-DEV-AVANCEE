<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>MasterAnnonce - Accueil</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f5f5f5;
            margin: 0;
            padding: 0;
            display: flex;
            flex-direction: column;
            min-height: 100vh;
        }

        header {
            background-color: #0066cc;
            color: white;
            padding: 20px;
            text-align: center;
        }

        header h1 {
            margin: 0;
            font-size: 2em;
        }

        header p {
            color: #ddd;
            margin: 5px 0 0 0;
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
            padding: 40px;
            border: 1px solid #ddd;
            text-align: center;
            max-width: 600px;
            width: 100%;
        }

        .content h2 {
            color: #333;
            margin-bottom: 20px;
        }

        .content p {
            color: #666;
            margin-bottom: 20px;
        }

        .button-group {
            display: flex;
            gap: 10px;
            justify-content: center;
            flex-wrap: wrap;
            margin-bottom: 30px;
        }

        .btn {
            padding: 10px 20px;
            border: none;
            border-radius: 0;
            cursor: pointer;
            text-decoration: none;
            display: inline-block;
            font-size: 1em;
        }

        .btn-primary {
            background-color: #0066cc;
            color: white;
        }

        .btn-primary:hover {
            background-color: #0052a3;
        }

        .btn-secondary {
            background-color: #666;
            color: white;
        }

        .btn-secondary:hover {
            background-color: #333;
        }

        .btn-outline {
            background-color: white;
            color: #0066cc;
            border: 1px solid #0066cc;
        }

        .btn-outline:hover {
            background-color: #0066cc;
            color: white;
        }

        footer {
            background-color: #333;
            color: white;
            text-align: center;
            padding: 15px;
            font-size: 0.9em;
        }

        .features {
            text-align: left;
            margin-top: 30px;
            padding-top: 20px;
            border-top: 1px solid #ddd;
        }

        .features h3 {
            color: #333;
            margin-bottom: 15px;
        }

        .features ul {
            list-style: none;
            color: #666;
            padding: 0;
        }

        .features li {
            padding: 8px 0;
            border-bottom: 1px solid #eee;
        }

        .features li:before {
            content: "- ";
            color: #0066cc;
            font-weight: bold;
            margin-right: 8px;
        }
    </style>
</head>
<body>
    <header>
        <h1>MasterAnnonce</h1>
        
    </header>

    <div class="container">
        <div class="content">
            <h2>Bienvenue sur MasterAnnonce</h2>
          

            <c:if test="${sessionScope.userId != null}">
                <p>Connecté en tant que <strong>${sessionScope.username}</strong></p>
                <div class="button-group">
                    <a href="AnnonceList" class="btn btn-primary">Voir les annonces</a>
                    <a href="AnnonceAdd" class="btn btn-secondary">Créer une annonce</a>
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

        </div>
    </div>
</body>
</html>
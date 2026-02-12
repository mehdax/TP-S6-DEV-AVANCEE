<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Connexion - MasterAnnonce</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f5f5f5;
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
            padding: 20px;
            margin: 0;
        }

        .card {
            background: white;
            padding: 30px;
            border: 1px solid #ddd;
            max-width: 400px;
            width: 100%;
        }

        .card h1 {
            color: #333;
            text-align: center;
            margin-bottom: 20px;
            font-size: 1.8em;
        }

        .form-group {
            margin-bottom: 15px;
        }

        label {
            display: block;
            margin-bottom: 5px;
            color: #333;
            font-weight: bold;
        }

        input[type="text"],
        input[type="password"] {
            width: 100%;
            padding: 10px;
            border: 1px solid #ccc;
            font-size: 1em;
            box-sizing: border-box;
        }

        .btn {
            width: 100%;
            padding: 10px;
            background-color: #0066cc;
            color: white;
            border: none;
            cursor: pointer;
            font-size: 1em;
            font-weight: bold;
        }

        .btn:hover {
            background-color: #0052a3;
        }

        .error {
            background-color: #ffcccc;
            color: #cc0000;
            padding: 10px;
            margin-bottom: 20px;
            border: 1px solid #cc0000;
        }

        .success {
            background-color: #ccffcc;
            color: #008000;
            padding: 10px;
            margin-bottom: 20px;
            border: 1px solid #008000;
        }

        .links {
            text-align: center;
            margin-top: 15px;
            color: #666;
        }

        .links a {
            color: #0066cc;
            text-decoration: underline;
            margin: 0 5px;
        }

        .links a:hover {
            color: #0052a3;
        }

        .back-link {
            display: block;
            color: #0066cc;
            text-decoration: underline;
            margin-bottom: 20px;
            font-weight: bold;
        }

        .back-link:hover {
            color: #0052a3;
        }
    </style>
</head>
<body>
    <div class="card">
        <a href="index.jsp" class="back-link">← Retour à l'accueil</a>

        <h1>Connexion</h1>

        <c:if test="${not empty error}">
            <div class="error">${error}</div>
        </c:if>

        <c:if test="${not empty success}">
            <div class="success">${success}</div>
        </c:if>

        <form method="POST" action="login">
            <div class="form-group">
                <label for="username">Nom d'utilisateur</label>
                <input type="text" id="username" name="username" required autofocus>
            </div>

            <div class="form-group">
                <label for="password">Mot de passe</label>
                <input type="password" id="password" name="password" required>
            </div>

            <button type="submit" class="btn">Se connecter</button>
        </form>

        <div class="links">
            <p>Pas encore de compte ? <a href="register">S'inscrire</a></p>
        </div>
    </div>
</body>
</html>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Ajouter une annonce</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            max-width: 600px;
            margin: 50px auto;
            padding: 20px;
            background-color: #f4f4f4;
        }
        .container {
            background-color: white;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        h1 {
            color: #333;
            text-align: center;
        }
        .form-group {
            margin-bottom: 20px;
        }
        label {
            display: block;
            margin-bottom: 5px;
            color: #555;
            font-weight: bold;
        }
        input[type="text"],
        input[type="email"],
        textarea {
            width: 100%;
            padding: 10px;
            border: 1px solid #ddd;
            border-radius: 4px;
            font-size: 14px;
            box-sizing: border-box;
        }
        textarea {
            resize: vertical;
            min-height: 100px;
        }
        button {
            width: 100%;
            padding: 12px;
            background-color: #4CAF50;
            color: white;
            border: none;
            border-radius: 4px;
            font-size: 16px;
            cursor: pointer;
        }
        button:hover {
            background-color: #45a049;
        }
        .error {
            background-color: #f44336;
            color: white;
            padding: 10px;
            border-radius: 4px;
            margin-bottom: 20px;
        }
        .link-back {
            display: block;
            text-align: center;
            margin-top: 20px;
            color: #4CAF50;
            text-decoration: none;
        }
        .link-back:hover {
            text-decoration: underline;
        }
    </style>
</head>
<body>
<div class="container">
    <h1> Nouvelle Annonce</h1>

    <c:if test="${not empty error}">
        <div class="error">
            <c:out value="${error}" />
        </div>
    </c:if>

    <form method="post" action="AnnonceAdd">
        <div class="form-group">
            <label for="title">Titre *</label>
            <input type="text" id="title" name="title" required maxlength="64"
                   placeholder="Ex: Vends vélo">
        </div>

        <div class="form-group">
            <label for="description">Description *</label>
            <textarea id="description" name="description" required maxlength="256"
                      placeholder="Décrivez votre annonce..."></textarea>
        </div>

        <div class="form-group">
            <label for="adress">Adresse *</label>
            <input type="text" id="adress" name="adress" required maxlength="64"
                   placeholder="Ex: 12 rue de Paris">
        </div>

        <div class="form-group">
            <label for="mail">Email *</label>
            <input type="email" id="mail" name="mail" required maxlength="64"
                   placeholder="votre.email@example.com">
        </div>

        <button type="submit"> Publier l'annonce</button>
    </form>

    <a href="AnnonceList" class="link-back"> Retour à la liste</a>
</div>
</body>
</html>

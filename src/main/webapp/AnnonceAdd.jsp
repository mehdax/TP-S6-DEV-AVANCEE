<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Créer une annonce - MasterAnnonce</title>
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
            padding: 20px;
        }

        .container {
            max-width: 600px;
            margin: 0 auto;
            background: white;
            padding: 40px;
            border-radius: 10px;
            box-shadow: 0 10px 50px rgba(0, 0, 0, 0.2);
        }

        h1 {
            color: #333;
            margin-bottom: 10px;
            font-size: 2em;
        }

        .subtitle {
            color: #666;
            margin-bottom: 30px;
        }

        .form-group {
            margin-bottom: 25px;
        }

        label {
            display: block;
            margin-bottom: 8px;
            color: #333;
            font-weight: bold;
        }

        input[type="text"],
        input[type="email"],
        textarea,
        select {
            width: 100%;
            padding: 12px;
            border: 1px solid #ddd;
            border-radius: 5px;
            font-size: 1em;
            font-family: inherit;
            transition: border-color 0.3s ease;
        }

        input[type="text"]:focus,
        input[type="email"]:focus,
        textarea:focus,
        select:focus {
            outline: none;
            border-color: #667eea;
            box-shadow: 0 0 5px rgba(102, 126, 234, 0.3);
        }

        textarea {
            resize: vertical;
            min-height: 120px;
        }

        .helper-text {
            font-size: 0.85em;
            color: #888;
            margin-top: 5px;
        }

        .error {
            background-color: #f8d7da;
            color: #721c24;
            padding: 15px;
            border-radius: 5px;
            margin-bottom: 20px;
            border: 1px solid #f5c6cb;
        }

        .button-group {
            display: flex;
            gap: 15px;
            margin-top: 30px;
        }

        .btn {
            flex: 1;
            padding: 12px;
            border: none;
            border-radius: 5px;
            font-size: 1em;
            font-weight: bold;
            cursor: pointer;
            transition: all 0.3s ease;
        }

        .btn-submit {
            background-color: #667eea;
            color: white;
        }

        .btn-submit:hover {
            background-color: #5568d3;
            transform: translateY(-2px);
        }

        .btn-cancel {
            background-color: #e0e0e0;
            color: #333;
            text-decoration: none;
            display: flex;
            align-items: center;
            justify-content: center;
        }

        .btn-cancel:hover {
            background-color: #d0d0d0;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>✍️ Créer une annonce</h1>
        <p class="subtitle">Remplissez le formulaire ci-dessous pour créer votre annonce</p>

        <c:if test="${not empty error}">
            <div class="error">${error}</div>
        </c:if>

        <form method="POST" action="AnnonceAdd">
            <div class="form-group">
                <label for="title">Titre *</label>
                <input type="text" id="title" name="title" required minlength="3" maxlength="64"
                       placeholder="Ex: Vente Apple MacBook Pro 15 pouces">
                <div class="helper-text">Entre 3 et 64 caractères</div>
            </div>

            <div class="form-group">
                <label for="description">Description *</label>
                <textarea id="description" name="description" required minlength="10" maxlength="256"
                          placeholder="Décrivez votre annonce en détail..."></textarea>
                <div class="helper-text">Entre 10 et 256 caractères</div>
            </div>

            <div class="form-group">
                <label for="categoryId">Catégorie *</label>
                <select id="categoryId" name="categoryId" required>
                    <option value="">-- Sélectionnez une catégorie --</option>
                    <c:forEach var="category" items="${categories}">
                        <option value="${category.id}">${category.label}</option>
                    </c:forEach>
                </select>
            </div>

            <div class="form-group">
                <label for="adress">Adresse *</label>
                <input type="text" id="adress" name="adress" required maxlength="64"
                       placeholder="Ex: 75001 Paris">
                <div class="helper-text">Maximum 64 caractères</div>
            </div>

            <div class="form-group">
                <label for="mail">Email de contact *</label>
                <input type="email" id="mail" name="mail" required maxlength="64"
                       placeholder="votre.email@example.com">
                <div class="helper-text">Adresse email valide</div>
            </div>

            <div class="button-group">
                <button type="submit" class="btn btn-submit">📤 Créer l'annonce</button>
                <a href="AnnonceList" class="btn btn-cancel">❌ Annuler</a>
            </div>
        </form>
    </div>
</body>
</html>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Modifier une annonce - MasterAnnonce</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f5f5f5;
            padding: 20px;
            margin: 0;
        }
        
        .container {
            max-width: 600px;
            margin: 0 auto;
            background: white;
            padding: 20px;
            border: 1px solid #ddd;
        }
        
        h1 {
            color: #333;
            margin-bottom: 10px;
        }
        
        .subtitle {
            color: #666;
            margin-bottom: 20px;
        }
        
        .form-group {
            margin-bottom: 15px;
        }
        
        label {
            display: block;
            margin-bottom: 5px;
            font-weight: bold;
            color: #333;
        }
        
        input[type="text"],
        input[type="email"],
        textarea,
        select {
            width: 100%;
            padding: 10px;
            border: 1px solid #ccc;
            font-size: 1em;
            box-sizing: border-box;
        }
        
        textarea {
            min-height: 100px;
            resize: vertical;
        }
        
        .helper-text {
            font-size: 0.9em;
            color: #666;
            margin-top: 3px;
        }
        
        .error {
            background-color: #ffcccc;
            color: #cc0000;
            padding: 10px;
            margin-bottom: 20px;
            border: 1px solid #cc0000;
        }
        
        .button-group {
            display: flex;
            gap: 10px;
            margin-top: 20px;
        }
        
        button {
            flex: 1;
            background-color: #0066cc;
            color: white;
            padding: 10px;
            border: none;
            cursor: pointer;
            font-size: 1em;
        }
        
        button:hover {
            background-color: #0052a3;
        }
        
        .btn-cancel {
            background-color: #999;
            text-decoration: none;
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
        }
        
        .btn-cancel:hover {
            background-color: #666;
        }
        
        .no-annonce {
            text-align: center;
            padding: 40px;
            color: #666;
        }
        
        .no-annonce h2 {
            color: #333;
        }
        
        .no-annonce a {
            color: #0066cc;
            text-decoration: underline;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>Modifier une annonce</h1>
        <p class="subtitle">Modifiez les informations de votre annonce</p>

        <c:if test="${not empty error}">
            <div class="error">${error}</div>
        </c:if>

        <c:choose>
            <c:when test="${not empty annonce}">
                <form method="POST" action="AnnonceUpdate">
                    <input type="hidden" name="id" value="${annonce.id}">

                    <div class="form-group">
                        <label for="title">Titre *</label>
                        <input type="text" id="title" name="title" required minlength="3" maxlength="64"
                               value="<c:out value='${annonce.title}' />">
                        <div class="helper-text">Entre 3 et 64 caractères</div>
                    </div>

                    <div class="form-group">
                        <label for="description">Description *</label>
                        <textarea id="description" name="description" required minlength="10" maxlength="256"><c:out value="${annonce.description}" /></textarea>
                        <div class="helper-text">Entre 10 et 256 caractères</div>
                    </div>

                    <div class="form-group">
                        <label for="categoryId">Catégorie *</label>
                        <select id="categoryId" name="categoryId" required>
                            <option value="">-- Sélectionnez une catégorie --</option>
                            <c:forEach var="category" items="${categories}">
                                <option value="${category.id}" <c:if test="${annonce.category.id == category.id}">selected</c:if>>${category.label}</option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="form-group">
                        <label for="adress">Adresse *</label>
                        <input type="text" id="adress" name="adress" required maxlength="64"
                               value="<c:out value='${annonce.adress}' />">
                        <div class="helper-text">Maximum 64 caractères</div>
                    </div>

                    <div class="form-group">
                        <label for="mail">Email de contact *</label>
                        <input type="email" id="mail" name="mail" required maxlength="64"
                               value="<c:out value='${annonce.mail}' />">
                        <div class="helper-text">Adresse email valide</div>
                    </div>

                    <div class="button-group">
                        <button type="submit" class="btn btn-submit">Enregistrer les modifications</button>
                        <a href="AnnonceList" class="btn btn-cancel">Annuler</a>
                    </div>
                </form>
            </c:when>
            <c:otherwise>
                <div class="no-annonce">
                    <h2>Annonce non trouvée</h2>
                    <p>L'annonce que vous cherchez n'existe pas ou a été supprimée.</p>
                    <br>
                    <a href="AnnonceList" style="color: #667eea; text-decoration: underline;">Retourner à la liste</a>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</body>
</html>

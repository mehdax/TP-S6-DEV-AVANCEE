<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>Liste des annonces</title>
  <style>
    body {
      font-family: Arial, sans-serif;
      margin: 0;
      padding: 20px;
      background-color: #f4f4f4;
    }
    .container {
      max-width: 1000px;
      margin: 0 auto;
    }
    h1 {
      color: #333;
      text-align: center;
    }
    .header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 30px;
    }
    .btn-add {
      background-color: #4CAF50;
      color: white;
      padding: 12px 24px;
      text-decoration: none;
      border-radius: 4px;
      font-weight: bold;
    }
    .btn-add:hover {
      background-color: #45a049;
    }
    .annonce-card {
      background-color: white;
      padding: 20px;
      margin-bottom: 20px;
      border-radius: 8px;
      box-shadow: 0 2px 5px rgba(0,0,0,0.1);
    }
    .annonce-title {
      font-size: 20px;
      font-weight: bold;
      color: #333;
      margin-bottom: 10px;
    }
    .annonce-description {
      color: #666;
      margin-bottom: 15px;
    }
    .annonce-info {
      display: flex;
      justify-content: space-between;
      align-items: center;
      font-size: 14px;
      color: #888;
      border-top: 1px solid #eee;
      padding-top: 10px;
    }
    .annonce-details {
      flex: 1;
    }
    .annonce-actions {
      display: flex;
      gap: 10px;
    }
    .btn {
      padding: 6px 12px;
      text-decoration: none;
      border-radius: 4px;
      font-size: 12px;
    }
    .btn-edit {
      background-color: #2196F3;
      color: white;
    }
    .btn-edit:hover {
      background-color: #0b7dda;
    }
    .btn-delete {
      background-color: #f44336;
      color: white;
    }
    .btn-delete:hover {
      background-color: #da190b;
    }
    .no-annonce {
      text-align: center;
      padding: 40px;
      background-color: white;
      border-radius: 8px;
      color: #888;
    }
    .success {
      background-color: #4CAF50;
      color: white;
      padding: 10px;
      border-radius: 4px;
      margin-bottom: 20px;
      text-align: center;
    }
    .error {
      background-color: #f44336;
      color: white;
      padding: 10px;
      border-radius: 4px;
      margin-bottom: 20px;
      text-align: center;
    }
  </style>
  <script>
    function confirmDelete(id, title) {
      if (confirm("Êtes-vous sûr de vouloir supprimer l'annonce \"" + title + "\" ?")) {
        window.location.href = "AnnonceDelete?id=" + id;
      }
    }
  </script>
</head>
<body>
<div class="container">
  <div class="header">
    <h1>Liste des Annonces</h1>
    <a href="AnnonceAdd" class="btn-add">Nouvelle annonce</a>
  </div>

  <c:if test="${param.success eq 'delete'}">
    <div class="success"> Annonce supprimée avec succès</div>
  </c:if>

  <c:if test="${param.error eq 'delete'}">
    <div class="error"> Erreur lors de la suppression</div>
  </c:if>

  <c:choose>
    <c:when test="${empty annonces}">
      <div class="no-annonce">
        <h2>Aucune annonce disponible</h2>
        <p>Commencez par créer votre première annonce !</p>
      </div>
    </c:when>
    <c:otherwise>
      <c:forEach var="annonce" items="${annonces}">
        <div class="annonce-card">
          <div class="annonce-title">
            <c:out value="${annonce.title}" />
          </div>
          <div class="annonce-description">
            <c:out value="${annonce.description}" />
          </div>
          <div class="annonce-info">
            <div class="annonce-details">
               <c:out value="${annonce.adress}" /> |
               <c:out value="${annonce.mail}" /> |
               <fmt:formatDate value="${annonce.date}" pattern="dd/MM/yyyy HH:mm" />
            </div>
            <div class="annonce-actions">
              <a href="AnnonceUpdate?id=${annonce.id}" class="btn btn-edit"> Modifier</a>
              <a href="javascript:void(0)"
                 onclick="confirmDelete(${annonce.id}, '${fn:escapeXml(annonce.title)}')"
                 class="btn btn-delete"> Supprimer</a>
            </div>
          </div>
        </div>
      </c:forEach>
    </c:otherwise>
  </c:choose>
</div>
</body>
</html>
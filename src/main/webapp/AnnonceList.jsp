<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Liste des annonces - MasterAnnonce</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f5f5f5;
            padding: 20px;
            margin: 0;
        }

        .container {
            max-width: 1000px;
            margin: 0 auto;
        }

        header {
            background-color: #0066cc;
            color: white;
            padding: 15px 20px;
            margin-bottom: 20px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        header h1 {
            margin: 0;
        }

        header .user-nav {
            display: flex;
            gap: 10px;
        }

        header a {
            color: white;
            text-decoration: none;
            padding: 5px 10px;
            border: 1px solid white;
        }

        header a:hover {
            background-color: rgba(255, 255, 255, 0.2);
        }

        .controls {
            background-color: white;
            padding: 15px 20px;
            margin-bottom: 20px;
            border-bottom: 1px solid #ddd;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .controls h2 {
            margin: 0;
        }

        .controls a {
            background-color: #0066cc;
            color: white;
            padding: 8px 15px;
            text-decoration: none;
        }

        .controls a:hover {
            background-color: #0052a3;
        }

        .success {
            background-color: #ccffcc;
            color: #008000;
            padding: 10px;
            margin-bottom: 20px;
            border: 1px solid #008000;
        }

        .error {
            background-color: #ffcccc;
            color: #cc0000;
            padding: 10px;
            margin-bottom: 20px;
            border: 1px solid #cc0000;
        }

        .annonce-card {
            background-color: white;
            padding: 20px;
            margin-bottom: 15px;
            border: 1px solid #ddd;
        }

        .annonce-title {
            font-size: 1.3em;
            font-weight: bold;
            color: #333;
            margin-bottom: 10px;
        }

        .annonce-description {
            color: #666;
            margin-bottom: 10px;
        }

        .annonce-meta {
            font-size: 0.9em;
            color: #888;
            margin-bottom: 10px;
        }

        .meta-item {
            margin-right: 20px;
        }

        .badge {
            display: inline-block;
            background-color: #0066cc;
            color: white;
            padding: 3px 8px;
            font-size: 0.9em;
        }

        .annonce-footer {
            border-top: 1px solid #eee;
            padding-top: 10px;
            display: flex;
            justify-content: space-between;
        }

        .annonce-actions {
            display: flex;
            gap: 10px;
        }

        .btn {
            padding: 8px 12px;
            text-decoration: none;
            font-size: 0.9em;
            cursor: pointer;
            border: 1px solid #ccc;
        }

        .btn-primary {
            background-color: #0066cc;
            color: white;
            border: none;
        }

        .btn-primary:hover {
            background-color: #0052a3;
        }

        .btn-warning {
            background-color: #ff9800;
            color: white;
            border: none;
        }

        .btn-warning:hover {
            background-color: #e68900;
        }

        .btn-danger {
            background-color: #cc0000;
            color: white;
            border: none;
        }

        .btn-danger:hover {
            background-color: #990000;
        }

        .no-annonce {
            text-align: center;
            padding: 40px;
            background-color: white;
            border: 1px solid #ddd;
            color: #888;
        }

        .pagination {
            text-align: center;
            margin-top: 20px;
        }

        .pagination a {
            padding: 8px 12px;
            margin: 0 5px;
            border: 1px solid #ddd;
            text-decoration: none;
            color: #0066cc;
        }

        .pagination a:hover {
            background-color: #0066cc;
            color: white;
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
        <header>
            <h1>Annonces</h1>
            <div class="user-nav">
                <c:if test="${sessionScope.userId != null}">
                    <span>Bienvenue, ${sessionScope.username}!</span>
                    <a href="AnnonceAdd">Créer une annonce</a>
                    <a href="logout">Déconnexion</a>
                </c:if>
                <c:if test="${sessionScope.userId == null}">
                    <a href="login">Connexion</a>
                    <a href="register">Inscription</a>
                </c:if>
                <a href="index.jsp">Accueil</a>
            </div>
        </header>

        <div class="controls">
            <h2>Annonces publiées</h2>
            <c:if test="${sessionScope.userId != null}">
                <a href="AnnonceAdd">Nouvelle annonce</a>
            </c:if>
        </div>

        <c:if test="${param.success eq 'deleted'}">
            <div class="success">Annonce supprimée avec succès</div>
        </c:if>

        <c:if test="${param.success eq 'updated'}">
            <div class="success">Annonce mise à jour avec succès</div>
        </c:if>

        <c:if test="${param.published eq 'true'}">
            <div class="success">Annonce publiée avec succès</div>
        </c:if>

        <c:if test="${param.archived eq 'true'}">
            <div class="success">Annonce archivée avec succès</div>
        </c:if>

        <c:if test="${param.error eq 'delete'}">
            <div class="error">Erreur lors de la suppression</div>
        </c:if>

        <c:if test="${not empty error}">
            <div class="error">${error}</div>
        </c:if>

        <c:choose>
            <c:when test="${empty annonces}">
                <div class="no-annonce">
                    <h2>Aucune annonce disponible</h2>
                    <p>Revenez plus tard pour découvrir les annonces publiées.</p>
                    <c:if test="${sessionScope.userId != null}">
                        <p><a href="AnnonceAdd" style="color: #667eea; text-decoration: underline;">Créer votre première annonce</a></p>
                    </c:if>
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
                        <div class="annonce-meta">
                            <div class="meta-item"><c:out value="${annonce.adress}" /></div>
                            <div class="meta-item"><c:out value="${annonce.mail}" /></div>
                            <div class="meta-item"><fmt:formatDate value="${annonce.date}" pattern="dd/MM/yyyy HH:mm" /></div>
                            <div class="meta-item"><span class="badge"><c:out value="${annonce.category.label}" /></span></div>
                            <div class="meta-item"><c:out value="${annonce.author.username}" /></div>
                        </div>
                        <div class="annonce-footer">
                            <div>
                                <span class="badge" style="background-color: #28a745;">Publié</span>
                            </div>
                            <div class="annonce-actions">
                                <c:if test="${sessionScope.userId == annonce.author.id}">
                                    <a href="AnnonceUpdate?id=${annonce.id}" class="btn btn-primary">Modifier</a>
                                    <c:if test="${annonce.status == 'PUBLISHED'}">
                                        <a href="AnnonceArchive?id=${annonce.id}" class="btn btn-warning">Archiver</a>
                                    </c:if>
                                    <a href="javascript:void(0)" onclick="confirmDelete(${annonce.id}, '${fn:escapeXml(annonce.title)}')" class="btn btn-danger">Supprimer</a>
                                </c:if>
                                <c:if test="${sessionScope.userId != annonce.author.id && sessionScope.userId != null}">
                                    <a href="mailto:${annonce.mail}" class="btn btn-primary">Contacter</a>
                                </c:if>
                            </div>
                        </div>
                    </div>
                </c:forEach>

                <!-- Pagination -->
                <c:if test="${page > 0}">
                    <div class="pagination">
                        <a href="AnnonceList?page=${page - 1}">← Précédent</a>
                    </div>
                </c:if>
            </c:otherwise>
        </c:choose>
    </div>
</body>
</html>
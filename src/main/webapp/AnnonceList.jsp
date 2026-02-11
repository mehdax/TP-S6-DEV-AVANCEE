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
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background-color: #f5f5f5;
            padding: 20px;
        }

        .container {
            max-width: 1000px;
            margin: 0 auto;
        }

        header {
            background-color: #667eea;
            color: white;
            padding: 20px 30px;
            border-radius: 8px 8px 0 0;
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 0;
        }

        header h1 {
            font-size: 2em;
        }

        header .user-nav {
            display: flex;
            gap: 15px;
            align-items: center;
        }

        header a {
            color: white;
            text-decoration: none;
            padding: 8px 15px;
            border: 1px solid white;
            border-radius: 5px;
            transition: all 0.3s ease;
        }

        header a:hover {
            background-color: rgba(255, 255, 255, 0.2);
        }

        .controls {
            background-color: white;
            padding: 20px 30px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            border-bottom: 1px solid #eee;
        }

        .controls a {
            background-color: #667eea;
            color: white;
            padding: 12px 24px;
            text-decoration: none;
            border-radius: 5px;
            font-weight: bold;
            transition: all 0.3s ease;
        }

        .controls a:hover {
            background-color: #5568d3;
            transform: translateY(-2px);
        }

        .success {
            background-color: #d4edda;
            color: #155724;
            padding: 15px;
            border-radius: 5px;
            margin-bottom: 20px;
            border: 1px solid #c3e6cb;
        }

        .error {
            background-color: #f8d7da;
            color: #721c24;
            padding: 15px;
            border-radius: 5px;
            margin-bottom: 20px;
            border: 1px solid #f5c6cb;
        }

        .annonce-card {
            background-color: white;
            padding: 25px;
            margin-bottom: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
            transition: all 0.3s ease;
        }

        .annonce-card:hover {
            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.15);
        }

        .annonce-title {
            font-size: 1.5em;
            font-weight: bold;
            color: #333;
            margin-bottom: 10px;
        }

        .annonce-description {
            color: #666;
            margin-bottom: 15px;
            line-height: 1.6;
        }

        .annonce-meta {
            display: flex;
            gap: 20px;
            font-size: 0.9em;
            color: #888;
            margin-bottom: 15px;
            flex-wrap: wrap;
        }

        .meta-item {
            display: flex;
            align-items: center;
            gap: 5px;
        }

        .badge {
            display: inline-block;
            background-color: #667eea;
            color: white;
            padding: 4px 10px;
            border-radius: 20px;
            font-size: 0.85em;
        }

        .status-published {
            background-color: #28a745;
        }

        .status-draft {
            background-color: #ffc107;
            color: #333;
        }

        .annonce-footer {
            display: flex;
            justify-content: space-between;
            align-items: center;
            border-top: 1px solid #eee;
            padding-top: 15px;
        }

        .annonce-actions {
            display: flex;
            gap: 10px;
        }

        .btn {
            padding: 8px 15px;
            text-decoration: none;
            border-radius: 5px;
            font-size: 0.9em;
            font-weight: bold;
            cursor: pointer;
            border: none;
            transition: all 0.3s ease;
        }

        .btn-primary {
            background-color: #2196F3;
            color: white;
        }

        .btn-primary:hover {
            background-color: #0b7dda;
        }

        .btn-warning {
            background-color: #ffc107;
            color: #333;
        }

        .btn-warning:hover {
            background-color: #e0a800;
        }

        .btn-danger {
            background-color: #f44336;
            color: white;
        }

        .btn-danger:hover {
            background-color: #da190b;
        }

        .no-annonce {
            text-align: center;
            padding: 60px 40px;
            background-color: white;
            border-radius: 8px;
            color: #888;
        }

        .no-annonce h2 {
            color: #333;
            margin-bottom: 15px;
        }

        .pagination {
            text-align: center;
            margin-top: 30px;
            display: flex;
            justify-content: center;
            gap: 10px;
        }

        .pagination a, .pagination span {
            padding: 8px 12px;
            border: 1px solid #ddd;
            border-radius: 5px;
            text-decoration: none;
            color: #667eea;
        }

        .pagination a:hover {
            background-color: #667eea;
            color: white;
        }

        .pagination .current {
            background-color: #667eea;
            color: white;
            border-color: #667eea;
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
            <h1>📋 Annonces</h1>
            <div class="user-nav">
                <c:if test="${sessionScope.userId != null}">
                    <span>Bienvenue, ${sessionScope.username}!</span>
                    <a href="AnnonceAdd">➕ Créer une annonce</a>
                    <a href="logout">🚪 Déconnexion</a>
                </c:if>
                <c:if test="${sessionScope.userId == null}">
                    <a href="login">Connexion</a>
                    <a href="register">Inscription</a>
                </c:if>
                <a href="index.jsp">🏠 Accueil</a>
            </div>
        </header>

        <div class="controls">
            <h2>Annonces publiées</h2>
            <c:if test="${sessionScope.userId != null}">
                <a href="AnnonceAdd">➕ Nouvelle annonce</a>
            </c:if>
        </div>

        <c:if test="${param.success eq 'deleted'}">
            <div class="success">✓ Annonce supprimée avec succès</div>
        </c:if>

        <c:if test="${param.success eq 'updated'}">
            <div class="success">✓ Annonce mise à jour avec succès</div>
        </c:if>

        <c:if test="${param.published eq 'true'}">
            <div class="success">✓ Annonce publiée avec succès</div>
        </c:if>

        <c:if test="${param.archived eq 'true'}">
            <div class="success">✓ Annonce archivée avec succès</div>
        </c:if>

        <c:if test="${param.error eq 'delete'}">
            <div class="error">✗ Erreur lors de la suppression</div>
        </c:if>

        <c:if test="${not empty error}">
            <div class="error">✗ ${error}</div>
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
                            <div class="meta-item">📍 <c:out value="${annonce.adress}" /></div>
                            <div class="meta-item">📧 <c:out value="${annonce.mail}" /></div>
                            <div class="meta-item">📅 <fmt:formatDate value="${annonce.date}" pattern="dd/MM/yyyy HH:mm" /></div>
                            <div class="meta-item">📂 <span class="badge"><c:out value="${annonce.category.label}" /></span></div>
                            <div class="meta-item">👤 <c:out value="${annonce.author.username}" /></div>
                        </div>
                        <div class="annonce-footer">
                            <div>
                                <span class="badge" style="background-color: #28a745;">✓ Publié</span>
                            </div>
                            <div class="annonce-actions">
                                <c:if test="${sessionScope.userId == annonce.author.id}">
                                    <a href="AnnonceUpdate?id=${annonce.id}" class="btn btn-primary">✏️ Modifier</a>
                                    <c:if test="${annonce.status == 'PUBLISHED'}">
                                        <a href="AnnonceArchive?id=${annonce.id}" class="btn btn-warning">📦 Archiver</a>
                                    </c:if>
                                    <a href="javascript:void(0)" onclick="confirmDelete(${annonce.id}, '${fn:escapeXml(annonce.title)}')" class="btn btn-danger">🗑️ Supprimer</a>
                                </c:if>
                                <c:if test="${sessionScope.userId != annonce.author.id && sessionScope.userId != null}">
                                    <a href="mailto:${annonce.mail}" class="btn btn-primary">✉️ Contacter</a>
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
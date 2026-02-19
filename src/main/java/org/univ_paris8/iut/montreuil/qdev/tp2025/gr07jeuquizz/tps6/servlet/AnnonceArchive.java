package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.service.AnnonceService;

import java.io.IOException;

/**
 * Servlet pour archiver une annonce (transition PUBLISHED → ARCHIVED)
 * 
 * @author TP Équipe
 * @version 1.0
 */
@WebServlet("/AnnonceArchive")
public class AnnonceArchive extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        // Vérifier que l'utilisateur est authentifié
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        try {
            Long userId = (Long) session.getAttribute("userId");
            Long annonceId = Long.parseLong(request.getParameter("id"));

            AnnonceService annonceService = new AnnonceService();

            // Récupérer l'annonce et vérifier que c'est l'auteur
            var annonce = annonceService.getAnnonceById(annonceId)
                    .orElseThrow(() -> new IllegalArgumentException("Annonce non trouvée"));

            if (!annonce.getAuthor().getId().equals(userId)) {
                request.setAttribute("error", "Vous ne pouvez pas archiver cette annonce");
                request.getRequestDispatcher("/AnnonceList").forward(request, response);
                return;
            }

            // Archiver l'annonce
            annonceService.archiveAnnonce(annonceId, userId);
            response.sendRedirect(request.getContextPath() + "/AnnonceList?archived=true");

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID invalide");
        } catch (IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Annonce non trouvée");
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}

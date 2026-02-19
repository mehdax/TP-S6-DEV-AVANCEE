package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.servlet;

import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.service.AnnonceService;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.model.Annonce;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Optional;

@WebServlet("/AnnonceDelete")
public class AnnonceDelete extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Vérifier l'authentification
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("userId") == null) {
                response.sendRedirect("login.jsp");
                return;
            }

            // Récupérer l'ID depuis l'URL
            String idParam = request.getParameter("id");

            if (idParam == null || idParam.trim().isEmpty()) {
                response.sendRedirect("AnnonceList");
                return;
            }

            try {
                Long id = Long.parseLong(idParam);
                Long userId = (Long) session.getAttribute("userId");

                // Récupérer l'annonce et vérifier que c'est l'auteur
                AnnonceService annonceService = new AnnonceService();
                Optional<Annonce> annonceOpt = annonceService.getAnnonceById(id);

                if (!annonceOpt.isPresent() || !annonceOpt.get().getAuthor().getId().equals(userId)) {
                    response.sendRedirect("AnnonceList");
                    return;
                }

                // Supprimer l'annonce via le service
                annonceService.deleteAnnonce(id, userId);

                // Rediriger vers la liste avec un message de succès
                response.sendRedirect("AnnonceList?success=deleted");

            } catch (NumberFormatException e) {
                response.sendRedirect("AnnonceList");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("AnnonceList?error=delete");
        }
    }
}

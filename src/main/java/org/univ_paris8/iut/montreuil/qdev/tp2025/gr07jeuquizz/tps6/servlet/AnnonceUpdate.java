package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.servlet;

import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.model.Annonce;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.model.Category;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.service.AnnonceService;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.service.CategoryService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@WebServlet("/AnnonceUpdate")
public class AnnonceUpdate extends HttpServlet {

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

                // Récupérer l'annonce et les catégories
                AnnonceService annonceService = new AnnonceService();
                Optional<Annonce> annonceOpt = annonceService.getAnnonceById(id);

                if (annonceOpt.isPresent()) {
                    Annonce annonce = annonceOpt.get();

                    // Vérifier que c'est l'auteur
                    Long userId = (Long) session.getAttribute("userId");
                    if (!annonce.getAuthor().getId().equals(userId)) {
                        response.sendRedirect("AnnonceList");
                        return;
                    }

                    CategoryService categoryService = new CategoryService();
                    List<Category> categories = categoryService.getAllCategories();

                    // Passer l'annonce et les catégories à la JSP
                    request.setAttribute("annonce", annonce);
                    request.setAttribute("categories", categories);

                    request.getRequestDispatcher("/AnnonceUpdate.jsp").forward(request, response);
                } else {
                    response.sendRedirect("AnnonceList");
                }

            } catch (NumberFormatException e) {
                response.sendRedirect("AnnonceList");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("AnnonceList");
        }
    }

    /**
     * Mettre à jour l'annonce
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Vérifier l'authentification
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("userId") == null) {
                response.sendRedirect("login.jsp");
                return;
            }

            // Récupérer les paramètres
            String idParam = request.getParameter("id");
            String title = request.getParameter("title");
            String description = request.getParameter("description");
            String adress = request.getParameter("adress");
            String mail = request.getParameter("mail");
            String categoryIdStr = request.getParameter("categoryId");

            // Validation
            if (idParam == null || title == null || title.trim().isEmpty() ||
                    description == null || description.trim().isEmpty() ||
                    adress == null || adress.trim().isEmpty() ||
                    mail == null || mail.trim().isEmpty() ||
                    categoryIdStr == null || categoryIdStr.trim().isEmpty()) {

                request.setAttribute("error", "Tous les champs sont obligatoires !");
                doGet(request, response);
                return;
            }

            try {
                Long id = Long.parseLong(idParam);
                Long categoryId = Long.parseLong(categoryIdStr);

                // Vérifier que l'utilisateur est l'auteur de cette annonce
                AnnonceService annonceService = new AnnonceService();
                Long userId = (Long) session.getAttribute("userId");
                Optional<Annonce> annonceOpt = annonceService.getAnnonceById(id);

                if (!annonceOpt.isPresent() || !annonceOpt.get().getAuthor().getId().equals(userId)) {
                    request.setAttribute("error", "Vous n'avez pas l'autorisation de modifier cette annonce");
                    response.sendRedirect("AnnonceList");
                    return;
                }

                // Mettre à jour via le service
                annonceService.updateAnnonce(id, title, description, adress, mail, categoryId, userId);

                // Rediriger vers la liste
                response.sendRedirect("AnnonceList?success=updated");

            } catch (NumberFormatException e) {
                request.setAttribute("error", "Paramètres invalides");
                doGet(request, response);
            }

        } catch (IllegalArgumentException e) {
            request.setAttribute("error", e.getMessage());
            doGet(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Erreur lors de la mise à jour");
            doGet(request, response);
        }
    }
}
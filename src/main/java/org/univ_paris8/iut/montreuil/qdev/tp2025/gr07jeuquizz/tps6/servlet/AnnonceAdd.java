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

@WebServlet("/AnnonceAdd")
public class AnnonceAdd extends HttpServlet {

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

            // Récupérer les catégories pour le formulaire
            CategoryService categoryService = new CategoryService();
            List<Category> categories = categoryService.getAllCategories();

            request.setAttribute("categories", categories);
            request.getRequestDispatcher("/AnnonceAdd.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Erreur lors du chargement du formulaire");
            request.getRequestDispatcher("/AnnonceAdd.jsp").forward(request, response);
        }
    }

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

            Long userId = (Long) session.getAttribute("userId");

            // Récupérer les paramètres du formulaire
            String title = request.getParameter("title");
            String description = request.getParameter("description");
            String adress = request.getParameter("adress");
            String mail = request.getParameter("mail");
            String categoryIdStr = request.getParameter("categoryId");

            // Validation : tous les champs sont obligatoires
            if (title == null || title.trim().isEmpty() ||
                    description == null || description.trim().isEmpty() ||
                    adress == null || adress.trim().isEmpty() ||
                    mail == null || mail.trim().isEmpty() ||
                    categoryIdStr == null || categoryIdStr.trim().isEmpty()) {

                request.setAttribute("error", "Tous les champs sont obligatoires !");
                doGet(request, response);
                return;
            }

            try {
                Long categoryId = Long.parseLong(categoryIdStr);

                // Créer l'annonce via le service (gère la transaction)
                AnnonceService annonceService = new AnnonceService();
                Annonce annonce = annonceService.createAnnonce(title, description, adress, mail, userId, categoryId);

                // Rediriger vers la liste des annonces
                response.sendRedirect("AnnonceList");

            } catch (NumberFormatException e) {
                request.setAttribute("error", "Catégorie invalide");
                doGet(request, response);
            }

        } catch (IllegalArgumentException e) {
            request.setAttribute("error", e.getMessage());
            doGet(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Erreur lors de la création de l'annonce");
            doGet(request, response);
        }
    }
}
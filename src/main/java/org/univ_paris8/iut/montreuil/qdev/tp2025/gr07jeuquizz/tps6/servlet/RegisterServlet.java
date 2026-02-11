package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.servlet;

import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.model.User;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.service.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Afficher la JSP du formulaire d'inscription
        request.getRequestDispatcher("/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Récupérer les paramètres du formulaire
            String username = request.getParameter("username");
            String email = request.getParameter("email");
            String password = request.getParameter("password");
            String passwordConfirm = request.getParameter("passwordConfirm");

            // Validation
            if (username == null || username.trim().isEmpty() ||
                    email == null || email.trim().isEmpty() ||
                    password == null || password.trim().isEmpty() ||
                    passwordConfirm == null || passwordConfirm.trim().isEmpty()) {

                request.setAttribute("error", "Tous les champs sont obligatoires");
                request.getRequestDispatcher("/register.jsp").forward(request, response);
                return;
            }

            // Vérifier que les mots de passe correspondent
            if (!password.equals(passwordConfirm)) {
                request.setAttribute("error", "Les mots de passe ne correspondent pas");
                request.getRequestDispatcher("/register.jsp").forward(request, response);
                return;
            }

            // Vérifier que le mot de passe fait au moins 6 caractères
            if (password.length() < 6) {
                request.setAttribute("error", "Le mot de passe doit faire au moins 6 caractères");
                request.getRequestDispatcher("/register.jsp").forward(request, response);
                return;
            }

            // Créer l'utilisateur via le service
            UserService userService = new UserService();
            User user = userService.createUser(username, email, password);

            // Rediriger vers la page de login avec un message de succès
            request.setAttribute("success", "Inscription réussie ! Veuillez vous connecter.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);

        } catch (IllegalArgumentException e) {
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/register.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Erreur lors de l'inscription : " + e.getMessage());
            request.getRequestDispatcher("/register.jsp").forward(request, response);
        }
    }
}

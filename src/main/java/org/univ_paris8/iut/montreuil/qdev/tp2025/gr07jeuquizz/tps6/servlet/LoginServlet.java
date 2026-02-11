package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.servlet;

import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.model.User;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.service.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Optional;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Afficher la JSP du formulaire de login
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Récupérer les paramètres du formulaire
            String username = request.getParameter("username");
            String password = request.getParameter("password");

            // Validation
            if (username == null || username.trim().isEmpty() ||
                    password == null || password.trim().isEmpty()) {

                request.setAttribute("error", "Identifiants manquants");
                request.getRequestDispatcher("/login.jsp").forward(request, response);
                return;
            }

            // Authentifier via le service
            UserService userService = new UserService();
            Optional<User> userOpt = userService.authenticate(username, password);

            if (userOpt.isPresent()) {
                // L'authentification est réussie
                User user = userOpt.get();

                // Créer une session et y stocker l'utilisateur
                HttpSession session = request.getSession(true);
                session.setAttribute("userId", user.getId());
                session.setAttribute("username", user.getUsername());
                session.setAttribute("email", user.getEmail());
                session.setMaxInactiveInterval(30 * 60); // 30 minutes

                // Rediriger vers la liste des annonces
                response.sendRedirect("AnnonceList");

            } else {
                // Échec de l'authentification
                request.setAttribute("error", "Nom d'utilisateur ou mot de passe incorrect");
                request.getRequestDispatcher("/login.jsp").forward(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Erreur lors de l'authentification");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }
}

package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Détruire la session
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        // Rediriger vers la page d'accueil
        response.sendRedirect("index.jsp");
    }
}

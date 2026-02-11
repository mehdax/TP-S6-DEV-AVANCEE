package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.servlet;

import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.model.Annonce;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.service.AnnonceService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/AnnonceList")
public class AnnonceList extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Récupérer le numéro de page (par défaut 0)
            int page = 0;
            String pageParam = request.getParameter("page");
            if (pageParam != null) {
                try {
                    page = Integer.parseInt(pageParam);
                    if (page < 0) page = 0;
                } catch (NumberFormatException e) {
                    page = 0;
                }
            }

            int pageSize = 5; // 5 annonces par page

            // Récupérer les annonces publiées avec pagination via le service
            AnnonceService annonceService = new AnnonceService();
            List<Annonce> annonces = annonceService.getPublishedAnnonces(page, pageSize);

            // Passer la liste à la JSP
            request.setAttribute("annonces", annonces);
            request.setAttribute("page", page);
            request.setAttribute("pageSize", pageSize);

            // Afficher la JSP
            request.getRequestDispatcher("/AnnonceList.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Erreur lors de la récupération des annonces");
            request.getRequestDispatcher("/AnnonceList.jsp").forward(request, response);
        }
    }
}
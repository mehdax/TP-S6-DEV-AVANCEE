package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.servlet;
/*
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.DAO.AnnonceDAO;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.db.ConnectionDB;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.model.Annonce;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.util.List;


@WebServlet("/AnnonceList")
public class AnnonceList extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Connexion à la base de données
            Connection conn = ConnectionDB.getInstance();
            AnnonceDAO annonceDAO = new AnnonceDAO(conn);

            // Récupérer toutes les annonces
            List<Annonce> annonces = annonceDAO.findAll();

            // Passer la liste à la JSP
            request.setAttribute("annonces", annonces);

            // Afficher la JSP
            request.getRequestDispatcher("/AnnonceList.jsp").forward(request, response);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            request.setAttribute("error", "Erreur de connexion à la base de données");
            request.getRequestDispatcher("/AnnonceList.jsp").forward(request, response);
        }
    }
}*/
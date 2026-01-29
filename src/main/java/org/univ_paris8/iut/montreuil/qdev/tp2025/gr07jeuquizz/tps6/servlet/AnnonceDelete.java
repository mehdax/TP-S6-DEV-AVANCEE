package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.servlet;


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

@WebServlet("/AnnonceDelete")
public class AnnonceDelete extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Récupérer l'ID depuis l'URL
        String idParam = request.getParameter("id");

        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendRedirect("AnnonceList.jsp");
            return;
        }

        try {
            int id = Integer.parseInt(idParam);

            // Connexion à la base de données
            Connection conn = ConnectionDB.getInstance();
            AnnonceDAO annonceDAO = new AnnonceDAO(conn);

            // Récupérer l'annonce
            Annonce annonce = annonceDAO.find(id);

            if (annonce != null) {
                // Supprimer l'annonce
                boolean success = annonceDAO.delete(annonce);

                if (success) {
                    // Rediriger vers la liste avec un message de succès
                    response.sendRedirect("AnnonceList?success=delete.jsp");
                } else {
                    response.sendRedirect("AnnonceList?error=delete.jsp");
                }
            } else {
                response.sendRedirect("AnnonceList.jsp");
            }

        } catch (NumberFormatException e) {
            response.sendRedirect("AnnonceList.jsp");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            response.sendRedirect("AnnonceList.jsp");
        }
    }
}


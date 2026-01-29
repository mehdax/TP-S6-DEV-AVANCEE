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

@WebServlet("/AnnonceUpdate")
public class AnnonceUpdate extends HttpServlet {


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Récupérer l'ID depuis l'URL
        String idParam = request.getParameter("id");

        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendRedirect("AnnonceList");
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
                // Passer l'annonce à la JSP
                request.setAttribute("annonce", annonce);
                request.getRequestDispatcher("/AnnonceUpdate.jsp").forward(request, response);
            } else {
                response.sendRedirect("AnnonceList");
            }

        } catch (NumberFormatException e) {
            response.sendRedirect("AnnonceList");
        } catch (ClassNotFoundException e) {
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

        // Récupérer les paramètres
        String idParam = request.getParameter("id");
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String adress = request.getParameter("adress");
        String mail = request.getParameter("mail");

        // Validation
        if (idParam == null || title == null || title.trim().isEmpty() ||
                description == null || description.trim().isEmpty() ||
                adress == null || adress.trim().isEmpty() ||
                mail == null || mail.trim().isEmpty()) {

            request.setAttribute("error", "Tous les champs sont obligatoires !");
            doGet(request, response);
            return;
        }

        try {
            int id = Integer.parseInt(idParam);

            // Connexion à la base de données
            Connection conn = ConnectionDB.getInstance();
            AnnonceDAO annonceDAO = new AnnonceDAO(conn);

            // Récupérer l'annonce existante
            Annonce annonce = annonceDAO.find(id);

            if (annonce != null) {
                // Mettre à jour les champs
                annonce.setTitle(title);
                annonce.setDescription(description);
                annonce.setAdress(adress);
                annonce.setMail(mail);

                // Sauvegarder
                boolean success = annonceDAO.update(annonce);

                if (success) {
                    response.sendRedirect("AnnonceList");
                } else {
                    request.setAttribute("error", "Erreur lors de la mise à jour");
                    doGet(request, response);
                }
            } else {
                response.sendRedirect("AnnonceList");
            }

        } catch (NumberFormatException e) {
            response.sendRedirect("AnnonceList");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            response.sendRedirect("AnnonceList");
        }
    }
}
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

@WebServlet("/AnnonceAdd")
public class AnnonceAdd extends HttpServlet {


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Afficher la JSP du formulaire
        request.getRequestDispatcher("/AnnonceAdd.jsp").forward(request, response);
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Récupérer les paramètres du formulaire
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String adress = request.getParameter("adress");
        String mail = request.getParameter("mail");

        // Validation : tous les champs sont obligatoires
        if (title == null || title.trim().isEmpty() ||
                description == null || description.trim().isEmpty() ||
                adress == null || adress.trim().isEmpty() ||
                mail == null || mail.trim().isEmpty()) {

            // Retourner au formulaire avec un message d'erreur
            request.setAttribute("error", "Tous les champs sont obligatoires !");
            request.getRequestDispatcher("/AnnonceAdd.jsp").forward(request, response);
            return;
        }

        try {
            // Connexion à la base de données
            Connection conn = ConnectionDB.getInstance();
            AnnonceDAO annonceDAO = new AnnonceDAO(conn);

            // Créer l'objet Annonce
            Annonce annonce = new Annonce(title, description, adress, mail);

            // Enregistrer en base
            boolean success = annonceDAO.create(annonce);

            if (success) {
                // Rediriger vers la liste des annonces
                response.sendRedirect("AnnonceList");
            } else {
                request.setAttribute("error", "Erreur lors de l'enregistrement de l'annonce");
                request.getRequestDispatcher("/AnnonceAdd.jsp").forward(request, response);
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            request.setAttribute("error", "Erreur de connexion à la base de données");
            request.getRequestDispatcher("/AnnonceAdd.jsp").forward(request, response);
        }
    }
}

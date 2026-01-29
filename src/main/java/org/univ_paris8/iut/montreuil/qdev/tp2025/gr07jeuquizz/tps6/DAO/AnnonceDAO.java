package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.DAO;

import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.model.Annonce;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AnnonceDAO extends DAO<Annonce> {

    public AnnonceDAO(Connection conn) {
        super(conn);
    }


    @Override
    public boolean create(Annonce annonce) {
        String sql = "INSERT INTO annonce (title, description, adress, mail) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, annonce.getTitle());
            stmt.setString(2, annonce.getDescription());
            stmt.setString(3, annonce.getAdress());
            stmt.setString(4, annonce.getMail());

            int result = stmt.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            System.err.println("Erreur lors de la création de l'annonce");
            e.printStackTrace();
            return false;
        }
    }


    @Override
    public Annonce find(int id) {
        String sql = "SELECT * FROM annonce WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Annonce(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("adress"),
                        rs.getString("mail"),
                        rs.getTimestamp("date")
                );
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de l'annonce");
            e.printStackTrace();
        }

        return null;
    }


    @Override
    public List<Annonce> findAll() {
        List<Annonce> annonces = new ArrayList<>();
        String sql = "SELECT * FROM annonce ORDER BY date DESC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Annonce annonce = new Annonce(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("adress"),
                        rs.getString("mail"),
                        rs.getTimestamp("date")
                );
                annonces.add(annonce);
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des annonces");
            e.printStackTrace();
        }

        return annonces;
    }


    @Override
    public boolean update(Annonce annonce) {
        String sql = "UPDATE annonce SET title = ?, description = ?, adress = ?, mail = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, annonce.getTitle());
            stmt.setString(2, annonce.getDescription());
            stmt.setString(3, annonce.getAdress());
            stmt.setString(4, annonce.getMail());
            stmt.setInt(5, annonce.getId());

            int result = stmt.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de l'annonce");
            e.printStackTrace();
            return false;
        }
    }


    @Override
    public boolean delete(Annonce annonce) {
        String sql = "DELETE FROM annonce WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, annonce.getId());

            int result = stmt.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de l'annonce");
            e.printStackTrace();
            return false;
        }
    }
}

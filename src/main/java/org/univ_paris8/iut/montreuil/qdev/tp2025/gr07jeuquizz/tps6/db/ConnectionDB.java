package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class ConnectionDB {
    private String url = "jdbc:postgresql://database-etudiants.iut.univ-paris8.fr/mbenali";
    private String user = "mbenali";
    private String passwd = "Mehdy123";
    private static Connection connect;
    private ConnectionDB() throws ClassNotFoundException {
        try {

            Class.forName("org.postgresql.Driver");
            connect = DriverManager.getConnection(url, user, passwd);
            System.out.println("✅ Connexion à la base de données réussie !");
        } catch (SQLException e) {
            System.err.println("❌ Erreur de connexion à la base de données");
            e.printStackTrace();
        }
    }

    public static Connection getInstance() throws ClassNotFoundException {
        if (connect == null) {
            new ConnectionDB();
        }
        return connect;
    }
}

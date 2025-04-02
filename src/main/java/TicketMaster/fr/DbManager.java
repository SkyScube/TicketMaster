package TicketMaster.fr;

import TicketMaster.fr.utils.LogManager;
import org.apache.juli.logging.Log;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DbManager {

    public static String path = "jdbc:mysql://localhost:3306/Test";

    public static List<Tickets> gettable() {
        List<Tickets> table = new ArrayList<>();

        String sql = "SELECT * FROM ticket JOIN user using (cuid)"; // Votre requête SQL

        try (Connection conn = DriverManager.getConnection(path, "root", "RandomPSW");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // Récupérer les métadonnées du résultat
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount(); // Nombre de colonnes

            // Parcourir les résultats
            while (rs.next()) {
                List<String> row = new ArrayList<>();
                    String id = rs.getString("id");
                    String cuid = rs.getString("cuid");
                    String description = rs.getString("description");
                    String date = rs.getString("date");
                    String state = rs.getString("state");
                    String prenom = rs.getString("prenom");
                    String nom = rs.getString("nom");
                    String email = rs.getString("email");
                    String adresse = rs.getString("adresse");
                    String ville = rs.getString("ville");
                table.add(new Tickets(id,cuid,description,date,state,prenom,nom,email,adresse,ville)); // Ajouter la ligne à la table4
            }

        } catch (SQLException e) {
            LogManager.log("Error", "Error on the table : "+ e.getMessage());
        }
        return new ArrayList<>();
    }

    public static List<List<String>> query(List<List<String>> arg, String user){
        List<List<String>> j = new ArrayList<>();
        for (List<String> i : arg) {
            if (i.contains(user)) {
                j.add(i);
            }
        }
        return j;
    }

    public static List<String> executeSelect(String query, List<Object> parameters) {
        List<String> result = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(path);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            // Assigner les paramètres à la requête
            for (int i = 0; i < parameters.size(); i++) {
                pstmt.setObject(i + 1, parameters.get(i));
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();

                while (rs.next()) {
                    StringBuilder row = new StringBuilder();
                    for (int i = 1; i <= columnCount; i++) {
                        row.append(rs.getString(i)).append(" | ");
                    }
                    result.add(row.toString());
                }
            }

        } catch (SQLException e) {
            System.out.println("Erreur lors de l'exécution de la requête : " + e.getMessage());
            LogManager.log("Error", "Error on the request: " + query);
            LogManager.log("Error", e.getMessage());
        }

        return result;
    }


    public static boolean executeUpdate(String query, List<Object> parameters) {
        try (Connection conn = DriverManager.getConnection(path);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            // Assigner les paramètres à la requête
            for (int i = 0; i < parameters.size(); i++) {
                pstmt.setObject(i + 1, parameters.get(i));
            }

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.out.println("Erreur lors de l'exécution de la requête : " + e.getMessage());
            LogManager.log("Error", "Error on the request: " + query);
            LogManager.log("Error", e.getMessage());
            return false;
        }
    }
}
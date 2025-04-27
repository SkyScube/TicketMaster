package TicketMaster.fr;

import TicketMaster.fr.DbManagers.Ticket;
import TicketMaster.fr.utils.LogManager;
import org.apache.juli.logging.Log;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DbManager {

    public static String path = "jdbc:mysql://localhost:3306/Test";

    public static List<Ticket> query(List<Ticket> arg, String user){
        List<Ticket> j = new ArrayList<>();

        for (Ticket i : arg) {
            System.out.println(i.getAssignedUser());
            if (i.getAssignedUser().toLowerCase().contains(user.toLowerCase())) {
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
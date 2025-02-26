package TicketMaster.fr;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static List<List<String>> golist(List<Tickets> table, boolean all) {
        List<List<String>> tableString = new ArrayList<>();
        List<String> etat = Arrays.asList("Ouvert", "En cours", "À traiter");
        if (all) {
            for (Tickets i : table){
            tableString.add(i.toList());
            }
        } else {
            for (Tickets i : table){
                if (etat.contains(i.getState())){
                    tableString.add(i.toList());
                }
            }
        }

        return tableString;
    }
}

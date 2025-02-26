package TicketMaster.fr.utils;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogManager {
    private static final String LOG_DIR = "logs/";
    private static final String LOG_FILE = "logs/tickets.log";

    static {
        try {
            Files.createDirectories(Paths.get(LOG_DIR)); // Crée le dossier logs s'il n'existe pas
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void log(String action, String message) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
        String logMessage = timestamp + " [" + action + "] " + message + "\n";

        try {
            Files.write(Paths.get(LOG_FILE), logMessage.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

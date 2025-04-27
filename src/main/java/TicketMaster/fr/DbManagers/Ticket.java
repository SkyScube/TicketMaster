package TicketMaster.fr.DbManagers;

import TicketMaster.fr.security.Tech;
import jakarta.persistence.*;
import lombok.Data;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne  // Un utilisateur peut avoir plusieurs tickets
    @JoinColumn(name = "cuid", referencedColumnName = "cuid") // Associe `cuid` entre `Ticket` et `User`
    private User user;

    private String description;
    private Date date;
    private String state;

    private static final String LOG_DIR = "logs/tickets/ticket";

    public void setDescription(String description, String tech) {
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
            description =  timestamp + " " + tech + " : " + description + "\n";
            String path = LOG_DIR+this.id+".log";
            Files.write(Paths.get(path), description.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            if (this.description == null || !this.description.equals(path)) {
                this.description = path;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String getDescription() {
        return LOG_DIR+this.id+".log";
    }

    public String getAssignedUser() {
        return (user != null) ? user.getNom() + " " + user.getPrenom() : "Aucun utilisateur";     }
}

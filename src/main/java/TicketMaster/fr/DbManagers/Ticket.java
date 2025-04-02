package TicketMaster.fr.DbManagers;

import TicketMaster.fr.security.Tech;
import jakarta.persistence.*;
import lombok.Data;

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
            System.out.println(this.id + description);
            Files.write(Paths.get(LOG_DIR+this.id+".log"), description.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public List<String> getDescription() {
        try{
            return Files.readAllLines(Paths.get(LOG_DIR+ this.id+".log"));
        } catch (IOException e){
            e.printStackTrace();

        }
        return new ArrayList<>();
    }
}

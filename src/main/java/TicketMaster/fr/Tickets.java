package TicketMaster.fr;

import lombok.Data;
import lombok.Setter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Data
public class Tickets {
    private String id;
    private String cuid;
    private String description;
    private String date;
    private String state;
    private String prenom;
    private String nom;
    @Setter
    private String email;
    private String adresse;
    private String ville;
    private static final String LOG_DIR = "logs/tickets/ticket";

    public Tickets() {
    }

    public Tickets(String id, String cuid, String description, String date, String state, String prenom, String nom, String email, String adresse, String ville) {
        this.id = id;
        this.cuid = cuid;
        this.description = (description != null && !description.isEmpty()) ? description : "Pas de description";
        if (!Files.exists(Paths.get(LOG_DIR + this.id + ".log"))) {
            setDescription(description);
        }
        this.date = (date != null && !date.isEmpty()) ? date : "Non spécifiée";
        this.state = (state != null && !state.isEmpty()) ? state : "En attente";
        this.prenom = prenom;
        this.nom = nom;
        this.email = email;
        this.adresse = (adresse != null && !adresse.isEmpty()) ? adresse : "Adresse inconnue";
        this.ville = (ville != null && !ville.isEmpty()) ? ville : "Ville inconnue";

    }

    public List<String> getDescription() {
        try{
            return Files.readAllLines(Paths.get(LOG_DIR+ this.id+".log"));
        } catch (IOException e){
            e.printStackTrace();

        }
        return new ArrayList<>();
    }

    public void setDescription(String desc) {
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
            desc =  timestamp + " "+ desc + "\n";
            System.out.println(this.id + desc);
            Files.write(Paths.get(LOG_DIR+this.id+".log"), desc.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String toString(){
        return this.id + " " + this.cuid + " " + this.description + " " + this.date + " " + this.state + " " +
                this.prenom + " " + this.nom + " " + this.email + " " + this.adresse + " " + this.ville;
    }


    public List<String> toList(){
        List<String> list = new ArrayList<>();
        list.add(this.id);
        list.add(this.cuid);
        list.add(this.description);
        list.add(this.date);
        list.add(this.state);
        list.add(this.prenom);
        list.add(this.nom);
        list.add(this.email);
        list.add(this.adresse);
        list.add(this.ville);
        return list;
    }

}
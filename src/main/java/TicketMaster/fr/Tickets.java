package TicketMaster.fr;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Tickets {
    private String id;
    private String cuid;
    private String description;
    private String date;
    private String state;
    private String prenom;
    private String nom;
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
        this.date = (date != null && !date.isEmpty()) ? date : "Non spécifiée";
        this.state = (state != null && !state.isEmpty()) ? state : "En attente";
        this.prenom = prenom;
        this.nom = nom;
        this.email = email;
        this.adresse = (adresse != null && !adresse.isEmpty()) ? adresse : "Adresse inconnue";
        this.ville = (ville != null && !ville.isEmpty()) ? ville : "Ville inconnue";

    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCuid() { return cuid; }
    public void setCuid(String cuid) { this.cuid = cuid; }

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
            Files.write(Paths.get(LOG_DIR+this.id+".log"), desc.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public String getVille() { return ville; }
    public void setVille(String ville) { this.ville = ville; }

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
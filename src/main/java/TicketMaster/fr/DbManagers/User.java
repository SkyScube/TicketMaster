package TicketMaster.fr.DbManagers;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class User {
    @Id
    String cuid;

    String prenom;
    String nom;
    String email;
    String adresse;
    String ville;
}

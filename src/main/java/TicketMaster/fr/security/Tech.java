package TicketMaster.fr.security;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Tech{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String role; // ROLE_USER, ROLE_ADMIN

    public boolean hasRole(char role){
        for (int i = 0; i < this.role.length(); i++){
            if (this.role.charAt(i) == role){
                return true;
            }
        }return false;
    }
}

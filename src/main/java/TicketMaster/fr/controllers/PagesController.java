package TicketMaster.fr.controllers;

import TicketMaster.fr.Tickets;
import TicketMaster.fr.utils.LogManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static TicketMaster.fr.DbManager.*;
import static TicketMaster.fr.Main.*;

@Controller
public class PagesController {

    public List<Tickets> table = gettable();
    public int userIndex;

    @GetMapping("/")
    public String home(@RequestParam(required = false, defaultValue = "-1") String id, ModelMap modelMap) {
        if ("-1".equals(id)) {
            modelMap.addAttribute("tickets", null);
            modelMap.addAttribute("table", table);
            modelMap.addAttribute("id", null);
        } else {
            modelMap.addAttribute("tickets", null);
            userIndex = Integer.parseInt(id) - 1;
            Tickets user = table.get(userIndex);
            modelMap.addAttribute("table", null);
            modelMap.addAttribute("id", user);
            LogManager.log("SUCCESS", "User has been found : "+ user.getNom() + " " + user.getPrenom());
            System.out.println(user.getDescription());
        }

        return "pages/home"; // Renvoyer la vue
    }

    @PostMapping("/submit")
    public String submit(@RequestParam("inputText") String inputText, ModelMap modelMap) {
        List<List<String>> search = query(golist(table,true), inputText);
        modelMap.addAttribute("table", search);
        return "pages/submit";
    }

    @PostMapping("/solve")
    public String solve(ModelMap modelMap) {
        executeUpdate("UPDATE ticket SET state = 'Clos' Where id = ?", List.of(userIndex + 1));
        return "redirect:/";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute Tickets ticket) {
        if (ticket == null || ticket.getId() == null) {
            System.out.println("Erreur : le ticket envoyé est null !");
            return "redirect:/";
        }

        // Récupérer l'ancien ticket via l'ID
        Tickets t = table.stream()
                .filter(tk -> tk.getId().equals(ticket.getId()))
                .findFirst()
                .orElse(null);

        // Vérifier si le ticket existe dans la base
        if (t == null) {
            System.out.println("Erreur : ticket introuvable avec ID " + ticket.getId());
            return "redirect:/";
        }

        // Construire la requête SQL
        StringBuilder sql = new StringBuilder("UPDATE ticket SET ");
        List<String> up = new ArrayList<>();
        List<Object> parameters = new ArrayList<>();
        List<String> logChanges = new ArrayList<>();

        if (!ticket.getCuid().equals(t.getCuid())) {
            up.add("cuid = ?");
            parameters.add(ticket.getCuid());
            logChanges.add("CUID: " + t.getCuid() + " → " + ticket.getCuid());
        }
        if (!ticket.getDescription().equals(t.getDescription())) {
            up.add("description = ?");
            parameters.add(ticket.getDescription());
            logChanges.add("Description: " + t.getDescription() + " → " + ticket.getDescription());
        }
        if (!ticket.getDate().equals(t.getDate())) {
            up.add("date = ?");
            parameters.add(ticket.getDate());
            logChanges.add("Date: " + t.getDate() + " → " + ticket.getDate());
        }
        if (!ticket.getState().equals(t.getState())) {
            up.add("state = ?");
            parameters.add(ticket.getState());
            logChanges.add("État: " + t.getState() + " → " + ticket.getState());
        }
        if (!ticket.getPrenom().equals(t.getPrenom())) {
            up.add("prenom = ?");
            parameters.add(ticket.getPrenom());
            logChanges.add("Prénom: " + t.getPrenom() + " → " + ticket.getPrenom());
        }
        if (!ticket.getNom().equals(t.getNom())) {
            up.add("nom = ?");
            parameters.add(ticket.getNom());
            logChanges.add("Nom: " + t.getNom() + " → " + ticket.getNom());
        }
        if (!ticket.getEmail().equals(t.getEmail())) {
            up.add("email = ?");
            parameters.add(ticket.getEmail());
            logChanges.add("Email: " + t.getEmail() + " → " + ticket.getEmail());
        }
        if (!ticket.getAdresse().equals(t.getAdresse())) {
            up.add("adresse = ?");
            parameters.add(ticket.getAdresse());
            logChanges.add("Adresse: " + t.getAdresse() + " → " + ticket.getAdresse());
        }
        if (!ticket.getVille().equals(t.getVille())) {
            up.add("ville = ?");
            parameters.add(ticket.getVille());
            logChanges.add("Ville: " + t.getVille() + " → " + ticket.getVille());
        }

        // Vérifier s'il y a des modifications
        if (!up.isEmpty()) {
            sql.append(String.join(", ", up));
            sql.append(" WHERE id = ?");
            parameters.add(ticket.getId());

            String logMessage = "Ticket " + ticket.getId() + " edited. Changes : " + String.join(", ", logChanges);
            LogManager.log("UPDATE", logMessage);

            System.out.println("Requête exécutée : " + sql);
        } else {
            System.out.println("Aucune modification détectée.");
        }

        return "redirect:/";
    }


    @GetMapping("/new")
    public String newt(ModelMap modelMap) {
        modelMap.addAttribute("ticket", new Tickets());
        String id = String.valueOf(table.size()+1);
        modelMap.addAttribute("id", id);
        Date date = new Date();
        modelMap.addAttribute("date", date);
        return "pages/new";
    }

    @PostMapping("/new")
    public String newTicket(@ModelAttribute Tickets ticket, RedirectAttributes redirectAttributes) {
        System.out.println(ticket);

        // Vérifier si l'utilisateur existe
        String userQuery = "SELECT * FROM user WHERE cuid = ?";
        List<Object> userParams = Arrays.asList(ticket.getCuid());
        List<String> user = executeSelect(userQuery, userParams);

        // Si l'utilisateur n'existe pas, l'insérer
        if (user.isEmpty()) {
            String insertUserQuery = "INSERT INTO user (cuid, prenom, nom, email, adresse, ville) VALUES (?, ?, ?, ?, ?, ?)";
            List<Object> insertUserParams = Arrays.asList(
                    ticket.getCuid(), ticket.getPrenom(), ticket.getNom(),
                    ticket.getEmail(), ticket.getAdresse(), ticket.getVille()
            );

            if (executeUpdate(insertUserQuery, insertUserParams)) {
                LogManager.log("INSERT", "User : " + ticket.getCuid() + " has been created in table user");
            }
        }

        String insertTicketQuery = "INSERT INTO ticket (cuid, description, state) VALUES (?, ?, ?)";
        List<Object> insertTicketParams = Arrays.asList(ticket.getCuid(), ticket.getDescription(), ticket.getState());

        if (executeUpdate(insertTicketQuery, insertTicketParams)) {
            LogManager.log("INSERT", "Ticket : " + ticket.getId() + " has been created");
            redirectAttributes.addFlashAttribute("success", true);
        } else {
            LogManager.log("ERROR", "Ticket : " + ticket.getId() + " can't be created");
        }

        return "redirect:/new";
    }

    @GetMapping("/description")
    public String description(@RequestParam(required = true) String id, ModelMap modelMap) {
        System.out.println(id);
        modelMap.addAttribute("ticket", table.get(Integer.parseInt(id)));
        return "pages/description";
    }
}
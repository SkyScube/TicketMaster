package TicketMaster.fr.controllers;

import TicketMaster.fr.DbManagers.Ticket;
import TicketMaster.fr.DbManagers.TicketsService;
import TicketMaster.fr.Tickets;
import TicketMaster.fr.utils.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static TicketMaster.fr.DbManager.*;
import static TicketMaster.fr.Main.golist;

@Controller
public class PostControlles {
    @Autowired
    private TicketsService ticketsService;
    private List<Tickets> table;
    private int userIndex;

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

    @PostMapping("/new")
    public String newTicket(@ModelAttribute Tickets ticket,@RequestParam("description") String description, RedirectAttributes redirectAttributes) {
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
        System.out.println(description);
        ticket.setDescription(description);
        List<Object> insertTicketParams = Arrays.asList(ticket.getCuid(), ticket.getDescription(), ticket.getState());

        if (executeUpdate(insertTicketQuery, insertTicketParams)) {
            LogManager.log("INSERT", "Ticket : " + ticket.getId() + " has been created");
            redirectAttributes.addFlashAttribute("success", true);
        } else {
            LogManager.log("ERROR", "Ticket : " + ticket.getId() + " can't be created");
        }
        return "redirect:/new";
    }

    @PostMapping("/description")
    public String desc(@RequestParam("description") String descritpion,@RequestParam("id") String id,RedirectAttributes redirectAttributes) {
        String tech = SecurityContextHolder.getContext().getAuthentication().getName();
        Ticket ticket = ticketsService.getTicketById(Long.parseLong(id));
        ticket.setDescription(descritpion,tech);
        redirectAttributes.addFlashAttribute("success", true);
        //LogManager.TechLog("SUCCESS", authentication.getName(), "Found : "+ ticket.getUser().getPrenom() + " " + ticket.getUser().getNom());
        return "redirect:/?id="+id;
    }

}

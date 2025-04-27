package TicketMaster.fr.controllers;

import TicketMaster.fr.DbManagers.Ticket;
import TicketMaster.fr.DbManagers.TicketsService;
import TicketMaster.fr.DbManagers.User;
import TicketMaster.fr.DbManagers.UserRepository;
import TicketMaster.fr.utils.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;
import java.util.function.Consumer;

import static TicketMaster.fr.DbManager.*;

@Controller
public class PostController {
    @Autowired
    private TicketsService ticketsService;
    @Autowired
    private UserRepository userrepository;
    private int userIndex;


    @PostMapping("/solve")
    public String solve(ModelMap modelMap) {
        executeUpdate("UPDATE ticket SET state = 'Clos' Where id = ?", List.of(userIndex + 1));
        return "redirect:/";
    }

    @PreAuthorize("HasRole('EDITOR')")
    @PostMapping("/update")
    public String update(@ModelAttribute Ticket newTicket, RedirectAttributes redirectAttributes) {
        if (newTicket == null || newTicket.getId() == null) {
            redirectAttributes.addFlashAttribute("error", "Erreur : ticket invalide.");
            return "redirect:/";
        }

        Ticket original = ticketsService.getTicketById(newTicket.getId());
        if (original == null) {
            redirectAttributes.addFlashAttribute("error", "Ticket non trouvé.");
            return "redirect:/";
        }

        System.out.println("\n\n\n\n\ntest");

        List<String> logs = new ArrayList<>();

        applyIfChanged("État", newTicket.getState(), original.getState(), val -> original.setState(val));
        applyIfChanged("Prénom", newTicket.getUser().getPrenom(), original.getUser().getPrenom(), val -> original.getUser().setPrenom(val));
        applyIfChanged("Nom", newTicket.getUser().getNom(), original.getUser().getNom(), val -> original.getUser().setNom(val));
        applyIfChanged("Email", newTicket.getUser().getEmail(), original.getUser().getEmail(), val -> original.getUser().setEmail(val));
        applyIfChanged("Adresse", newTicket.getUser().getAdresse(), original.getUser().getAdresse(), val -> original.getUser().setAdresse(val));
        applyIfChanged("Ville", newTicket.getUser().getVille(), original.getUser().getVille(), val -> original.getUser().setVille(val));

        if (!logs.isEmpty()) {
            ticketsService.save(original);
            String logMessage = "Ticket " + original.getId() + " modifié. Changements : " + String.join(", ", logs);
            LogManager.log("UPDATE", logMessage);
            redirectAttributes.addFlashAttribute("success", "Ticket mis à jour avec succès !");
        } else {
            redirectAttributes.addFlashAttribute("info", "Aucune modification détectée.");
        }

        return "redirect:/";
    }

    private <T> void applyIfChanged(String fieldName, T newValue, T oldValue, Consumer<T> updater) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (newValue != null && !newValue.equals(oldValue)) {
            LogManager.TechLog("EDIT", authentication.getName(), fieldName + " : " + oldValue + " → " + newValue);
            updater.accept(newValue);
        }
    }

    @PostMapping("/new")
    public String newTicket(@ModelAttribute Ticket ticket,@RequestParam("description") String description, RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String cuid = ticket.getUser().getCuid();

        Optional<User> optionalUser = userrepository.findById(cuid);
        User user;

        if (optionalUser.isPresent()) {
            user = optionalUser.get();
        } else {
            user = ticket.getUser(); // à sécuriser dans un vrai projet !
            userrepository.save(user);
        }

        ticket.setUser(user);
        ticket.setDate(new Date());

        // Méthode custom qui ajoute une entrée à l'historique de description
        ticket.setDescription(description, authentication.getName());

        ticketsService.save(ticket);

        // Ajouter la description dans le fichier log (tech hardcodé ici)
        //ticket.setDescription(ticket.getDescription().get(0), "SYSTEM");
        System.out.println(ticket);
        LogManager.TechLog("SUCCESS", authentication.getName(), "Creation ticket"+ticket.getId());
        redirectAttributes.addFlashAttribute("success", true);
        return "redirect:/new";
    }

    @PostMapping("/description")
    public String desc(@RequestParam("description") String descritpion,@RequestParam("id") String id,RedirectAttributes redirectAttributes) {
        String tech = SecurityContextHolder.getContext().getAuthentication().getName();
        Ticket ticket = ticketsService.getTicketById(Long.parseLong(id));
        ticket.setDescription(descritpion,tech);
        ticketsService.save(ticket);
        redirectAttributes.addFlashAttribute("success", true);
        //LogManager.TechLog("SUCCESS", authentication.getName(), "Found : "+ ticket.getUser().getPrenom() + " " + ticket.getUser().getNom());
        return "redirect:/?id="+id;
    }

}

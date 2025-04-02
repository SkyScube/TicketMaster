package TicketMaster.fr.controllers;

import TicketMaster.fr.DbManagers.Ticket;
import TicketMaster.fr.DbManagers.TicketsRepository;
import TicketMaster.fr.DbManagers.TicketsService;
import TicketMaster.fr.Tickets;
import TicketMaster.fr.utils.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    @Autowired
    private TicketsService ticketsService;

    public List<Tickets> table = gettable();
    public Long userIndex;


    @GetMapping("/")
    public String home(@RequestParam(required = false, defaultValue = "-1") String id, ModelMap modelMap) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ("-1".equals(id)) {
            modelMap.addAttribute("tickets", null);
            modelMap.addAttribute("table", ticketsService.getAllTickets());
            modelMap.addAttribute("id", null);
        } else {
            modelMap.addAttribute("tickets", null);
            userIndex = Long.parseLong(id);
            Ticket ticket = ticketsService.getTicketById(userIndex);
            modelMap.addAttribute("table", null);
            modelMap.addAttribute("id", ticket);
            LogManager.TechLog("SUCCESS", authentication.getName(), "Found : "+ ticket.getUser().getPrenom() + " " + ticket.getUser().getNom());
        }
        return "pages/home"; // Renvoyer la vue
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



    @GetMapping("/description")
    public String description(@RequestParam(required = true) String id, ModelMap modelMap) {
        modelMap.addAttribute("ticket", ticketsService.getTicketById(Long.parseLong(id)));
        return "pages/description";
    }

    @GetMapping("/auth/login")
    public String showLoginPage() {
        return "auth/login"; // Affiche login.html
    }

    @GetMapping("/auth/register")
    public String showRegisterPage() {
        return "auth/register"; // Affiche register.html
    }
}
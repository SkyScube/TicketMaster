package TicketMaster.fr.controllers;

import TicketMaster.fr.DbManagers.*;
import TicketMaster.fr.security.Tech;
import TicketMaster.fr.security.TechRepository;
import TicketMaster.fr.utils.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static TicketMaster.fr.DbManager.*;

@Controller
public class PagesController {
    @Autowired
    private TicketsService ticketsService;
    @Autowired
    private UserRepository userrepository;
    @Autowired
    private TechRepository techRepository;

    public Long userIndex;

    @GetMapping("/")
    public String home(@RequestParam(required = false, defaultValue = "-1") String id,
                       @RequestParam(required = false) String searchTerm,
                       ModelMap modelMap) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if ("-1".equals(id)) {
                modelMap.addAttribute("tickets", null);
                modelMap.addAttribute("table", ticketsService.getAllTickets());
                modelMap.addAttribute("id", null);
            } else {
                modelMap.addAttribute("tickets", null);
                userIndex = Long.parseLong(id);
                Ticket ticket = ticketsService.getTicketById(userIndex);
                System.out.println(ticket.getState());
                modelMap.addAttribute("table", null);
                modelMap.addAttribute("id", ticket);
                modelMap.addAttribute("searchTerm", searchTerm);
                LogManager.TechLog("SUCCESS", authentication.getName(), "Found : " + ticket.getUser().getPrenom() + " " + ticket.getUser().getNom());
            }
        return "pages/home"; // Renvoyer la vue
    }

    @GetMapping("/new")
    public String newt(ModelMap modelMap) {
        Ticket ticket = new Ticket();
        ticket.setUser(new User());
        modelMap.addAttribute("ticket", ticket);

        String id = String.valueOf(ticketsService.getAllTickets().size()+1);
        modelMap.addAttribute("id", id);
        Date date = new Date();
        modelMap.addAttribute("date", date);
        return "pages/new";
    }



    @GetMapping("/description")
    public String description(@RequestParam(required = true) String id, ModelMap modelMap) {
        System.out.println(id);
        System.out.println(ticketsService.getAllDescription(ticketsService.getTicketById(Long.parseLong(id))));
        modelMap.addAttribute("ticket", ticketsService.getAllDescription(ticketsService.getTicketById(Long.parseLong(id))));
        modelMap.addAttribute("id", id);
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

    @GetMapping("/api/user/{cuid}")
    @ResponseBody
    public ResponseEntity<User> getUserByCuid(@PathVariable String cuid) {
        return userrepository.findByCuid(cuid)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("search")
    public String search(@RequestParam(value = "searchTerm") String inputText, ModelMap modelMap) {
        List<Ticket> search = query(ticketsService.getAllTickets(), inputText);
        modelMap.addAttribute("table", search);
        modelMap.addAttribute("searchTerm", inputText); // Pour re-remplir le champ dans le formulaire
        return "pages/submit";
    }
}
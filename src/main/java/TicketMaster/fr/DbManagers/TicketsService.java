package TicketMaster.fr.DbManagers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TicketsService {
    @Autowired
    private TicketsRepository ticketsRepository;

    public List<Ticket> getAllTickets() {
        return ticketsRepository.findAll();
    }

    public Ticket getTicketById(Long id) {
        return ticketsRepository.findById(id).orElse(null);
    }

    public Ticket save(Ticket ticket) {
        return ticketsRepository.save(ticket);
    }
    public List<String> getAllDescription(Ticket ticket) {
        try{
            return Files.readAllLines(Paths.get(ticket.getDescription()));
        } catch (IOException e){
            e.printStackTrace();

        }
        return new ArrayList<>();
    }
}

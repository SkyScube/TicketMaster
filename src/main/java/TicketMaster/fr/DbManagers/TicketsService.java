package TicketMaster.fr.DbManagers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}

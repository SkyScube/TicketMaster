package TicketMaster.fr.DbManagers;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketsRepository extends JpaRepository<Ticket, Long> {
}

package TicketMaster.fr.security;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TechRepository extends JpaRepository<Tech, Long> {
    Tech findByUsername(String username);
}

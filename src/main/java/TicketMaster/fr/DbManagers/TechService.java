package TicketMaster.fr.DbManagers;

import TicketMaster.fr.security.Tech;
import TicketMaster.fr.security.TechRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TechService {
    @Autowired
    private TechRepository techRepository;

    public Boolean hasRole(Tech tech, char role) {
        String tech_role = tech.getRole();
        for (int i = 0; i < tech_role.length(); i++) {
            if (tech_role.charAt(i) == role) {
                return true;
            }
        }
        return false;
    }
}

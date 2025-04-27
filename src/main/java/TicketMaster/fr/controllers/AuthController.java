package TicketMaster.fr.controllers;

import TicketMaster.fr.security.JwtUtils;
import TicketMaster.fr.security.Tech;
import TicketMaster.fr.security.TechRepository;
import TicketMaster.fr.utils.LogManager;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.Cookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final TechRepository techRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String username, @RequestParam String password, HttpServletResponse response) {
        Tech user = new Tech();
        user.setUsername(username);
        user.setPassword(password);
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
            if (authentication.isAuthenticated()) {
                String token = jwtUtils.generateToken(user.getUsername());
                Cookie jwtCookie = new Cookie("JWT_TOKEN", token);
                jwtCookie.setHttpOnly(true);
                jwtCookie.setSecure(false);
                jwtCookie.setPath("/");
                response.addCookie(jwtCookie);
                LogManager.TechLog("CONNECTION", user.getUsername(), "logged in");
                return ResponseEntity.status(HttpStatus.SEE_OTHER)
                        .header(HttpHeaders.LOCATION, "/")
                        .build();
            }
            LogManager.TechLog("ERROR", user.getUsername(), "try logged in");
            return ResponseEntity.status(HttpStatus.SEE_OTHER)
                    .header(HttpHeaders.LOCATION, "/auth/login?error=true")
                    .build();
        } catch (AuthenticationException e) {
            LogManager.TechLog("ERROR", user.getUsername(), "try logged in");
            return ResponseEntity.status(HttpStatus.SEE_OTHER)
                    .header(HttpHeaders.LOCATION, "/auth/login?error=true")
                    .build();        }
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestParam String username, @RequestParam String password, Model model) {
        // Vérifie si l'utilisateur existe déjà
        if (techRepository.findByUsername(username) != null) {
            model.addAttribute("error", "L'utilisateur existe déjà !");
            return ResponseEntity.status(303).header("Location", "/auth/login").build();
        }

        String hashedPassword = passwordEncoder.encode(password);
        Tech newUser = new Tech();
        newUser.setUsername(username);
        newUser.setPassword(hashedPassword);
        newUser.setRole("r"); // Valeur par défaut
        techRepository.save(newUser);
        LogManager.TechLog("REGISTER", username, "role = " + newUser.getRole());
        return ResponseEntity.status(303).header("Location", "/auth/login").build();
    }
}

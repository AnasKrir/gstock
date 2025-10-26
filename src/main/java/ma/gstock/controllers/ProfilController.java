// src/main/java/ma/gstock/controllers/ProfilController.java
package ma.gstock.controllers;

import ma.gstock.entities.Utilisateur;
import ma.gstock.repositories.UtilisateurRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ProfilController {

    private final UtilisateurRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    public ProfilController(UtilisateurRepository userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/profil")
    public String view(Authentication auth, Model model) {
        Utilisateur me = userRepo.findByUsername(auth.getName()).orElseThrow();
        model.addAttribute("me", me);
        return "profil";
    }

    @PostMapping("/profil/password")
    public String changePassword(Authentication auth,
                                 @RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 Model model) {
        Utilisateur me = userRepo.findByUsername(auth.getName()).orElseThrow();

        if (!passwordEncoder.matches(currentPassword, me.getPassword())) {
            model.addAttribute("me", me);
            model.addAttribute("error", "Mot de passe actuel incorrect.");
            return "profil";
        }
        if (newPassword == null || newPassword.length() < 4 || !newPassword.equals(confirmPassword)) {
            model.addAttribute("me", me);
            model.addAttribute("error", "Nouveau mot de passe invalide ou non confirmé.");
            return "profil";
        }

        me.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(me);
        model.addAttribute("me", me);
        model.addAttribute("success", "Mot de passe mis à jour avec succès.");
        return "profil";
    }
}

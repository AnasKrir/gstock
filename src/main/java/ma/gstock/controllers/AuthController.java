package ma.gstock.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "logout", required = false) String logout,
                        Model model) {
        if (error != null)  model.addAttribute("error", "Identifiants invalides. Réessayez.");
        if (logout != null) model.addAttribute("message", "Vous êtes déconnecté.");
        return "login";
    }

    @GetMapping("/")
    public String home(Authentication auth) {
        if (auth == null) return "redirect:/login";
        var roles = auth.getAuthorities();
        boolean isAdmin   = roles.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isVendeur = roles.stream().anyMatch(a -> a.getAuthority().equals("ROLE_VENDEUR"));
        if (isAdmin)   return "redirect:/admin";
        if (isVendeur) return "redirect:/vendeur";
        return "index";
    }
}

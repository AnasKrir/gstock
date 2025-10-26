// ma/gstock/controllers/VendeurController.java
package ma.gstock.controllers;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class VendeurController {
    @GetMapping("/vendeur")
    public String vendeurHome() { return "vendeur/home"; }
}

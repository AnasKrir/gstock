// ma.gstock.controllers.VendeurCommandeController
package ma.gstock.controllers;

import ma.gstock.entities.Commande;
import ma.gstock.entities.Utilisateur;
import ma.gstock.exceptions.StockInsuffisantException;
import ma.gstock.repositories.CommandeRepository;
import ma.gstock.repositories.ProduitRepository;
import ma.gstock.repositories.UtilisateurRepository;
import ma.gstock.services.CommandeService;
import ma.gstock.services.dto.CreateCommandeInput;
import ma.gstock.services.dto.LigneCommandeInput;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/vendeur/commandes")
public class VendeurCommandeController {

    private final UtilisateurRepository userRepo;
    private final CommandeRepository commandeRepo;
    private final ProduitRepository produitRepo;
    private final CommandeService commandeService;

    public VendeurCommandeController(UtilisateurRepository userRepo,
                                     CommandeRepository commandeRepo,
                                     ProduitRepository produitRepo,
                                     CommandeService commandeService) {
        this.userRepo = userRepo;
        this.commandeRepo = commandeRepo;
        this.produitRepo = produitRepo;
        this.commandeService = commandeService;
    }

    @GetMapping
    public String mesCommandes(Authentication auth, Model model) {
        Utilisateur me = userRepo.findAll().stream()
                .filter(u -> u.getUsername().equals(auth.getName()))
                .findFirst()
                .orElse(null);
        List<Commande> list = commandeRepo.findAll().stream()
                .filter(c -> c.getVendeur().getId().equals(me.getId()))
                .toList();
        model.addAttribute("commandes", list);
        return "vendeur/commandes/list";
    }

    @GetMapping("/nouvelle")
    public String nouvelle(Model model) {
        model.addAttribute("produits", produitRepo.findAll());
        return "vendeur/commandes/form";
    }

    // ❗️CORRECTION: ne pas redéclarer le chemin; le mapping de classe couvre déjà /vendeur/commandes
    @PostMapping
    public String creerPourVendeur(
            @RequestParam Long clientId,
            @RequestParam("productIds") List<Long> productIds,
            @RequestParam("quantites") List<Integer> quantites,
            Authentication auth,
            RedirectAttributes ra
    ) {
        // Récupérer le vendeur courant
        Utilisateur me = userRepo.findAll().stream()
                .filter(u -> u.getUsername().equals(auth.getName()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Vendeur introuvable"));
        Long vendeurId = me.getId();

        // Construire l’input
        List<LigneCommandeInput> lignes = new ArrayList<>();
        for (int i = 0; i < productIds.size(); i++) {
            Integer q = quantites.get(i);
            if (q != null && q > 0) {
                lignes.add(new LigneCommandeInput(productIds.get(i), q));
            }
        }
        CreateCommandeInput input = new CreateCommandeInput(clientId, vendeurId, lignes);

        try {
            commandeService.creerCommande(input); // lève StockInsuffisantException si besoin
            ra.addFlashAttribute("success", "Commande validée avec succès.");
            return "redirect:/vendeur/commandes";
        } catch (StockInsuffisantException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/vendeur/commandes/nouvelle";
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/vendeur/commandes/nouvelle";
        }
    }

    // ❌ À SUPPRIMER d’ici : ce mapping admin devient /vendeur/commandes/admin/..., ce qui est faux
    // Garde la validation dans le contrôleur ADMIN.
    // @PostMapping("/admin/commandes/{id}/valider") ...
}

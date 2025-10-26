package ma.gstock.controllers;

import ma.gstock.entities.Client;
import ma.gstock.entities.Commande;
import ma.gstock.entities.Produit;
import ma.gstock.entities.Utilisateur;
import ma.gstock.entities.enums.Role;
import ma.gstock.exceptions.StockInsuffisantException;
import ma.gstock.repositories.ClientRepository;
import ma.gstock.repositories.CommandeRepository;
import ma.gstock.repositories.ProduitRepository;
import ma.gstock.repositories.UtilisateurRepository;
import ma.gstock.services.CommandeService;
import ma.gstock.services.dto.CreateCommandeInput;
import ma.gstock.services.dto.LigneCommandeInput;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/commandes")
public class CommandeController {

    private final CommandeRepository commandeRepo;
    private final ClientRepository clientRepo;
    private final UtilisateurRepository utilisateurRepo;
    private final ProduitRepository produitRepo;
    private final CommandeService commandeService;

    public CommandeController(CommandeRepository commandeRepo,
                              ClientRepository clientRepo,
                              UtilisateurRepository utilisateurRepo,
                              ProduitRepository produitRepo,
                              CommandeService commandeService) {
        this.commandeRepo = commandeRepo;
        this.clientRepo = clientRepo;
        this.utilisateurRepo = utilisateurRepo;
        this.produitRepo = produitRepo;
        this.commandeService = commandeService;
    }

    // GET /admin/commandes  -> templates/admin/commandes/list.html
    @GetMapping
    public String list(@RequestParam(value = "q", required = false) String q, Model model) {
        List<Commande> commandes;

        if (q == null || q.isBlank()) {
            // tout, trié du plus récent au plus ancien
            commandes = commandeRepo.findAll(Sort.by(Sort.Direction.DESC, "id"));
            // ou: commandes = commandeRepo.findAllByOrderByIdDesc();
        } else {
            String term = q.trim();
            // si nombre -> chercher par id exact; sinon -> par nom du client (contains, ignore case)
            try {
                Long id = Long.valueOf(term);
                commandes = commandeRepo.findById(id)
                        .map(List::of)
                        .orElseGet(List::of);
            } catch (NumberFormatException ex) {
                commandes = commandeRepo.findByClient_NomContainingIgnoreCaseOrderByIdDesc(term);
                // (optionnel) si tu veux aussi élargir au vendeur :
                // var byVendeur = commandeRepo.findByVendeur_UsernameContainingIgnoreCaseOrderByIdDesc(term);
                // commandes = Stream.concat(commandes.stream(), byVendeur.stream())
                //                   .distinct()
                //                   .toList();
            }
        }

        model.addAttribute("q", q);
        model.addAttribute("commandes", commandes);
        return "admin/commandes/list";
    }


    // GET /admin/commandes/nouvelle -> templates/admin/commandes/form.html
    @GetMapping("/nouvelle")
    public String nouvelle(Model model) {
        List<Client> clients = clientRepo.findAll(Sort.by("nom"));
        // Si ton UtilisateurRepository n'a pas findByRole, ajoute-le, sinon filtre en mémoire.
        List<Utilisateur> vendeurs = utilisateurRepo.findAll()
                .stream().filter(u -> u.getRole() == Role.VENDEUR || u.getRole() == Role.ADMIN)
                .collect(Collectors.toList());
        List<Produit> produits = produitRepo.findAll(Sort.by("nom"));

        model.addAttribute("clients", clients);
        model.addAttribute("vendeurs", vendeurs);
        model.addAttribute("produits", produits);
        return "admin/commandes/form";
    }

    // POST /admin/commandes  (lit productIds[] + quantites[])
    // POST /admin/commandes
    @PostMapping
    public String create(
            @RequestParam Long clientId,
            @RequestParam Long vendeurId,
            @RequestParam("productIds") List<Long> productIds,
            @RequestParam("quantites") List<Integer> quantites,
            RedirectAttributes ra
    ) {
        List<LigneCommandeInput> lignes = new ArrayList<>();
        for (int i = 0; i < productIds.size(); i++) {
            Integer q = quantites.get(i);
            if (q != null && q > 0) {
                lignes.add(new LigneCommandeInput(productIds.get(i), q));
            }
        }

        try {
            CreateCommandeInput input = new CreateCommandeInput(clientId, vendeurId, lignes);
            commandeService.creerCommande(input);
            ra.addFlashAttribute("success", "Commande validée avec succès.");
            return "redirect:/admin/commandes";
        } catch (StockInsuffisantException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/commandes"; // revient au formulaire pour afficher l'erreur
        }
    }


    // POST /admin/commandes/{id}/annuler
    @PostMapping("/{id}/annuler")
    public String annuler(@PathVariable Long id) {
        commandeService.annulerCommande(id);
        return "redirect:/admin/commandes";
    }

    // Valider (workflow en attente)
    @PostMapping("/{id}/valider")
    public String valider(@PathVariable Long id) {
        commandeService.validerCommande(id);
        return "redirect:/admin/commandes";
    }


    // Historique simple (filtre statut)
    @GetMapping("/historique")
    public String historique(@RequestParam(required = false) String statut, Model model) {
        if (statut != null) {
            try {
                var s = ma.gstock.entities.enums.StatutCommande.valueOf(statut);
                model.addAttribute("commandes", commandeRepo.findByStatut(s));
            } catch (IllegalArgumentException e) {
                model.addAttribute("commandes", commandeRepo.findAll(Sort.by(Sort.Direction.DESC, "id")));
            }
        } else {
            model.addAttribute("commandes", commandeRepo.findAll(Sort.by(Sort.Direction.DESC, "id")));
        }
        model.addAttribute("q", "");
        return "admin/commandes/list";
    }



    

}

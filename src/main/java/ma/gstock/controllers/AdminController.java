package ma.gstock.controllers;

import ma.gstock.entities.Commande;
import ma.gstock.entities.LigneCommande;
import ma.gstock.entities.Produit;
import ma.gstock.entities.enums.StatutCommande;
import ma.gstock.repositories.CommandeRepository;
import ma.gstock.repositories.LigneCommandeRepository;
import ma.gstock.repositories.ProduitRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ProduitRepository produitRepo;
    private final CommandeRepository commandeRepo;
    private final LigneCommandeRepository ligneRepo;

    public AdminController(ProduitRepository produitRepo,
                           CommandeRepository commandeRepo,
                           LigneCommandeRepository ligneRepo) {
        this.produitRepo = produitRepo;
        this.commandeRepo = commandeRepo;
        this.ligneRepo = ligneRepo;
    }

    // GET /admin  -> templates/admin/home.html
    @GetMapping
    public String home(Model model) {
        List<Produit> all = produitRepo.findAll();
        long totalProduits = all.size();
        long stocksCritiques = all.stream()
                .filter(p -> p.getStockDisponible() != null && p.getSeuilAlerte() != null
                        && p.getStockDisponible() <= p.getSeuilAlerte())
                .count();
        long stocksNormaux = totalProduits - stocksCritiques;

        BigDecimal valeurStock = all.stream()
                .map(p -> p.getPrix().multiply(BigDecimal.valueOf(
                        p.getStockDisponible() == null ? 0 : p.getStockDisponible())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<Produit> dernierProduits = produitRepo
                .findAll(Sort.by(Sort.Direction.DESC, "id"))
                .stream().limit(5).collect(Collectors.toList());

        long totalCommandes = commandeRepo.count();

        model.addAttribute("totalProduits", totalProduits);
        model.addAttribute("totalCommandes", totalCommandes);
        model.addAttribute("stocksNormaux", stocksNormaux);
        model.addAttribute("stocksCritiques", stocksCritiques);
        model.addAttribute("valeurStock", valeurStock);
        model.addAttribute("dernierProduits", dernierProduits);
        return "admin/home";
    }

    // GET /admin/rapports -> templates/admin/rapports.html
    @GetMapping("/rapports")
    public String rapports(Model model) {
        List<Commande> allCmd = commandeRepo.findAll();

        long total = allCmd.size();
        long valides = allCmd.stream().filter(c -> c.getStatut() == StatutCommande.VALIDEE).count();
        long annulees = allCmd.stream().filter(c -> c.getStatut() == StatutCommande.ANNULEE).count();

        // ventes du jour (VALIDEE)
        LocalDate today = LocalDate.now();
        BigDecimal ventesDuJour = allCmd.stream()
                .filter(c -> c.getStatut() == StatutCommande.VALIDEE)
                .filter(c -> c.getDateCreation() != null &&
                        c.getDateCreation().toLocalDate().isEqual(today))
                .map(Commande::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // revenus par mois (année courante)
        Map<String, BigDecimal> revenusParMois = new LinkedHashMap<>();
        for (Month m : Month.values()) revenusParMois.put(m.name(), BigDecimal.ZERO);

        allCmd.stream()
                .filter(c -> c.getStatut() == StatutCommande.VALIDEE && c.getDateCreation() != null
                        && c.getDateCreation().getYear() == today.getYear())
                .forEach(c -> {
                    String key = c.getDateCreation().getMonth().name();
                    revenusParMois.put(key, revenusParMois.get(key).add(c.getTotal()));
                });

        // top produits vendus (sur toutes les lignes VALIDEE)
        List<LigneCommande> lignes = ligneRepo.findAll();
        Map<String, Integer> countByProduit = new HashMap<>();
        lignes.stream()
                .filter(l -> l.getCommande() != null && l.getCommande().getStatut() == StatutCommande.VALIDEE)
                .forEach(l -> {
                    String nom = l.getProduit().getNom();
                    countByProduit.put(nom, countByProduit.getOrDefault(nom, 0) + l.getQuantite());
                });

        List<Object[]> topProduits = countByProduit.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(10)
                .map(e -> new Object[]{e.getKey(), e.getValue()})
                .collect(Collectors.toList());

        model.addAttribute("totalCommandes", total);
        model.addAttribute("commandesValidees", valides);
        model.addAttribute("commandesAnnulees", annulees);
        model.addAttribute("ventesDuJour", ventesDuJour);
        model.addAttribute("revenusParMois", revenusParMois);
        model.addAttribute("topProduits", topProduits);

        return "admin/rapports";
    }
}

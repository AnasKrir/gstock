// ma/gstock/controllers/AdminStockController.java
package ma.gstock.controllers;

import ma.gstock.entities.Produit;
import ma.gstock.repositories.ProduitRepository;
import ma.gstock.services.StockService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/stocks")
public class AdminStockController {
    private final ProduitRepository produitRepo;
    private final StockService stockService;
    public AdminStockController(ProduitRepository produitRepo, StockService stockService) {
        this.produitRepo = produitRepo; this.stockService = stockService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("produits", produitRepo.findAll());
        return "admin/stocks/list";
    }

    // aligne avec <form action="/admin/stocks/{id}/set-quantite">
    @PostMapping("/{id}/set-quantite")
    public String setQuantite(@PathVariable Long id, @RequestParam Integer quantite) {
        Produit p = produitRepo.findById(id).orElseThrow();
        if (quantite != null && quantite >= 0) {
            p.setStockDisponible(quantite);
            produitRepo.save(p);
        }
        return "redirect:/admin/stocks";
    }

    // aligne avec <form action="/admin/stocks/{id}/set-seuil">
    @PostMapping("/{id}/set-seuil")
    public String setSeuil(@PathVariable Long id, @RequestParam Integer seuil) {
        Produit p = produitRepo.findById(id).orElseThrow();
        if (seuil != null && seuil >= 0) {
            p.setSeuilAlerte(seuil);
            produitRepo.save(p);
        }
        return "redirect:/admin/stocks";
    }
}

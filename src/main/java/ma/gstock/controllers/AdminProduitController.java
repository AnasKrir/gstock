package ma.gstock.controllers;

import ma.gstock.entities.Produit;
import ma.gstock.entities.enums.CategorieProduit;
import ma.gstock.repositories.ProduitRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/admin/produits")
public class AdminProduitController {

    private final ProduitRepository produitRepo;

    public AdminProduitController(ProduitRepository produitRepo) {
        this.produitRepo = produitRepo;
    }

    // GET /admin/produits  -> templates/admin/produits/list.html
    @GetMapping
    public String list(@RequestParam(name = "q", required = false) String q,
                               @RequestParam(name = "cat", required = false) CategorieProduit cat,
                               Model model) {


        List<Produit> produits;
        boolean hasQ = (q != null && !q.isBlank());
        if (cat != null && hasQ) {
            produits = produitRepo
                    .findByCategorieAndNomContainingIgnoreCaseOrderByIdDesc(cat, q.trim());
        } else if (cat != null) {
            produits = produitRepo.findByCategorieOrderByIdDesc(cat);
        } else if (hasQ) {
            produits = produitRepo.findByNomContainingIgnoreCaseOrderByIdDesc(q.trim());
        } else {
            produits = produitRepo.findAllOrderByIdDesc();
        }

        model.addAttribute("produits", produits);
        model.addAttribute("q", q);
        model.addAttribute("cat", cat);
        model.addAttribute("categories", CategorieProduit.values()); // pour la liste déroulante
        return "admin/produits/list";
    }


    // GET /admin/produits/nouveau -> templates/admin/produits/form.html
    @GetMapping("/nouveau")
    public String nouveau(Model model) {
        model.addAttribute("produit", null);
        model.addAttribute("categories", Arrays.asList(CategorieProduit.values()));
        return "admin/produits/form";
    }

    // GET /admin/produits/{id}/edit -> templates/admin/produits/form.html
    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        Produit p = produitRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Produit introuvable"));
        model.addAttribute("produit", p);
        model.addAttribute("categories", Arrays.asList(CategorieProduit.values()));
        return "admin/produits/form";
    }

    // POST /admin/produits/save
    @PostMapping("/save")
    public String save(
            @RequestParam(required = false) Long id,
            @RequestParam String nom,
            @RequestParam CategorieProduit categorie,
            @RequestParam BigDecimal prix,
            @RequestParam Integer stockDisponible,
            @RequestParam Integer seuilAlerte
    ) {
        Produit p = (id != null) ? produitRepo.findById(id)
                .orElse(Produit.builder().id(id).build()) : new Produit();

        p.setNom(nom);
        p.setCategorie(categorie);
        p.setPrix(prix);
        p.setStockDisponible(stockDisponible);
        p.setSeuilAlerte(seuilAlerte);
        produitRepo.save(p);

        return "redirect:/admin/produits";
    }

    // GET /admin/produits/{id}/delete
    @GetMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        produitRepo.deleteById(id);
        return "redirect:/admin/produits";
    }
}

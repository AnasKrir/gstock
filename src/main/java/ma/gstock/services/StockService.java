// src/main/java/ma/gstock/services/StockService.java
package ma.gstock.services;

import ma.gstock.entities.Produit;
import ma.gstock.repositories.ProduitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StockService {
    private final ProduitRepository produitRepo;
    public StockService(ProduitRepository produitRepo) { this.produitRepo = produitRepo; }

    @Transactional
    public boolean verifierStock(Long produitId, int quantiteSouhaitee) {
        Produit p = produitRepo.findById(produitId).orElseThrow();
        return p.getStockDisponible() >= quantiteSouhaitee;
    }

    @Transactional
    public void incrementerStock(Long produitId, int qte) {
        Produit p = produitRepo.findById(produitId).orElseThrow();
        p.setStockDisponible(p.getStockDisponible() + qte);
        produitRepo.save(p);
    }

    @Transactional
    public void decrementerStock(Long produitId, int qte) {
        Produit p = produitRepo.findById(produitId).orElseThrow();
        int after = Math.max(0, p.getStockDisponible() - qte);
        p.setStockDisponible(after);
        produitRepo.save(p);
    }

    @Transactional
    public void setQuantite(Long produitId, int quantite) {
        Produit p = produitRepo.findById(produitId).orElseThrow();
        p.setStockDisponible(Math.max(0, quantite));
        produitRepo.save(p);
    }

    @Transactional
    public void setSeuil(Long produitId, int seuil) {
        Produit p = produitRepo.findById(produitId).orElseThrow();
        p.setSeuilAlerte(Math.max(0, seuil));
        produitRepo.save(p);
    }
}

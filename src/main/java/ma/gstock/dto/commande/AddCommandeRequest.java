package ma.gstock.dto.commande;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

public class AddCommandeRequest {
    @NotNull
    private Long clientId;

    @NotNull
    private List<Ligne> lignes;

    public Long getClientId() { return clientId; }
    public void setClientId(Long clientId) { this.clientId = clientId; }
    public List<Ligne> getLignes() { return lignes; }
    public void setLignes(List<Ligne> lignes) { this.lignes = lignes; }

    public static class Ligne {
        @NotNull
        private Long produitId;

        @Min(1)
        private int quantite;

        // Optionnel : si non fourni, on prendra le prix depuis Produit
        private BigDecimal prixUnitaire; // TODO: adapte si tu utilises un autre nom

        public Long getProduitId() { return produitId; }
        public void setProduitId(Long produitId) { this.produitId = produitId; }
        public int getQuantite() { return quantite; }
        public void setQuantite(int quantite) { this.quantite = quantite; }
        public BigDecimal getPrixUnitaire() { return prixUnitaire; }
        public void setPrixUnitaire(BigDecimal prixUnitaire) { this.prixUnitaire = prixUnitaire; }
    }
}

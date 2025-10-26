package ma.gstock.dto.commande;

import jakarta.validation.constraints.NotNull;
import ma.gstock.entities.enums.StatutCommande;

public class ChangeStatutRequest {
    @NotNull
    private StatutCommande statut;

    public StatutCommande getStatut() { return statut; }
    public void setStatut(StatutCommande statut) { this.statut = statut; }
}

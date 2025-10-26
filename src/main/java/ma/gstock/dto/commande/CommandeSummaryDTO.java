package ma.gstock.dto.commande;

import ma.gstock.entities.enums.StatutCommande;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CommandeSummaryDTO {
    private Long id;
    private String clientNomComplet;
    private LocalDateTime dateCreation;
    private StatutCommande statut;
    private BigDecimal total;

    public CommandeSummaryDTO(Long id, String clientNomComplet, LocalDateTime dateCreation,
                              StatutCommande statut, BigDecimal total) {
        this.id = id;
        this.clientNomComplet = clientNomComplet;
        this.dateCreation = dateCreation;
        this.statut = statut;
        this.total = total;
    }

    public Long getId() { return id; }
    public String getClientNomComplet() { return clientNomComplet; }
    public LocalDateTime getDateCreation() { return dateCreation; }
    public StatutCommande getStatut() { return statut; }
    public BigDecimal getTotal() { return total; }
}

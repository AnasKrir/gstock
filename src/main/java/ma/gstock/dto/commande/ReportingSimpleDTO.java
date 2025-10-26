package ma.gstock.dto.commande;

import java.math.BigDecimal;

public class ReportingSimpleDTO {
    private long totalCommandes;
    private long enAttente;
    private long validees;
    private long annulees;
    private BigDecimal chiffreAffairesTotal; // sur commandes VALIDEE

    public ReportingSimpleDTO(long totalCommandes, long enAttente, long validees, long annulees, BigDecimal ca) {
        this.totalCommandes = totalCommandes;
        this.enAttente = enAttente;
        this.validees = validees;
        this.annulees = annulees;
        this.chiffreAffairesTotal = ca;
    }

    public long getTotalCommandes() { return totalCommandes; }
    public long getEnAttente() { return enAttente; }
    public long getValidees() { return validees; }
    public long getAnnulees() { return annulees; }
    public BigDecimal getChiffreAffairesTotal() { return chiffreAffairesTotal; }
}

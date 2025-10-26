package ma.gstock.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity @Table(name = "lignes_commande")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LigneCommande {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional=false, fetch = FetchType.LAZY)
    private Commande commande;

    @ManyToOne(optional=false, fetch = FetchType.LAZY)
    private Produit produit;

    @Column(nullable=false)
    private Integer quantite;

    @Column(nullable=false, precision=12, scale=2)
    private BigDecimal prixUnitaire;

    @Column(nullable=false, precision=14, scale=2)
    private BigDecimal totalLigne;
}

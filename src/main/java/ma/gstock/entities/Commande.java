package ma.gstock.entities;

import jakarta.persistence.*;
import lombok.*;
import ma.gstock.entities.enums.StatutCommande;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity @Table(name = "commandes")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Commande {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private LocalDateTime dateCreation;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=20)
    private StatutCommande statut;

    @ManyToOne(optional=false, fetch = FetchType.LAZY)
    private Client client;

    @ManyToOne(optional=false, fetch = FetchType.LAZY)
    private Utilisateur vendeur;

    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<LigneCommande> lignes = new ArrayList<>();

    @Column(nullable=false, precision=14, scale=2)
    private BigDecimal total;

    @PrePersist
    void prePersist() {
        if (dateCreation == null) dateCreation = LocalDateTime.now();
        if (statut == null) statut = StatutCommande.EN_ATTENTE;
    }
}

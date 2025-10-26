package ma.gstock.entities;

import jakarta.persistence.*;
import lombok.*;
import ma.gstock.entities.enums.CategorieProduit;

import java.math.BigDecimal;

@Entity @Table(name = "produits")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Produit {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, length=160)
    private String nom;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=30)
    private CategorieProduit categorie;

    @Column(nullable=false, precision=12, scale=2)
    private BigDecimal prix;

    @Column(nullable=false)
    private Integer stockDisponible;

    @Column(nullable=false)
    private Integer seuilAlerte = 10;
}

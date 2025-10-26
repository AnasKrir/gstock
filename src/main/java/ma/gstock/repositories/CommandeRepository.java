// ma.gstock.repositories.CommandeRepository
package ma.gstock.repositories;

import ma.gstock.entities.Commande;
import ma.gstock.entities.enums.StatutCommande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CommandeRepository extends JpaRepository<Commande, Long> {
    List<Commande> findByStatut(StatutCommande statut);
    List<Commande> findByDateCreationBetween(LocalDateTime start, LocalDateTime end);
    @Query("""
        select c from Commande c
        left join fetch c.lignes l
        left join fetch l.produit
        where c.id = :id
    """)
    Optional<Commande> findByIdWithLignesAndProduits(Long id);
    // Empêche la suppression d’un client lié à au moins une commande
    boolean existsByClientId(Long clientId);
    // Recherche par NOM du client (insensible à la casse), tri id DESC
    List<Commande> findByClient_NomContainingIgnoreCaseOrderByIdDesc(String nom);

    // (optionnel) tout, déjà possible avec findAll(Sort), mais pratique si tu veux une signature dédiée
    List<Commande> findAllByOrderByIdDesc();

    // (optionnel) si tu veux aussi filtrer par vendeur
    // List<Commande> findByVendeur_UsernameContainingIgnoreCaseOrderByIdDesc(String username);
}

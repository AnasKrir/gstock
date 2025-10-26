// ma/gstock/repositories/ProduitRepository.java
package ma.gstock.repositories;

import ma.gstock.entities.Produit;
import ma.gstock.entities.enums.CategorieProduit; // adapte le package si ton enum est ailleurs
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProduitRepository extends JpaRepository<Produit, Long> {

    @Query("select p from Produit p order by p.id desc")
    List<Produit> findAllOrderByIdDesc();

    List<Produit> findByNomContainingIgnoreCaseOrderByIdDesc(String nom);

    List<Produit> findByCategorieOrderByIdDesc(CategorieProduit categorie);

    List<Produit> findByCategorieAndNomContainingIgnoreCaseOrderByIdDesc(
            CategorieProduit categorie, String nom);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update Produit p
           set p.stockDisponible = p.stockDisponible - :q
         where p.id = :id and p.stockDisponible >= :q
    """)
    int decrementStockIfEnough(@Param("id") Long produitId, @Param("q") int quantite);
}

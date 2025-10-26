// ma/gstock/repositories/ClientRepository.java
package ma.gstock.repositories;

import ma.gstock.entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ClientRepository extends JpaRepository<Client, Long> {

    @Query("select c from Client c order by c.id desc")
    List<Client> findAllOrderByIdDesc();

    List<Client> findByNomContainingIgnoreCaseOrderByIdDesc(String nom);
}

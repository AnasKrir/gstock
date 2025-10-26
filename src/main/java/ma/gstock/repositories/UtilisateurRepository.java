package ma.gstock.repositories;

import ma.gstock.entities.Utilisateur;
import ma.gstock.entities.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    Optional<Utilisateur> findByUsername(String username);
    boolean existsByUsername(String username);
    List<Utilisateur> findByRole(Role role);
}

package ma.gstock.config;

import ma.gstock.entities.Utilisateur;
import ma.gstock.entities.enums.Role; // adapte le nom si besoin
import ma.gstock.repositories.UtilisateurRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DevSeedConfig {

    @Bean
    CommandLineRunner seedUsers(UtilisateurRepository repo, PasswordEncoder encoder) {
        return args -> {
            if (repo.findByUsername("admin").isEmpty()) {
                Utilisateur u = new Utilisateur();
                u.setUsername("admin");
                u.setPassword(encoder.encode("admin"));
                u.setRole(Role.ADMIN); // ou setRole("ADMIN") selon ton entité
                u.setEnabled(true);
                repo.save(u);
            }
            if (repo.findByUsername("vendeur").isEmpty()) {
                Utilisateur u = new Utilisateur();
                u.setUsername("vendeur");
                u.setPassword(encoder.encode("vendeur"));
                u.setRole(Role.VENDEUR);
                u.setEnabled(true);
                repo.save(u);
            }
        };
    }
}

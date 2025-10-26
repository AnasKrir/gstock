package ma.gstock.entities;

import jakarta.persistence.*;
import lombok.*;
import ma.gstock.entities.enums.Role;

@Entity
@Table(name = "utilisateurs", uniqueConstraints = {
        @UniqueConstraint(name = "uk_utilisateur_username", columnNames = "username")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Utilisateur {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String username;

    @Column(nullable = false)
    private String password; // stocké en BCrypt

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    private boolean enabled = true;
}

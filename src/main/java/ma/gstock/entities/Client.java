// ma/gstock/entities/Client.java
package ma.gstock.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity @Table(name = "clients")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Client {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank @Size(max = 120)
    @Column(nullable=false, length=120)
    private String nom;

    @Email @Size(max = 160)
    @Column(length=160)
    private String email;

    @Size(max = 30)
    @Column(length=30)
    private String telephone;

    @Size(max = 255)
    @Column(length=255)
    private String adresse;
}

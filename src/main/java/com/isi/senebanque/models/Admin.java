package com.isi.senebanque.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "administrateurs")
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "nom" , length = 20, nullable = false)
    private String nom;
    @Column(name = "prenom" , length = 50, nullable = false)
    private String prenom;
    @Column(name = "email" , length = 150, nullable = false, unique = true)
    private String email;
    @Column(name = "username", nullable = false)
    private String username;
    @Column(name = "password", nullable = false)
    private String password;
    @OneToMany(mappedBy = "utilisateur", fetch = FetchType.LAZY)
    private List<TicketSupport> tickets_support;

    @Override
    public String toString() {
        return prenom + " " + nom;
    }
}

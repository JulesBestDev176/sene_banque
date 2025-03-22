package com.isi.senebanque.models;


import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "clients")
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "nom" , length = 20, nullable = false)
    private String nom;
    @Column(name = "prenom" , length = 50, nullable = false)
    private String prenom;
    @Column(name = "email" , length = 150, nullable = false, unique = true)
    private String email;
    @Column(name = "telephone" , length = 13, nullable = false, unique = true)
    private String telephone;
    @Column(name = "username" , length = 20, nullable = false, unique = true)
    private String username;
    @Column(name = "password" , nullable = false)
    private String password;
    @Column(name = "adresse" , length = 200, nullable = false)
    private String adresse;
    @Column(name = "date_inscription" , nullable = false)
    @Temporal(TemporalType.DATE)
    private Date date_inscription = new Date();
    @Column(name = "statut" , nullable = false)
    private String statut = "actif";
    @OneToMany(mappedBy = "client", fetch = FetchType.LAZY)
    private List<Compte> comptes;
    @OneToMany(mappedBy = "client", fetch = FetchType.LAZY)
    private List<Credit> credits;
    @OneToMany(mappedBy = "client", fetch = FetchType.LAZY)
    private List<TicketSupport> tickets;

    @Override
    public String toString() {
        return prenom + " " + nom;
    }
}

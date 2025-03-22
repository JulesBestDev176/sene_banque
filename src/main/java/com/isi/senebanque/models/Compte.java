package com.isi.senebanque.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Getter
@Setter
@Table(name = "comptes")
public class Compte {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "numero", length = 20, nullable = false)
    private String numero;
    @Column(name = "type_compte" , nullable = false)
    private String type_compte;
    @Column(name = "solde" , nullable = false)
    private double solde;
    @Column(name = "frais_bancaire")
    private double frais_bancaire;
    @Column(name = "date_creation", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date date_creation;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;
    @OneToMany(mappedBy = "compte_source", fetch = FetchType.LAZY)
    private List<Transaction> transactionsSource;
    @OneToMany(mappedBy = "compte_dest", fetch = FetchType.LAZY)
    private List<Transaction> transactionsDest;
    @OneToMany(mappedBy = "compte", fetch = FetchType.LAZY)
    private List<CarteBancaire> cartes_bancaires;
    @Column(name = "statut", nullable = false)
    private String statut = "ACTIF";

    @Override
    public String toString() {
        return numero;
    }
}


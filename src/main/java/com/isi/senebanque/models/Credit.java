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
@Table(name = "credits")
public class Credit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "montant", nullable = false)
    private double montant;
    @Column(name = "taux_interet", nullable = false)
    private double taux_interet;
    @Column(name = "date_limite", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date date_limite;
    @Column(name = "mensualite", nullable = false)
    private double mensualite;
    @Column(name = "date_demande", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date date_demande;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;
    @Column(name = "statut", nullable = false)
    private String statut = "en attente";
    @OneToMany(mappedBy = "credit", fetch = FetchType.LAZY)
    private List<Remboursement> remboursements;
}

package com.isi.senebanque.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "carte_bancaires")
public class CarteBancaire {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "numero", length = 20, nullable = false)
    private String numero;
    @Column(name = "cvv", length = 3, nullable = false)
    private String cvv;
    @Column(name = "date_expiration", nullable = false)
    private Date date_expiration;
    @Column(name = "solde", nullable = false)
    private double solde;
    @Column(name = "statut", nullable = false)
    private String statut = "actif";
    @Column(name = "code_pin", length = 5, nullable = false)
    private String code_pin;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compte_id", nullable = false)
    private Compte compte;
}

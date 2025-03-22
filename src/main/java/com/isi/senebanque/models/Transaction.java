package com.isi.senebanque.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "numero", nullable=false)
    private String numero;
    @Column(name = "type_transaction", nullable = false )
    private String type_transaction;
    @Column(name = "montant", nullable = false)
    private double montant;
    @Column(name = "date_transaction", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date date_transaction;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compte_source_id")
    private Compte compte_source;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compte_dest_id")
    private Compte compte_dest;
    @Column(name = "statut_transaction")
    private String statut;
}




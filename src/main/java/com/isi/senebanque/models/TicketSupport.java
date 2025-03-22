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
@Table(name = "tickets_support")
public class TicketSupport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "sujet", length = 50, nullable = false)
    private String sujet;
    @Column(name = "description", length = 1000, nullable = false)
    private String description;
    @Column(name = "date_ouverture", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date date_ouverture;
    @Column(name = "statut", nullable = false)
    private String statut = "en cours";
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Admin utilisateur;
}

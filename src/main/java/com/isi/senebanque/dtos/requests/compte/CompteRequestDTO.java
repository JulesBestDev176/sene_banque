package com.isi.senebanque.dtos.requests.compte;

import lombok.*;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class CompteRequestDTO {
    private String numero;
    private String typeCompte;
    private double solde;
    private double fraisBancaire;
    private Date dateCreation;
    private Long clientId;
    private String statut;
}

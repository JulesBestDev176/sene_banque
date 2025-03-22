package com.isi.senebanque.dtos.responses.credit;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Data
@Getter
@Setter
public class CreditResponseDTO {
    private Long id;
    private double montant;
    private double tauxInteret;
    private Date dateLimite;
    private double mensualite;
    private Date dateDemande;
    private Long clientId;
    private String clientNom;
    private String clientPrenom;
    private String statut;
}

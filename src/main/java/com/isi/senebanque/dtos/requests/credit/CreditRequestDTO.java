package com.isi.senebanque.dtos.requests.credit;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Data
@Getter
@Setter
public class CreditRequestDTO {
    private double montant;
    private double tauxInteret;
    private Date dateLimite;
    private double mensualite;
    private Date dateDemande;
    private Long clientId;
}

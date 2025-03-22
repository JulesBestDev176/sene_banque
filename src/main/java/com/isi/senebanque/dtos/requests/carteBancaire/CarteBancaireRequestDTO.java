package com.isi.senebanque.dtos.requests.carteBancaire;

import lombok.*;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class CarteBancaireRequestDTO {
    private String numero;
    private String cvv;
    private Date dateExpiration;
    private double solde;
    private String statut;
    private String codePin;
    private Long compteId;
}

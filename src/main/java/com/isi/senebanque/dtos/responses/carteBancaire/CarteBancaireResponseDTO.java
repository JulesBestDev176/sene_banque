package com.isi.senebanque.dtos.responses.carteBancaire;

import lombok.*;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class CarteBancaireResponseDTO {
    private Long id;
    private String numero;
    private String cvv;
    private Date dateExpiration;
    private double solde;
    private String statut;
    private String codePin;
    private Long compteId;
    private String compteNumero;
}

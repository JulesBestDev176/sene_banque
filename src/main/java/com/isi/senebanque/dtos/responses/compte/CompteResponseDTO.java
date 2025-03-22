package com.isi.senebanque.dtos.responses.compte;
import lombok.*;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class CompteResponseDTO {
    private Long id;
    private String numero;
    private String typeCompte;
    private double solde;
    private double fraisBancaire;
    private Date dateCreation;
    private Long clientId;
    private String statut;
}

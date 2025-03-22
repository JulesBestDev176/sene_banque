package com.isi.senebanque.dtos.responses.transaction;

import lombok.*;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
public class TransactionResponseDTO {
    private Long id;
    private String numero;
    private String typeTransaction;
    private double montant;
    private Date dateTransaction;
    private Long compteSourceId;
    private Long compteDestId;
    private String statut;



    public String getCompteSourceFormatted() {
        return "Client " + compteSourceId;
    }
}

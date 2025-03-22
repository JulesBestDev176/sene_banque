package com.isi.senebanque.dtos.requests.transaction;

import lombok.*;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class TransactionRequestDTO {
    private String numero;
    private String typeTransaction;
    private double montant;
    private Date dateTransaction;
    private Long compteSourceId;
    private Long compteDestId;
    private String statut;
}

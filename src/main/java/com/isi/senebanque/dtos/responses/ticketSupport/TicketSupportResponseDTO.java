package com.isi.senebanque.dtos.responses.ticketSupport;

import lombok.*;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class TicketSupportResponseDTO {
    private Long id;
    private String sujet;
    private String description;
    private Date dateOuverture;
    private String statut;
    private Long clientId;
    private Long utilisateurId;
}

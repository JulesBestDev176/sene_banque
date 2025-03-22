package com.isi.senebanque.dtos.requests.ticketSupport;

import lombok.*;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class TicketSupportRequestDTO {
    private String sujet;
    private String description;
    private Date dateOuverture;
    private Long clientId;
    private Long utilisateurId;
}

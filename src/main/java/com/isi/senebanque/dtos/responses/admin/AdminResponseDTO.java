package com.isi.senebanque.dtos.responses.admin;


import com.isi.senebanque.dtos.responses.ticketSupport.TicketSupportResponseDTO;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class AdminResponseDTO {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String username;
}

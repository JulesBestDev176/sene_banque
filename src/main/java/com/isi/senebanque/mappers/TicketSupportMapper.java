package com.isi.senebanque.mappers;

import com.isi.senebanque.dtos.requests.ticketSupport.TicketSupportRequestDTO;
import com.isi.senebanque.dtos.responses.ticketSupport.TicketSupportResponseDTO;
import com.isi.senebanque.models.TicketSupport;

public class TicketSupportMapper {


    public static TicketSupport toEntity(TicketSupportRequestDTO ticketSupportRequestDTO) {
        return TicketSupport.builder()
                .sujet(ticketSupportRequestDTO.getSujet())
                .description(ticketSupportRequestDTO.getDescription())
                .date_ouverture(ticketSupportRequestDTO.getDateOuverture())
                .statut("en cours")
                .build();
    }


    public static TicketSupportResponseDTO toResponseDTO(TicketSupport ticketSupport) {
        return TicketSupportResponseDTO.builder()
                .id(ticketSupport.getId())
                .sujet(ticketSupport.getSujet())
                .description(ticketSupport.getDescription())
                .dateOuverture(ticketSupport.getDate_ouverture())
                .statut(ticketSupport.getStatut())
                .clientId(ticketSupport.getClient() != null ? ticketSupport.getClient().getId() : null)
                .utilisateurId(ticketSupport.getUtilisateur() != null ? ticketSupport.getUtilisateur().getId() : null)
                .build();
    }


    public static void updateEntityFromDTO(TicketSupportRequestDTO ticketSupportRequestDTO, TicketSupport ticketSupport) {
        ticketSupport.setSujet(ticketSupportRequestDTO.getSujet());
        ticketSupport.setDescription(ticketSupportRequestDTO.getDescription());
        ticketSupport.setDate_ouverture(ticketSupportRequestDTO.getDateOuverture());
    }
}

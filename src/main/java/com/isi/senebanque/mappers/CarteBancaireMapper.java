package com.isi.senebanque.mappers;

import com.isi.senebanque.dtos.requests.carteBancaire.CarteBancaireRequestDTO;
import com.isi.senebanque.dtos.responses.carteBancaire.CarteBancaireResponseDTO;
import com.isi.senebanque.models.CarteBancaire;

public class CarteBancaireMapper {


    public static CarteBancaire toEntity(CarteBancaireRequestDTO carteBancaireRequestDTO) {
        return CarteBancaire.builder()
                .numero(carteBancaireRequestDTO.getNumero())
                .cvv(carteBancaireRequestDTO.getCvv())
                .date_expiration(carteBancaireRequestDTO.getDateExpiration())
                .solde(carteBancaireRequestDTO.getSolde())
                .statut(carteBancaireRequestDTO.getStatut())
                .code_pin(carteBancaireRequestDTO.getCodePin())
                .build();
    }


    public static CarteBancaireResponseDTO toResponseDTO(CarteBancaire carteBancaire) {
        return CarteBancaireResponseDTO.builder()
                .id(carteBancaire.getId())
                .numero(carteBancaire.getNumero())
                .cvv(carteBancaire.getCvv())
                .dateExpiration(carteBancaire.getDate_expiration())
                .solde(carteBancaire.getSolde())
                .statut(carteBancaire.getStatut())
                .codePin(carteBancaire.getCode_pin())
                .compteId(carteBancaire.getCompte() != null ? carteBancaire.getCompte().getId() : null)
                .compteNumero(carteBancaire.getCompte() != null ? carteBancaire.getCompte().getNumero() : null)
                .build();
    }


    public static void updateEntityFromDTO(CarteBancaireRequestDTO carteBancaireRequestDTO, CarteBancaire carteBancaire) {
        carteBancaire.setNumero(carteBancaireRequestDTO.getNumero());
        carteBancaire.setCvv(carteBancaireRequestDTO.getCvv());
        carteBancaire.setDate_expiration(carteBancaireRequestDTO.getDateExpiration());
        carteBancaire.setSolde(carteBancaireRequestDTO.getSolde());
        carteBancaire.setStatut(carteBancaireRequestDTO.getStatut());
        carteBancaire.setCode_pin(carteBancaireRequestDTO.getCodePin());
    }
}

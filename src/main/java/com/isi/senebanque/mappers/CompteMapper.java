package com.isi.senebanque.mappers;

import com.isi.senebanque.dtos.requests.compte.CompteRequestDTO;
import com.isi.senebanque.dtos.responses.compte.CompteResponseDTO;
import com.isi.senebanque.models.Compte;

public class CompteMapper {


    public static Compte toEntity(CompteRequestDTO compteRequestDTO) {
        return Compte.builder()
                .numero(compteRequestDTO.getNumero())
                .type_compte(compteRequestDTO.getTypeCompte())
                .solde(compteRequestDTO.getSolde())
                .frais_bancaire(compteRequestDTO.getFraisBancaire())
                .date_creation(compteRequestDTO.getDateCreation())
                .statut(compteRequestDTO.getStatut())
                .build();
    }


    public static CompteResponseDTO toResponseDTO(Compte compte) {
        return CompteResponseDTO.builder()
                .id(compte.getId())
                .numero(compte.getNumero())
                .typeCompte(compte.getType_compte())
                .solde(compte.getSolde())
                .fraisBancaire(compte.getFrais_bancaire())
                .dateCreation(compte.getDate_creation())
                .clientId(compte.getClient() != null ? compte.getClient().getId() : null)
                .statut(compte.getStatut())
                .build();
    }


    public static void updateEntityFromDTO(CompteRequestDTO compteRequestDTO, Compte compte) {
        compte.setNumero(compteRequestDTO.getNumero());
        compte.setType_compte(compteRequestDTO.getTypeCompte());
        compte.setSolde(compteRequestDTO.getSolde());
        compte.setFrais_bancaire(compteRequestDTO.getFraisBancaire());
        compte.setDate_creation(compteRequestDTO.getDateCreation());
        compte.setStatut(compteRequestDTO.getStatut());
    }
}

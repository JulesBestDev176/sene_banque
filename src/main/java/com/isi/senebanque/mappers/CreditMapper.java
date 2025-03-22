package com.isi.senebanque.mappers;

import com.isi.senebanque.dtos.requests.credit.CreditRequestDTO;
import com.isi.senebanque.dtos.responses.credit.CreditResponseDTO;
import com.isi.senebanque.models.Credit;

public class CreditMapper {


    public static Credit toEntity(CreditRequestDTO creditRequestDTO) {
        Credit credit = new Credit();
        credit.setMontant(creditRequestDTO.getMontant());
        credit.setTaux_interet(creditRequestDTO.getTauxInteret());
        credit.setDate_limite(creditRequestDTO.getDateLimite());
        credit.setMensualite(creditRequestDTO.getMensualite());
        credit.setDate_demande(creditRequestDTO.getDateDemande());

        return credit;
    }


    public static CreditResponseDTO toResponseDTO(Credit credit) {
        CreditResponseDTO creditResponseDTO = new CreditResponseDTO();
        creditResponseDTO.setId(credit.getId());
        creditResponseDTO.setMontant(credit.getMontant());
        creditResponseDTO.setTauxInteret(credit.getTaux_interet());
        creditResponseDTO.setDateLimite(credit.getDate_limite());
        creditResponseDTO.setMensualite(credit.getMensualite());
        creditResponseDTO.setDateDemande(credit.getDate_demande());
        creditResponseDTO.setClientId(credit.getClient() != null ? credit.getClient().getId() : null);


        if (credit.getClient() != null) {
            creditResponseDTO.setClientNom(credit.getClient().getNom());
            creditResponseDTO.setClientPrenom(credit.getClient().getPrenom());
        }

        creditResponseDTO.setStatut(credit.getStatut());
        return creditResponseDTO;
    }


    public static void updateEntityFromDTO(CreditRequestDTO creditRequestDTO, Credit credit) {
        credit.setMontant(creditRequestDTO.getMontant());
        credit.setTaux_interet(creditRequestDTO.getTauxInteret());
        credit.setDate_limite(creditRequestDTO.getDateLimite());
        credit.setMensualite(creditRequestDTO.getMensualite());
        credit.setDate_demande(creditRequestDTO.getDateDemande());

    }
}

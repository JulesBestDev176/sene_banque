package com.isi.senebanque.mappers;


import com.isi.senebanque.dtos.requests.client.ClientRequestDTO;
import com.isi.senebanque.models.Client;


import java.util.Date;

public class ClientMapper {

    public static Client toEntity(ClientRequestDTO clientRequestDTO) {
        Client client = new Client();
        client.setNom(clientRequestDTO.getNom());
        client.setPrenom(clientRequestDTO.getPrenom());
        client.setEmail(clientRequestDTO.getEmail());
        client.setTelephone(clientRequestDTO.getTelephone());
        client.setAdresse(clientRequestDTO.getAdresse());
        client.setDate_inscription(new Date());
        client.setStatut("actif");
        return client;
    }
}

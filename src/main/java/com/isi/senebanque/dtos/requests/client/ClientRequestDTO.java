package com.isi.senebanque.dtos.requests.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientRequestDTO {
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String adresse;
}

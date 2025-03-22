package com.isi.senebanque.dtos.requests.admin;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminRequestDTO {
    private String nom;
    private String prenom;
    private String email;
    private String username;
    private String password;
}

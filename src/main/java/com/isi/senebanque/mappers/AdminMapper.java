package com.isi.senebanque.mappers;

import com.isi.senebanque.dtos.requests.admin.AdminRequestDTO;
import com.isi.senebanque.dtos.responses.admin.AdminResponseDTO;
import com.isi.senebanque.models.Admin;

public class AdminMapper {


    public static Admin toEntity(AdminRequestDTO adminRequestDTO) {
        return Admin.builder()
                .nom(adminRequestDTO.getNom())
                .prenom(adminRequestDTO.getPrenom())
                .email(adminRequestDTO.getEmail())
                .username(adminRequestDTO.getUsername())
                .password(adminRequestDTO.getPassword())
                .build();
    }


    public static AdminResponseDTO toResponseDTO(Admin admin) {
        return AdminResponseDTO.builder()
                .id(admin.getId())
                .nom(admin.getNom())
                .prenom(admin.getPrenom())
                .email(admin.getEmail())
                .username(admin.getUsername())
                .build();
    }


    public static void updateEntityFromDTO(AdminRequestDTO adminRequestDTO, Admin admin) {
        admin.setNom(adminRequestDTO.getNom());
        admin.setPrenom(adminRequestDTO.getPrenom());
        admin.setEmail(adminRequestDTO.getEmail());
        admin.setUsername(adminRequestDTO.getUsername());
        admin.setPassword(adminRequestDTO.getPassword());
    }
}

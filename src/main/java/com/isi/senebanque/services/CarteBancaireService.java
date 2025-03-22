package com.isi.senebanque.services;

import com.isi.senebanque.dtos.requests.carteBancaire.CarteBancaireRequestDTO;
import com.isi.senebanque.dtos.responses.carteBancaire.CarteBancaireResponseDTO;
import com.isi.senebanque.mappers.CarteBancaireMapper;
import com.isi.senebanque.models.CarteBancaire;
import com.isi.senebanque.models.Compte;
import com.isi.senebanque.utils.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class CarteBancaireService {

    public List<CarteBancaireResponseDTO> getAllCartesBancaires() {
        EntityManager entityManager = JpaUtil.getEntityManager();
        try {
            TypedQuery<CarteBancaire> query = entityManager.createQuery(
                    "SELECT c FROM CarteBancaire c ORDER BY c.id DESC", CarteBancaire.class);
            List<CarteBancaire> cartes = query.getResultList();
            return cartes.stream()
                    .map(CarteBancaireMapper::toResponseDTO)
                    .collect(Collectors.toList());
        } finally {
            entityManager.close();
        }
    }

    public List<CarteBancaireResponseDTO> getCartesBancairesByCompte(Long compteId) {
        EntityManager entityManager = JpaUtil.getEntityManager();
        try {
            TypedQuery<CarteBancaire> query = entityManager.createQuery(
                    "SELECT c FROM CarteBancaire c WHERE c.compte.id = :compteId ORDER BY c.id DESC",
                    CarteBancaire.class);
            query.setParameter("compteId", compteId);
            List<CarteBancaire> cartes = query.getResultList();
            return cartes.stream()
                    .map(CarteBancaireMapper::toResponseDTO)
                    .collect(Collectors.toList());
        } finally {
            entityManager.close();
        }
    }

    public CarteBancaireResponseDTO getCarteBancaireById(Long id) {
        EntityManager entityManager = JpaUtil.getEntityManager();
        try {
            CarteBancaire carte = entityManager.find(CarteBancaire.class, id);
            if (carte == null) {
                return null;
            }
            return CarteBancaireMapper.toResponseDTO(carte);
        } finally {
            entityManager.close();
        }
    }

    public CarteBancaireResponseDTO ajouterCarteBancaire(CarteBancaireRequestDTO carteBancaireRequestDTO) {
        EntityManager entityManager = JpaUtil.getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();


            Compte compte = entityManager.find(Compte.class, carteBancaireRequestDTO.getCompteId());
            if (compte == null) {
                return null;
            }


            if (!"ACTIF".equals(compte.getStatut())) {
                return null;
            }


            if (carteBancaireRequestDTO.getNumero() == null || carteBancaireRequestDTO.getNumero().isEmpty()) {
                carteBancaireRequestDTO.setNumero(genererNumeroCarte());
            }

            if (carteBancaireRequestDTO.getCvv() == null || carteBancaireRequestDTO.getCvv().isEmpty()) {
                carteBancaireRequestDTO.setCvv(genererCVV());
            }

            if (carteBancaireRequestDTO.getCodePin() == null || carteBancaireRequestDTO.getCodePin().isEmpty()) {
                carteBancaireRequestDTO.setCodePin(genererCodePIN());
            }


            if (carteBancaireRequestDTO.getDateExpiration() == null) {
                LocalDate dateExpiration = LocalDate.now().plusYears(5);
                carteBancaireRequestDTO.setDateExpiration(Date.from(dateExpiration.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            }


            if (carteBancaireRequestDTO.getStatut() == null || carteBancaireRequestDTO.getStatut().isEmpty()) {
                carteBancaireRequestDTO.setStatut("ACTIF");
            }


            CarteBancaire carte = CarteBancaireMapper.toEntity(carteBancaireRequestDTO);
            carte.setCompte(compte);

            entityManager.persist(carte);
            transaction.commit();

            return CarteBancaireMapper.toResponseDTO(carte);
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
            return null;
        } finally {
            entityManager.close();
        }
    }

    public boolean changerStatutCarte(Long id, String nouveauStatut) {
        if (!nouveauStatut.equals("ACTIF") && !nouveauStatut.equals("INACTIF") && !nouveauStatut.equals("BLOQUE")) {
            return false;
        }

        EntityManager entityManager = JpaUtil.getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();

            CarteBancaire carte = entityManager.find(CarteBancaire.class, id);
            if (carte == null) {
                return false;
            }

            carte.setStatut(nouveauStatut);
            entityManager.merge(carte);
            transaction.commit();

            return true;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        } finally {
            entityManager.close();
        }
    }

    public boolean activerCarte(Long id) {
        return changerStatutCarte(id, "ACTIF");
    }

    public boolean desactiverCarte(Long id) {
        return changerStatutCarte(id, "INACTIF");
    }

    public boolean bloquerCarte(Long id) {
        return changerStatutCarte(id, "BLOQUE");
    }

    public List<CarteBancaireResponseDTO> rechercherCartesBancaires(String terme, String statut) {
        EntityManager entityManager = JpaUtil.getEntityManager();
        try {
            String jpql = "SELECT c FROM CarteBancaire c WHERE 1=1";

            if (statut != null && !statut.equals("Tous")) {
                jpql += " AND c.statut = :statut";
            }

            if (terme != null && !terme.isEmpty()) {
                jpql += " AND (LOWER(c.numero) LIKE :terme OR CAST(c.id AS string) LIKE :terme)";
            }

            jpql += " ORDER BY c.id DESC";

            TypedQuery<CarteBancaire> query = entityManager.createQuery(jpql, CarteBancaire.class);

            if (statut != null && !statut.equals("Tous")) {
                query.setParameter("statut", statut);
            }

            if (terme != null && !terme.isEmpty()) {
                query.setParameter("terme", "%" + terme.toLowerCase() + "%");
            }

            List<CarteBancaire> cartes = query.getResultList();

            return cartes.stream()
                    .map(CarteBancaireMapper::toResponseDTO)
                    .collect(Collectors.toList());
        } finally {
            entityManager.close();
        }
    }



    private String genererNumeroCarte() {

        Random random = new Random();
        StringBuilder numero = new StringBuilder();

        for (int i = 0; i < 4; i++) {
            int groupe = 1000 + random.nextInt(9000);
            numero.append(groupe);
            if (i < 3) {
                numero.append(" ");
            }
        }

        return numero.toString();
    }

    private String genererCVV() {

        Random random = new Random();
        return String.format("%03d", random.nextInt(1000));
    }

    private String genererCodePIN() {

        Random random = new Random();
        return String.format("%04d", random.nextInt(10000));
    }

    public List<CarteBancaireResponseDTO> getCartesBancairesByClient(Long clientId) {
        EntityManager entityManager = JpaUtil.getEntityManager();
        try {

            TypedQuery<Compte> queryComptes = entityManager.createQuery(
                    "SELECT c FROM Compte c WHERE c.client.id = :clientId", Compte.class);
            queryComptes.setParameter("clientId", clientId);
            List<Compte> comptes = queryComptes.getResultList();


            if (comptes.isEmpty()) {
                return new ArrayList<>();
            }


            List<Long> compteIds = comptes.stream()
                    .map(Compte::getId)
                    .collect(Collectors.toList());

            
            TypedQuery<CarteBancaire> queryCartes = entityManager.createQuery(
                    "SELECT cb FROM CarteBancaire cb WHERE cb.compte.id IN :compteIds ORDER BY cb.id DESC",
                    CarteBancaire.class);
            queryCartes.setParameter("compteIds", compteIds);
            List<CarteBancaire> cartes = queryCartes.getResultList();

            return cartes.stream()
                    .map(CarteBancaireMapper::toResponseDTO)
                    .collect(Collectors.toList());
        } finally {
            entityManager.close();
        }
    }
}

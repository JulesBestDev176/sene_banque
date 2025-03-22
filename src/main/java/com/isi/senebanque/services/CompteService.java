package com.isi.senebanque.services;

import com.isi.senebanque.dtos.requests.compte.CompteRequestDTO;
import com.isi.senebanque.dtos.responses.compte.CompteResponseDTO;
import com.isi.senebanque.mappers.CompteMapper;
import com.isi.senebanque.models.Client;
import com.isi.senebanque.models.Compte;
import com.isi.senebanque.utils.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class CompteService {
    private static final double FRAIS_BANCAIRE_EPARGNE = 5000.0;
    private static final double FRAIS_BANCAIRE_COURANT = 10000.0;
    public static final String STATUT_ACTIF = "ACTIF";
    public static final String STATUT_INACTIF = "INACTIF";
    public static final String TYPE_COMPTE_COURANT = "COURANT";
    public static final String TYPE_COMPTE_EPARGNE = "EPARGNE";
    public long getNombreComptesActifs() {
        EntityManager entityManager = JpaUtil.getEntityManager();
        try {
            TypedQuery<Long> query = entityManager.createQuery(
                    "SELECT COUNT(c) FROM Compte c WHERE c.statut = :statut", Long.class);
            query.setParameter("statut", "actif");
            return query.getSingleResult();
        } finally {
            entityManager.close();
        }
    }

    public List<CompteResponseDTO> getAllComptes() {
        EntityManager entityManager = JpaUtil.getEntityManager();
        try {
            TypedQuery<Compte> query = entityManager.createQuery("SELECT c FROM Compte c ORDER BY c.id DESC", Compte.class);
            List<Compte> comptes = query.getResultList();

            return comptes.stream()
                    .map(CompteMapper::toResponseDTO)
                    .collect(Collectors.toList());
        } finally {
            entityManager.close();
        }
    }

    public List<CompteResponseDTO> getComptesByClient(Long clientId) {
        EntityManager entityManager = JpaUtil.getEntityManager();
        try {
            TypedQuery<Compte> query = entityManager.createQuery(
                    "SELECT c FROM Compte c WHERE c.client.id = :clientId ORDER BY c.id DESC", Compte.class);
            query.setParameter("clientId", clientId);
            List<Compte> comptes = query.getResultList();

            return comptes.stream()
                    .map(CompteMapper::toResponseDTO)
                    .collect(Collectors.toList());
        } finally {
            entityManager.close();
        }
    }

    public boolean crediterCompte(Long compteId, double montant) {
        EntityManager entityManager = JpaUtil.getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();

            Compte compte = entityManager.find(Compte.class, compteId);
            if (compte == null) {
                return false;
            }


            compte.setSolde(compte.getSolde() + montant);

            entityManager.merge(compte);
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

    public CompteResponseDTO getCompteCourantPrincipal(Long clientId) {
        EntityManager entityManager = JpaUtil.getEntityManager();
        try {
            TypedQuery<Compte> query = entityManager.createQuery(
                    "SELECT c FROM Compte c WHERE c.client.id = :clientId AND c.type_compte = :typeCompte AND c.statut = :statut ORDER BY c.id",
                    Compte.class);
            query.setParameter("clientId", clientId);
            query.setParameter("typeCompte", TYPE_COMPTE_COURANT);
            query.setParameter("statut", STATUT_ACTIF);
            query.setMaxResults(1);

            List<Compte> comptes = query.getResultList();
            if (comptes.isEmpty()) {
                return null;
            }

            return CompteMapper.toResponseDTO(comptes.get(0));
        } finally {
            entityManager.close();
        }
    }

    public CompteResponseDTO getCompteById(Long id) {
        EntityManager entityManager = JpaUtil.getEntityManager();
        try {
            Compte compte = entityManager.find(Compte.class, id);
            if (compte == null) {
                return null;
            }
            return CompteMapper.toResponseDTO(compte);
        } finally {
            entityManager.close();
        }
    }

    public CompteResponseDTO ajouterCompte(CompteRequestDTO compteRequestDTO) {

        double fraisBancaire = "EPARGNE".equalsIgnoreCase(compteRequestDTO.getTypeCompte())
                ? FRAIS_BANCAIRE_EPARGNE : FRAIS_BANCAIRE_COURANT;

        if (compteRequestDTO.getSolde() <= fraisBancaire) {
            System.out.println("Solde insuffisant pour couvrir les frais bancaires");
            return null;
        }


        if (!verifierContraintesCompteClient(compteRequestDTO.getClientId(), compteRequestDTO.getTypeCompte())) {
            System.out.println("Le client a déjà atteint la limite de comptes ou possède déjà un compte de ce type");
            return null;
        }

        EntityManager entityManager = JpaUtil.getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();


            Client client = entityManager.find(Client.class, compteRequestDTO.getClientId());
            if (client == null) {
                System.out.println("Client non trouvé");
                return null;
            }


            String numeroCompte = genererNumeroCompte();


            Compte compte = CompteMapper.toEntity(compteRequestDTO);
            compte.setClient(client);
            compte.setNumero(numeroCompte);
            compte.setFrais_bancaire(fraisBancaire);
            compte.setDate_creation(new Date());
            compte.setStatut("ACTIF");


            double nouveauSolde = compteRequestDTO.getSolde() - fraisBancaire;
            compte.setSolde(nouveauSolde);

            entityManager.persist(compte);
            transaction.commit();

            return CompteMapper.toResponseDTO(compte);
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

    public CompteResponseDTO modifierCompte(Long id, CompteRequestDTO compteRequestDTO) {
        EntityManager entityManager = JpaUtil.getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();

            Compte compte = entityManager.find(Compte.class, id);
            if (compte == null) {
                return null;
            }

            compte.setSolde(compteRequestDTO.getSolde());
            compte.setStatut(compteRequestDTO.getStatut());

            entityManager.merge(compte);
            transaction.commit();

            return CompteMapper.toResponseDTO(compte);
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

    public boolean changerStatutCompte(Long id, String nouveauStatut) {
        if (!nouveauStatut.equals("ACTIF") && !nouveauStatut.equals("INACTIF") && !nouveauStatut.equals("BLOQUE")) {
            return false;
        }

        EntityManager entityManager = JpaUtil.getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();

            Compte compte = entityManager.find(Compte.class, id);
            if (compte == null) {
                return false;
            }

            compte.setStatut(nouveauStatut);
            entityManager.merge(compte);
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

    public List<CompteResponseDTO> rechercherComptes(String terme, String statut, String typeCompte) {
        EntityManager entityManager = JpaUtil.getEntityManager();
        try {
            String jpql = "SELECT c FROM Compte c WHERE 1=1";

            if (statut != null && !statut.equals("Tous")) {
                jpql += " AND c.statut = :statut";
            }

            if (typeCompte != null && !typeCompte.equals("Tous")) {
                jpql += " AND c.type_compte = :typeCompte";
            }

            if (terme != null && !terme.isEmpty()) {
                jpql += " AND (LOWER(c.numero) LIKE :terme OR CAST(c.id AS string) LIKE :terme)";
            }

            jpql += " ORDER BY c.id DESC";

            TypedQuery<Compte> query = entityManager.createQuery(jpql, Compte.class);

            if (statut != null && !statut.equals("Tous")) {
                query.setParameter("statut", statut);
            }

            if (typeCompte != null && !typeCompte.equals("Tous")) {
                query.setParameter("typeCompte", typeCompte);
            }

            if (terme != null && !terme.isEmpty()) {
                query.setParameter("terme", "%" + terme.toLowerCase() + "%");
            }

            List<Compte> comptes = query.getResultList();

            return comptes.stream()
                    .map(CompteMapper::toResponseDTO)
                    .collect(Collectors.toList());
        } finally {
            entityManager.close();
        }
    }

    private boolean verifierContraintesCompteClient(Long clientId, String typeCompte) {
        EntityManager entityManager = JpaUtil.getEntityManager();
        try {

            TypedQuery<Long> queryTotal = entityManager.createQuery(
                    "SELECT COUNT(c) FROM Compte c WHERE c.client.id = :clientId", Long.class);
            queryTotal.setParameter("clientId", clientId);
            long nombreComptes = queryTotal.getSingleResult();

            if (nombreComptes >= 2) {
                return false;
            }


            TypedQuery<Long> queryType = entityManager.createQuery(
                    "SELECT COUNT(c) FROM Compte c WHERE c.client.id = :clientId AND LOWER(c.type_compte) = LOWER(:typeCompte)", Long.class);
            queryType.setParameter("clientId", clientId);
            queryType.setParameter("typeCompte", typeCompte);
            long nombreComptesType = queryType.getSingleResult();

            return nombreComptesType == 0;
        } finally {
            entityManager.close();
        }
    }
    public boolean ajouterMontantAuCompte(Long clientId, double montant) {
        EntityManager entityManager = JpaUtil.getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();


            int updatedRows = entityManager.createQuery(
                            "UPDATE Compte c SET c.solde = c.solde + :montant WHERE c.client.id = :clientId")
                    .setParameter("montant", montant)
                    .setParameter("clientId", clientId)
                    .executeUpdate();

            transaction.commit();

            
            return updatedRows > 0;
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
    private String genererNumeroCompte() {
        
        String prefixe = "SN-";
        String date = String.format("%ty%<tm%<td", new Date());
        Random random = new Random();
        String suffixe = String.format("%05d", random.nextInt(100000));
        return prefixe + date + "-" + suffixe;
    }
}

package com.isi.senebanque.services;

import com.isi.senebanque.dtos.requests.credit.CreditRequestDTO;
import com.isi.senebanque.dtos.responses.compte.CompteResponseDTO;
import com.isi.senebanque.dtos.responses.credit.CreditResponseDTO;
import com.isi.senebanque.mappers.CreditMapper;
import com.isi.senebanque.models.Client;
import com.isi.senebanque.models.Compte;
import com.isi.senebanque.models.Credit;
import com.isi.senebanque.utils.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class CreditService {
    public static final String STATUT_EN_ATTENTE = "EN ATTENTE";
    public static final String STATUT_APPROUVE = "EN COURS";
    public static final String STATUT_REJETE = "REJETE";
    public static final String STATUT_SOLDE = "TERMINE";
    private final CompteService compteService = new CompteService();
    public long getNombreTotalCredits() {
        EntityManager entityManager = JpaUtil.getEntityManager();
        try {
            TypedQuery<Long> query = entityManager.createQuery(
                    "SELECT COUNT(c) FROM Credit c", Long.class);
            return query.getSingleResult();
        } finally {
            entityManager.close();
        }
    }

    public long getNombreCreditsEnAttente() {
        EntityManager entityManager = JpaUtil.getEntityManager();
        try {
            TypedQuery<Long> query = entityManager.createQuery(
                    "SELECT COUNT(c) FROM Credit c WHERE c.statut = :statut", Long.class);
            query.setParameter("statut", "en attente");
            return query.getSingleResult();
        } finally {
            entityManager.close();
        }
    }

    public long getNombreCreditsEnCours() {
        EntityManager entityManager = JpaUtil.getEntityManager();
        try {
            TypedQuery<Long> query = entityManager.createQuery(
                    "SELECT COUNT(c) FROM Credit c WHERE c.statut = :statut", Long.class);
            query.setParameter("statut", "en cours");
            return query.getSingleResult();
        } finally {
            entityManager.close();
        }
    }



    public List<CreditResponseDTO> getCreditsByStatut(String statut) {
        EntityManager entityManager = JpaUtil.getEntityManager();
        try {
            TypedQuery<Credit> query = entityManager.createQuery(
                    "SELECT c FROM Credit c WHERE UPPER(c.statut) = UPPER(:statut) ORDER BY c.date_demande DESC",
                    Credit.class);
            query.setParameter("statut", statut);
            List<Credit> credits = query.getResultList();
            return credits.stream()
                    .map(CreditMapper::toResponseDTO)
                    .collect(Collectors.toList());
        } finally {
            entityManager.close();
        }
    }

    public List<CreditResponseDTO> getCreditsEnAttente() {
        return getCreditsByStatut(STATUT_EN_ATTENTE);
    }

    public CreditResponseDTO getCreditById(Long id) {
        EntityManager entityManager = JpaUtil.getEntityManager();
        try {
            Credit credit = entityManager.find(Credit.class, id);
            if (credit == null) {
                return null;
            }
            return CreditMapper.toResponseDTO(credit);
        } finally {
            entityManager.close();
        }
    }


    public List<CreditResponseDTO> getCreditsByClient(Long clientId) {
        EntityManager entityManager = JpaUtil.getEntityManager();
        try {
            TypedQuery<Credit> query = entityManager.createQuery(
                    "SELECT c FROM Credit c WHERE c.client.id = :clientId ORDER BY c.date_demande DESC",
                    Credit.class);
            query.setParameter("clientId", clientId);
            List<Credit> credits = query.getResultList();
            return credits.stream()
                    .map(CreditMapper::toResponseDTO)
                    .collect(Collectors.toList());
        } finally {
            entityManager.close();
        }
    }

    public List<CreditResponseDTO> getAllCredits() {
        EntityManager entityManager = JpaUtil.getEntityManager();
        try {
            TypedQuery<Credit> query = entityManager.createQuery(
                    "SELECT c FROM Credit c ORDER BY c.date_demande DESC",
                    Credit.class);
            List<Credit> credits = query.getResultList();
            return credits.stream()
                    .map(CreditMapper::toResponseDTO)
                    .collect(Collectors.toList());
        } finally {
            entityManager.close();
        }
    }

    public CreditResponseDTO ajouterCredit(CreditRequestDTO creditRequestDTO) {
        EntityManager entityManager = JpaUtil.getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();


            Client client = entityManager.find(Client.class, creditRequestDTO.getClientId());
            if (client == null) {
                return null;
            }


            Credit credit = CreditMapper.toEntity(creditRequestDTO);
            credit.setClient(client);


            if (credit.getStatut() == null || credit.getStatut().isEmpty()) {
                credit.setStatut(STATUT_EN_ATTENTE);
            }


            if (credit.getDate_demande() == null) {
                credit.setDate_demande(new Date());
            }

            entityManager.persist(credit);
            transaction.commit();

            return CreditMapper.toResponseDTO(credit);
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

    public boolean changerStatutCredit(Long id, String nouveauStatut) {
        if (!nouveauStatut.equals(STATUT_EN_ATTENTE) &&
                !nouveauStatut.equals(STATUT_APPROUVE) &&
                !nouveauStatut.equals(STATUT_REJETE) &&
                !nouveauStatut.equals(STATUT_SOLDE)) {
            return false;
        }

        EntityManager entityManager = JpaUtil.getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();

            Credit credit = entityManager.find(Credit.class, id);
            if (credit == null) {
                return false;
            }

            credit.setStatut(nouveauStatut);
            entityManager.merge(credit);
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

    public boolean approuverCredit(Long id) {
        EntityManager entityManager = JpaUtil.getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();

            Credit credit = entityManager.find(Credit.class, id);
            if (credit == null) {
                return false;
            }


            Client client = credit.getClient();
            if (client == null) {
                transaction.rollback();
                return false;
            }


            TypedQuery<Compte> queryCourant = entityManager.createQuery(
                    "SELECT c FROM Compte c WHERE c.client.id = :clientId AND c.type_compte = :typeCompte AND c.statut = :statut ORDER BY c.id",
                    Compte.class);
            queryCourant.setParameter("clientId", client.getId());
            queryCourant.setParameter("typeCompte", CompteService.TYPE_COMPTE_COURANT);
            queryCourant.setParameter("statut", CompteService.STATUT_ACTIF);
            queryCourant.setMaxResults(1);

            List<Compte> comptesCourants = queryCourant.getResultList();


            Compte compteACrediter = null;

            if (!comptesCourants.isEmpty()) {
                compteACrediter = comptesCourants.get(0);
                System.out.println("Compte courant trouvé avec ID: " + compteACrediter.getId());
            } else {

                TypedQuery<Compte> queryEpargne = entityManager.createQuery(
                        "SELECT c FROM Compte c WHERE c.client.id = :clientId AND c.type_compte = :typeCompte AND c.statut = :statut ORDER BY c.id",
                        Compte.class);
                queryEpargne.setParameter("clientId", client.getId());
                queryEpargne.setParameter("typeCompte", CompteService.TYPE_COMPTE_EPARGNE);
                queryEpargne.setParameter("statut", CompteService.STATUT_ACTIF);
                queryEpargne.setMaxResults(1);

                List<Compte> comptesEpargne = queryEpargne.getResultList();

                if (!comptesEpargne.isEmpty()) {
                    compteACrediter = comptesEpargne.get(0);
                    System.out.println("Compte épargne trouvé avec ID: " + compteACrediter.getId());
                }
            }


            if (compteACrediter == null) {
                System.err.println("Aucun compte actif (courant ou épargne) trouvé pour le client ID: " + client.getId());
                transaction.rollback();
                return false;
            }


            compteACrediter.setSolde(compteACrediter.getSolde() + credit.getMontant());
            entityManager.merge(compteACrediter);


            credit.setStatut(STATUT_APPROUVE);
            entityManager.merge(credit);


            transaction.commit();


            String typeCompte = compteACrediter.getType_compte().equals(CompteService.TYPE_COMPTE_COURANT) ? "courant" : "épargne";
            System.out.println("Crédit approuvé avec succès. Le compte " + typeCompte +
                    " du client (ID: " + client.getId() + ") a été crédité de " + credit.getMontant() + " FCFA");

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

    public boolean rejeterCredit(Long id) {
        return changerStatutCredit(id, STATUT_REJETE);
    }

    public List<CreditResponseDTO> rechercherCredits(String terme, String statut) {
        EntityManager entityManager = JpaUtil.getEntityManager();
        try {
            String jpql = "SELECT c FROM Credit c WHERE 1=1";

            if (statut != null && !statut.equals("Tous")) {
                jpql += " AND UPPER(c.statut) = UPPER(:statut)";
            }

            if (terme != null && !terme.isEmpty()) {
                jpql += " AND (CAST(c.id AS string) LIKE :terme OR " +
                        "LOWER(c.client.nom) LIKE :terme OR " +
                        "LOWER(c.client.prenom) LIKE :terme)";
            }

            jpql += " ORDER BY c.date_demande DESC";

            TypedQuery<Credit> query = entityManager.createQuery(jpql, Credit.class);

            if (statut != null && !statut.equals("Tous")) {
                query.setParameter("statut", statut);
            }

            if (terme != null && !terme.isEmpty()) {
                query.setParameter("terme", "%" + terme.toLowerCase() + "%");
            }

            List<Credit> credits = query.getResultList();

            return credits.stream()
                    .map(CreditMapper::toResponseDTO)
                    .collect(Collectors.toList());
        } finally {
            entityManager.close();
        }
    }


}

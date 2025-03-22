package com.isi.senebanque.services;

import com.isi.senebanque.dtos.requests.ticketSupport.TicketSupportRequestDTO;
import com.isi.senebanque.dtos.responses.ticketSupport.TicketSupportResponseDTO;
import com.isi.senebanque.mappers.TicketSupportMapper;
import com.isi.senebanque.models.Admin;
import com.isi.senebanque.models.Client;
import com.isi.senebanque.models.TicketSupport;
import com.isi.senebanque.utils.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class TicketSupportService {
    public static final String STATUT_EN_COURS = "en cours";
    public static final String STATUT_TRAITE = "traité";
    public static final String STATUT_CLOS = "clos";
    public List<TicketSupportResponseDTO> getAllTickets() {
        EntityManager entityManager = JpaUtil.getEntityManager();
        try {
            TypedQuery<TicketSupport> query = entityManager.createQuery(
                    "SELECT t FROM TicketSupport t", TicketSupport.class);
            List<TicketSupport> tickets = query.getResultList();
            return tickets.stream()
                    .map(TicketSupportMapper::toResponseDTO)
                    .collect(Collectors.toList());
        } finally {
            entityManager.close();
        }
    }

    public List<TicketSupportResponseDTO> getTroisDerniersTickets() {
        EntityManager entityManager = JpaUtil.getEntityManager();
        try {
            TypedQuery<TicketSupport> query = entityManager.createQuery(
                    "SELECT t FROM TicketSupport t ORDER BY t.date_ouverture DESC", TicketSupport.class);
            query.setMaxResults(3);
            List<TicketSupport> tickets = query.getResultList();
            return tickets.stream()
                    .map(TicketSupportMapper::toResponseDTO)
                    .collect(Collectors.toList());
        } finally {
            entityManager.close();
        }
    }

    public List<TicketSupportResponseDTO> getTicketsByClient(Long clientId) {
        EntityManager entityManager = JpaUtil.getEntityManager();
        try {
            TypedQuery<TicketSupport> query = entityManager.createQuery(
                    "SELECT t FROM TicketSupport t WHERE t.client.id = :clientId ORDER BY t.date_ouverture DESC",
                    TicketSupport.class);
            query.setParameter("clientId", clientId);
            List<TicketSupport> tickets = query.getResultList();
            return tickets.stream()
                    .map(TicketSupportMapper::toResponseDTO)
                    .collect(Collectors.toList());
        } finally {
            entityManager.close();
        }
    }

    public List<TicketSupportResponseDTO> getTicketsEnCours() {
        EntityManager entityManager = JpaUtil.getEntityManager();
        try {
            TypedQuery<TicketSupport> query = entityManager.createQuery(
                    "SELECT t FROM TicketSupport t WHERE t.statut = :statut ORDER BY t.date_ouverture DESC",
                    TicketSupport.class);
            query.setParameter("statut", STATUT_EN_COURS);
            List<TicketSupport> tickets = query.getResultList();
            return tickets.stream()
                    .map(TicketSupportMapper::toResponseDTO)
                    .collect(Collectors.toList());
        } finally {
            entityManager.close();
        }
    }

    public TicketSupportResponseDTO getTicketById(Long id) {
        EntityManager entityManager = JpaUtil.getEntityManager();
        try {
            TicketSupport ticket = entityManager.find(TicketSupport.class, id);
            if (ticket == null) {
                return null;
            }
            return TicketSupportMapper.toResponseDTO(ticket);
        } finally {
            entityManager.close();
        }
    }


    public TicketSupportResponseDTO ajouterTicket(TicketSupportRequestDTO ticketRequestDTO) {
        EntityManager entityManager = JpaUtil.getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();


            Client client = entityManager.find(Client.class, ticketRequestDTO.getClientId());
            if (client == null) {
                return null;
            }


            TicketSupport ticket = TicketSupportMapper.toEntity(ticketRequestDTO);
            ticket.setClient(client);


            Admin admin = entityManager.find(Admin.class, 1L);
            if (admin != null) {
                ticket.setUtilisateur(admin);
            }


            if (ticket.getDate_ouverture() == null) {
                ticket.setDate_ouverture(new Date());
            }


            ticket.setStatut(STATUT_EN_COURS);

            entityManager.persist(ticket);
            transaction.commit();

            return TicketSupportMapper.toResponseDTO(ticket);
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

    public boolean changerStatutTicket(Long id, String nouveauStatut) {
        if (!nouveauStatut.equals(STATUT_EN_COURS) &&
                !nouveauStatut.equals(STATUT_TRAITE) &&
                !nouveauStatut.equals(STATUT_CLOS)) {
            return false;
        }

        EntityManager entityManager = JpaUtil.getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();

            TicketSupport ticket = entityManager.find(TicketSupport.class, id);
            if (ticket == null) {
                return false;
            }

            ticket.setStatut(nouveauStatut);
            entityManager.merge(ticket);
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

    public boolean marquerTraite(Long id) {
        return changerStatutTicket(id, STATUT_TRAITE);
    }

    public boolean cloreTicket(Long id) {
        return changerStatutTicket(id, STATUT_CLOS);
    }

    public List<TicketSupportResponseDTO> rechercherTickets(String terme, String statut) {
        EntityManager entityManager = JpaUtil.getEntityManager();
        try {
            String jpql = "SELECT t FROM TicketSupport t WHERE 1=1";

            if (statut != null && !statut.equals("Tous")) {
                jpql += " AND t.statut = :statut";
            }

            if (terme != null && !terme.isEmpty()) {
                jpql += " AND (CAST(t.id AS string) LIKE :terme OR " +
                        "LOWER(t.sujet) LIKE :terme OR " +
                        "LOWER(t.description) LIKE :terme OR " +
                        "LOWER(t.client.nom) LIKE :terme OR " +
                        "LOWER(t.client.prenom) LIKE :terme)";
            }

            jpql += " ORDER BY t.date_ouverture DESC";

            TypedQuery<TicketSupport> query = entityManager.createQuery(jpql, TicketSupport.class);

            if (statut != null && !statut.equals("Tous")) {
                query.setParameter("statut", statut);
            }

            if (terme != null && !terme.isEmpty()) {
                query.setParameter("terme", "%" + terme.toLowerCase() + "%");
            }

            List<TicketSupport> tickets = query.getResultList();

            return tickets.stream()
                    .map(TicketSupportMapper::toResponseDTO)
                    .collect(Collectors.toList());
        } finally {
            entityManager.close();
        }
    }

    public long compterTicketsParStatut(String statut) {
        EntityManager entityManager = JpaUtil.getEntityManager();
        try {
            TypedQuery<Long> query = entityManager.createQuery(
                    "SELECT COUNT(t) FROM TicketSupport t WHERE t.statut = :statut", Long.class);
            query.setParameter("statut", statut);
            return query.getSingleResult();
        } finally {
            entityManager.close();
        }
    }

    public long compterTousTickets() {
        EntityManager entityManager = JpaUtil.getEntityManager();
        try {
            TypedQuery<Long> query = entityManager.createQuery(
                    "SELECT COUNT(t) FROM TicketSupport t", Long.class);
            return query.getSingleResult();
        } finally {
            entityManager.close();
        }
    }
}

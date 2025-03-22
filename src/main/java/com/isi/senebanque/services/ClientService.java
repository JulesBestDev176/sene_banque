package com.isi.senebanque.services;

import com.isi.senebanque.dtos.requests.client.ClientRequestDTO;
import com.isi.senebanque.dtos.responses.client.ClientResponseDTO;
import com.isi.senebanque.models.Client;
import com.isi.senebanque.utils.EnvoyerMailService;
import com.isi.senebanque.utils.JpaUtil;
import com.isi.senebanque.utils.VerifierEmail;
import com.isi.senebanque.utils.VerifierTelephone;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class ClientService {


    public long getNombreClients() {
        EntityManager entityManager = JpaUtil.getEntityManager();
        try {
            TypedQuery<Long> query = entityManager.createQuery(
                    "SELECT COUNT(c) FROM Client c", Long.class);
            return query.getSingleResult();
        } finally {
            entityManager.close();
        }
    }


    public String getClientNomComplet(Long clientId) {
        EntityManager entityManager = JpaUtil.getEntityManager();
        try {
            Client client = entityManager.find(Client.class, clientId);
            if (client == null) {
                return "Client inconnu";
            }
            return client.getNom() + " " + client.getPrenom();
        } catch (Exception e) {
            e.printStackTrace();
            return "Client " + clientId;
        } finally {
            entityManager.close();
        }
    }

    public List<ClientResponseDTO> getAllClients() {
        EntityManager entityManager = JpaUtil.getEntityManager();
        try {
            TypedQuery<Client> query = entityManager.createQuery("SELECT c FROM Client c ORDER BY c.id DESC", Client.class);
            List<Client> clients = query.getResultList();

            return clients.stream()
                    .map(client -> new ClientResponseDTO(
                            client.getId(),
                            client.getNom(),
                            client.getPrenom(),
                            client.getEmail(),
                            client.getTelephone(),
                            client.getUsername(),
                            client.getAdresse(),
                            client.getDate_inscription(),
                            client.getStatut()
                    ))
                    .collect(Collectors.toList());
        } finally {
            entityManager.close();
        }
    }

    public ClientResponseDTO ajouterClient(ClientRequestDTO clientRequest) {
        if (!validerDonneesClient(clientRequest)) {
            return null;
        }

        EntityManager entityManager = JpaUtil.getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            String password = GenererPasswordService.genererPassword();
            String username = GenererUsernameService.genererUsername(clientRequest.getNom(), clientRequest.getPrenom());
            transaction.begin();

            Client client = new Client();
            client.setNom(clientRequest.getNom());
            client.setPrenom(clientRequest.getPrenom());
            client.setEmail(clientRequest.getEmail());
            client.setTelephone(clientRequest.getTelephone());
            client.setUsername(username);
            client.setPassword(password);
            client.setAdresse(clientRequest.getAdresse());
            client.setDate_inscription(new Date());
            client.setStatut("Actif");

            entityManager.persist(client);
            transaction.commit();


            EnvoyerMailService.envoyerMailBienvenue(client.getEmail(), client.getNom(), client.getPrenom(), client.getUsername(), client.getPassword());

            return new ClientResponseDTO(
                    client.getId(),
                    client.getNom(),
                    client.getPrenom(),
                    client.getEmail(),
                    client.getTelephone(),
                    client.getUsername(),
                    client.getAdresse(),
                    client.getDate_inscription(),
                    client.getStatut()
            );
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

    public ClientResponseDTO modifierClient(Long id, ClientRequestDTO clientRequest) {
        if (!validerDonneesClient(clientRequest)) {
            return null;
        }

        EntityManager entityManager = JpaUtil.getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();

            Client client = entityManager.find(Client.class, id);
            if (client == null) {
                return null;
            }

            client.setNom(clientRequest.getNom());
            client.setPrenom(clientRequest.getPrenom());
            client.setEmail(clientRequest.getEmail());
            client.setTelephone(clientRequest.getTelephone());

            client.setAdresse(clientRequest.getAdresse());

            entityManager.merge(client);
            transaction.commit();

            return new ClientResponseDTO(
                    client.getId(),
                    client.getNom(),
                    client.getPrenom(),
                    client.getEmail(),
                    client.getTelephone(),
                    client.getUsername(),
                    client.getAdresse(),
                    client.getDate_inscription(),
                    client.getStatut()
            );
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

    public boolean supprimerClient(Long id) {
        EntityManager entityManager = JpaUtil.getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();

            Client client = entityManager.find(Client.class, id);
            if (client == null) {
                return false;
            }

            entityManager.remove(client);
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

    public boolean changerStatutClient(Long id, String nouveauStatut) {
        if (!nouveauStatut.equals("Actif") && !nouveauStatut.equals("Inactif")) {
            return false;
        }

        EntityManager entityManager = JpaUtil.getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();

            Client client = entityManager.find(Client.class, id);
            if (client == null) {
                return false;
            }

            client.setStatut(nouveauStatut);
            entityManager.merge(client);
            transaction.commit();


            if (nouveauStatut.equals("Inactif")) {
                EnvoyerMailService.envoyerMailDesactivation(client.getEmail(), client.getNom(), client.getPrenom());
            } else {
                EnvoyerMailService.envoyerMailReactivation(client.getEmail(), client.getNom(), client.getPrenom());
            }

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

    public ClientResponseDTO getClientById(Long id) {
        EntityManager entityManager = JpaUtil.getEntityManager();
        try {
            Client client = entityManager.find(Client.class, id);
            if (client == null) {
                return null;
            }

            return new ClientResponseDTO(
                    client.getId(),
                    client.getNom(),
                    client.getPrenom(),
                    client.getEmail(),
                    client.getTelephone(),
                    client.getUsername(),
                    client.getAdresse(),
                    client.getDate_inscription(),
                    client.getStatut()
            );
        } finally {
            entityManager.close();
        }
    }

    public List<ClientResponseDTO> rechercherClients(String terme, String statut) {
        String recherche = terme.toLowerCase();
        List<ClientResponseDTO> tousLesClients = getAllClients();

        return tousLesClients.stream()
                .filter(client ->
                        ("Tous".equals(statut) || client.getStatut().equals(statut)) &&
                                (client.getNom().toLowerCase().contains(recherche) ||
                                        client.getPrenom().toLowerCase().contains(recherche) ||
                                        client.getEmail().toLowerCase().contains(recherche) ||
                                        client.getTelephone().toLowerCase().contains(recherche) ||
                                        client.getUsername().toLowerCase().contains(recherche))
                )
                .collect(Collectors.toList());
    }

    private boolean validerDonneesClient(ClientRequestDTO clientRequest) {

        if (clientRequest.getNom() == null || clientRequest.getNom().trim().isEmpty() ||
                clientRequest.getPrenom() == null || clientRequest.getPrenom().trim().isEmpty() ||
                clientRequest.getEmail() == null || clientRequest.getEmail().trim().isEmpty() ||
                clientRequest.getTelephone() == null || clientRequest.getTelephone().trim().isEmpty()) {
            return false;
        }


        if (!VerifierEmail.estValide(clientRequest.getEmail())) {
            return false;
        }


        if (!VerifierTelephone.estValide(clientRequest.getTelephone())) {
            return false;
        }

        return true;
    }
}

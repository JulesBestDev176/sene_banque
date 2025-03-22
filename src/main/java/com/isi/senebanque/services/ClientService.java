package com.isi.senebanque.services;

import com.isi.senebanque.dtos.requests.client.ClientRequestDTO;
import com.isi.senebanque.dtos.requests.compte.CompteRequestDTO;
import com.isi.senebanque.dtos.responses.client.ClientResponseDTO;
import com.isi.senebanque.dtos.responses.compte.CompteResponseDTO;
import com.isi.senebanque.models.Client;
import com.isi.senebanque.models.Compte;
import com.isi.senebanque.utils.EnvoyerMailService;
import com.isi.senebanque.utils.JpaUtil;
import com.isi.senebanque.utils.VerifierEmail;
import com.isi.senebanque.utils.VerifierTelephone;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.util.Date;
import java.util.List;
import java.util.Random;
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

    public ClientResponseDTO ajouterClientAvecCompte(ClientRequestDTO clientRequest) {
        if (!validerDonneesClient(clientRequest)) {
            return null;
        }

        EntityManager entityManager = JpaUtil.getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            String password = GenererPasswordService.genererPassword();
            String username = GenererUsernameService.genererUsername(clientRequest.getNom(), clientRequest.getPrenom());
            transaction.begin();

            // Création du client
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
            entityManager.flush(); // Pour s'assurer que l'ID du client est généré

            // Création du compte associé
            Compte compte = new Compte();
            compte.setClient(client);
            compte.setType_compte(CompteService.TYPE_COMPTE_COURANT);
            compte.setNumero(genererNumeroCompte());
            compte.setSolde(15000.0 - 1000); // Solde initial moins frais bancaires
            compte.setFrais_bancaire(0);
            compte.setDate_creation(new Date());
            compte.setStatut(CompteService.STATUT_ACTIF);

            entityManager.persist(compte);

            transaction.commit();

            // Envoi d'email de bienvenue
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

    public ClientResponseDTO ajouterClientAvecCompte(ClientRequestDTO clientRequest, String typeCompte, double soldeInitial) {
        if (!validerDonneesClient(clientRequest)) {
            System.out.println("Données client invalides");
            return null;
        }

        // Vérifier le type de compte
        if (!typeCompte.equals(CompteService.TYPE_COMPTE_COURANT) && !typeCompte.equals(CompteService.TYPE_COMPTE_EPARGNE)) {
            System.out.println("Type de compte invalide: " + typeCompte);
            return null;
        }

        // Vérifier si le solde est suffisant pour couvrir les frais bancaires
        double fraisBancaire = typeCompte.equals(CompteService.TYPE_COMPTE_EPARGNE)
                ? 0
                : 1000;

        if (soldeInitial <= fraisBancaire) {
            System.out.println("Solde insuffisant pour couvrir les frais bancaires");
            return null;
        }

        EntityManager entityManager = JpaUtil.getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            String password = GenererPasswordService.genererPassword();
            String username = GenererUsernameService.genererUsername(clientRequest.getNom(), clientRequest.getPrenom());

            transaction.begin();

            // Création du client
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
            entityManager.flush(); // Pour s'assurer que l'ID est généré

            // Création du compte associé
            Compte compte = new Compte();
            compte.setClient(client);
            compte.setType_compte(typeCompte);

            // Génération du numéro de compte
            String prefixe = "SN-";
            String date = String.format("%ty%<tm%<td", new Date());
            Random random = new Random();
            String suffixe = String.format("%05d", random.nextInt(100000));
            String numeroCompte = prefixe + date + "-" + suffixe;

            compte.setNumero(numeroCompte);
            compte.setSolde(soldeInitial - fraisBancaire); // Solde initial moins frais bancaires
            compte.setFrais_bancaire(fraisBancaire);
            compte.setDate_creation(new Date());
            compte.setStatut(CompteService.STATUT_ACTIF);

            entityManager.persist(compte);

            transaction.commit();

            // Envoi d'email de bienvenue
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

    // Réutiliser la méthode de génération de numéro de compte du CompteService
    private String genererNumeroCompte() {
        String prefixe = "SN-";
        String date = String.format("%ty%<tm%<td", new Date());
        Random random = new Random();
        String suffixe = String.format("%05d", random.nextInt(100000));
        return prefixe + date + "-" + suffixe;
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

            // Créer un compte courant par défaut pour le nouveau client
            CompteService compteService = new CompteService();
            CompteRequestDTO compteRequestDTO = new CompteRequestDTO();
            compteRequestDTO.setTypeCompte(CompteService.TYPE_COMPTE_COURANT);
            compteRequestDTO.setSolde(15000.0); // Solde initial avec un montant supérieur aux frais bancaires
            compteRequestDTO.setClientId(client.getId());
            compteRequestDTO.setStatut(CompteService.STATUT_ACTIF);

            CompteResponseDTO compteResponseDTO = compteService.ajouterCompte(compteRequestDTO);
            if (compteResponseDTO == null) {
                System.out.println("Erreur lors de la création du compte pour le client " + client.getId());
            }

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

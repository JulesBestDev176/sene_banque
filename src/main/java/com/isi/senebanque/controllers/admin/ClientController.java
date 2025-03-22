package com.isi.senebanque.controllers.admin;

import com.isi.senebanque.dtos.requests.client.ClientRequestDTO;
import com.isi.senebanque.dtos.responses.client.ClientResponseDTO;
import com.isi.senebanque.services.ClientService;
import com.isi.senebanque.services.CompteService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javafx.scene.control.Alert.AlertType;
import javafx.application.Platform;

public class ClientController implements Initializable {

    @FXML
    private TableView<ClientResponseDTO> tableClients;
    @FXML
    private TableColumn<ClientResponseDTO, Long> colId;
    @FXML
    private TableColumn<ClientResponseDTO, String> colNom;
    @FXML
    private TableColumn<ClientResponseDTO, String> colPrenom;
    @FXML
    private TableColumn<ClientResponseDTO, String> colEmail;
    @FXML
    private TableColumn<ClientResponseDTO, String> colTelephone;
    @FXML
    private TableColumn<ClientResponseDTO, String> colUsername;
    @FXML
    private TableColumn<ClientResponseDTO, String> colAdresse;
    @FXML
    private TableColumn<ClientResponseDTO, Date> colDateInscription;
    @FXML
    private TableColumn<ClientResponseDTO, String> colStatut;
    @FXML
    private Pagination paginationClients;
    @FXML
    private TextField rechercheField;
    @FXML
    private ComboBox<String> filtreStatut;
    @FXML
    private Button btnRafraichir;
    @FXML
    private Label infoPage;
    @FXML
    private ComboBox<String> typeCompteField;
    @FXML
    private TextField soldeInitialField;
    @FXML
    private Label labelFraisBancaire;

    @FXML
    private TextField nomField;
    @FXML
    private TextField prenomField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField telephoneField;
    @FXML
    private TextArea adresseField;
    @FXML
    private Button btnEnregistrer;
    @FXML
    private Button btnReinitialiser;

    private final int ITEMS_PAR_PAGE = 10;
    private ObservableList<ClientResponseDTO> listeClients = FXCollections.observableArrayList();
    private ObservableList<ClientResponseDTO> listeClientsFiltree = FXCollections.observableArrayList();

    private final ClientService clientService = new ClientService();

    private boolean modeEdition = false;
    private Long clientIdEnEdition = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("Initialisation du contrôleur ClientController");

        filtreStatut.setItems(FXCollections.observableArrayList("Tous", "Actif", "Inactif"));
        filtreStatut.setValue("Tous");

        typeCompteField.getItems().addAll(CompteService.TYPE_COMPTE_COURANT, CompteService.TYPE_COMPTE_EPARGNE);
        typeCompteField.setValue(CompteService.TYPE_COMPTE_COURANT);

        updateFraisBancaireLabel(CompteService.TYPE_COMPTE_COURANT);

        typeCompteField.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateFraisBancaireLabel(newVal);
        });

        configurerColonnesTable();

        configurerEcouteurs();

        Platform.runLater(this::chargerListeClients);
    }

    private void configurerColonnesTable() {
        System.out.println("Configuration des colonnes de la table");

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colTelephone.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colAdresse.setCellValueFactory(new PropertyValueFactory<>("adresse"));
        colDateInscription.setCellValueFactory(new PropertyValueFactory<>("date_inscription"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));

        colDateInscription.setCellFactory(column -> new TableCell<ClientResponseDTO, Date>() {
            private final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

            @Override
            protected void updateItem(Date item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(format.format(item));
                }
            }
        });

        colStatut.setCellFactory(column -> new TableCell<ClientResponseDTO, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label badge = new Label(item);
                    badge.getStyleClass().add("badge-statut");
                    badge.getStyleClass().add(item.equalsIgnoreCase("actif") ? "statut-actif" : "statut-inactif");
                    setGraphic(badge);
                }
            }
        });


    }



    private void chargerListeClients() {
        try {
            System.out.println("Chargement de la liste des clients");
            List<ClientResponseDTO> clients = clientService.getAllClients();
            System.out.println("Nombre de clients récupérés: " + clients.size());

            listeClients.clear();
            listeClients.addAll(clients);
            listeClientsFiltree.setAll(listeClients);

            for (ClientResponseDTO client : listeClientsFiltree) {
                System.out.println("Client: " + client.getId() + " - " + client.getNom() + " " + client.getPrenom());
            }

            configurerPagination();

            if (paginationClients.getPageCount() > 0) {
                paginationClients.setCurrentPageIndex(0);
                actualiserTableauParPage(0);
            }

            mettreAJourInfoPagination();
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des clients: " + e.getMessage());
            e.printStackTrace();
            afficherMessage("Erreur", "Impossible de charger la liste des clients: " + e.getMessage(), AlertType.ERROR);
        }
    }

    private void configurerPagination() {
        int totalItems = listeClientsFiltree.size();
        int pageCount = (totalItems + ITEMS_PAR_PAGE - 1) / ITEMS_PAR_PAGE;

        System.out.println("Configuration de la pagination - Total items: " + totalItems + ", Nombre de pages: " + pageCount);

        paginationClients.setPageCount(Math.max(1, pageCount));
        paginationClients.setCurrentPageIndex(0);

        paginationClients.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) -> {
            System.out.println("Changement de page: " + newIndex.intValue());
            actualiserTableauParPage(newIndex.intValue());
            mettreAJourInfoPagination();
        });
    }

    private void updateFraisBancaireLabel(String typeCompte) {
        double frais = typeCompte.equals(CompteService.TYPE_COMPTE_EPARGNE)
                ? 0
                : 1000;

        labelFraisBancaire.setText("Frais bancaires: " + frais + " FCFA");
    }

    private void actualiserTableauParPage(int pageIndex) {
        int fromIndex = pageIndex * ITEMS_PAR_PAGE;
        int toIndex = Math.min(fromIndex + ITEMS_PAR_PAGE, listeClientsFiltree.size());

        System.out.println("Actualisation du tableau pour la page " + pageIndex +
                " (index " + fromIndex + " à " + toIndex + " sur " + listeClientsFiltree.size() + " clients)");

        if (listeClientsFiltree.isEmpty() || fromIndex >= listeClientsFiltree.size()) {
            System.out.println("Aucun élément à afficher - liste vide ou index invalide");
            tableClients.setItems(FXCollections.observableArrayList());
        } else {
            ObservableList<ClientResponseDTO> pageItems = FXCollections.observableArrayList(
                    listeClientsFiltree.subList(fromIndex, toIndex)
            );
            System.out.println("Affichage de " + pageItems.size() + " clients sur cette page");
            tableClients.setItems(pageItems);

            for (ClientResponseDTO client : pageItems) {
                System.out.println("Affichage client: " + client.getId() + " - " + client.getNom());
            }
        }
    }

    private void mettreAJourInfoPagination() {
        int pageActuelle = paginationClients.getCurrentPageIndex();
        int debut = listeClientsFiltree.isEmpty() ? 0 : pageActuelle * ITEMS_PAR_PAGE + 1;
        int fin = Math.min((pageActuelle + 1) * ITEMS_PAR_PAGE, listeClientsFiltree.size());

        if (listeClientsFiltree.isEmpty()) {
            infoPage.setText("Aucun client trouvé");
        } else {
            infoPage.setText(String.format("Affichage %d-%d sur %d", debut, fin, listeClientsFiltree.size()));
        }

        System.out.println("Info pagination mise à jour: " + infoPage.getText());
    }

    private void configurerEcouteurs() {

        rechercheField.textProperty().addListener((obs, oldVal, newVal) -> {
            filtrerListeClients();
        });


        filtreStatut.valueProperty().addListener((obs, oldVal, newVal) -> {
            filtrerListeClients();
        });


        btnRafraichir.setOnAction(event -> {
            System.out.println("Rafraîchissement de la liste");
            rechercheField.clear();
            filtreStatut.setValue("Tous");
            chargerListeClients();
        });


        btnEnregistrer.setOnAction(event -> {
            enregistrerClient();
        });


        btnReinitialiser.setOnAction(event -> {
            reinitialiserFormulaire();
        });
    }

    private void filtrerListeClients() {
        String recherche = rechercheField.getText().toLowerCase();
        String statut = filtreStatut.getValue();

        System.out.println("Filtrage des clients - Recherche: '" + recherche + "', Statut: " + statut);

        List<ClientResponseDTO> clientsFiltres = clientService.rechercherClients(recherche, statut);
        listeClientsFiltree.setAll(clientsFiltres);

        System.out.println("Nombre de clients après filtrage: " + listeClientsFiltree.size());

        configurerPagination();
        paginationClients.setCurrentPageIndex(0);
        actualiserTableauParPage(0);
        mettreAJourInfoPagination();
    }

    private void enregistrerClient() {
        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        String email = emailField.getText().trim();
        String telephone = telephoneField.getText().trim();
        String adresse = adresseField.getText().trim();
        String typeCompte = typeCompteField.getValue();

        // Valider les champs obligatoires du client
        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || telephone.isEmpty()) {
            afficherMessage("Champs manquants", "Veuillez remplir tous les champs obligatoires du client.", AlertType.WARNING);
            return;
        }

        // Validation du solde initial
        double soldeInitial;
        try {
            soldeInitial = Double.parseDouble(soldeInitialField.getText().trim().replace(" ", ""));
        } catch (NumberFormatException e) {
            afficherMessage("Erreur de saisie", "Le solde initial doit être un nombre valide.", AlertType.WARNING);
            return;
        }

        // Vérification du montant minimum
        double fraisBancaire = typeCompte.equals(CompteService.TYPE_COMPTE_EPARGNE)
                ? 0
                : 1000;

        if (soldeInitial <= fraisBancaire) {
            afficherMessage("Solde insuffisant",
                    "Le solde initial doit être supérieur à " + fraisBancaire + " FCFA pour couvrir les frais bancaires.",
                    AlertType.WARNING);
            return;
        }

        ClientRequestDTO clientRequest = new ClientRequestDTO();
        clientRequest.setNom(nom);
        clientRequest.setPrenom(prenom);
        clientRequest.setEmail(email);
        clientRequest.setTelephone(telephone);
        clientRequest.setAdresse(adresse);

        ClientResponseDTO resultat;

        try {
            if (modeEdition && clientIdEnEdition != null) {
                // Logique de modification existante
                System.out.println("Modification du client ID: " + clientIdEnEdition);
                resultat = clientService.modifierClient(clientIdEnEdition, clientRequest);
                if (resultat != null) {
                    afficherMessage("Client modifié", "Le client a été modifié avec succès.", AlertType.INFORMATION);
                } else {
                    afficherMessage("Erreur", "Erreur lors de la modification du client. Vérifiez les données saisies.", AlertType.ERROR);
                    return;
                }
            } else {
                // Ajout d'un nouveau client avec compte
                System.out.println("Ajout d'un nouveau client avec compte " + typeCompte);

                resultat = clientService.ajouterClientAvecCompte(clientRequest, typeCompte, soldeInitial);

                if (resultat != null) {
                    afficherMessage("Client et compte créés",
                            "Le client a été ajouté avec succès et un compte " + typeCompte + " a été créé automatiquement.",
                            AlertType.INFORMATION);
                } else {
                    afficherMessage("Erreur", "Erreur lors de l'ajout du client ou de la création du compte. Vérifiez les données saisies.", AlertType.ERROR);
                    return;
                }
            }

            reinitialiserFormulaire();
            chargerListeClients();
        } catch (Exception e) {
            System.err.println("Erreur lors de l'enregistrement du client: " + e.getMessage());
            e.printStackTrace();
            afficherMessage("Erreur", "Une erreur est survenue: " + e.getMessage(), AlertType.ERROR);
        }
    }



    private void editerClient(ClientResponseDTO client) {
        modeEdition = true;
        clientIdEnEdition = client.getId();

        System.out.println("Édition du client ID: " + client.getId() + " - " + client.getNom() + " " + client.getPrenom());

        nomField.setText(client.getNom());
        prenomField.setText(client.getPrenom());
        emailField.setText(client.getEmail());
        telephoneField.setText(client.getTelephone());
        adresseField.setText(client.getAdresse());

        btnEnregistrer.setText("Mettre à jour");

        nomField.requestFocus();
    }

    private void supprimerClient(ClientResponseDTO client) {
        System.out.println("Suppression du client ID: " + client.getId() + " - " + client.getNom() + " " + client.getPrenom());

        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer le client");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer le client " + client.getNom() + " " + client.getPrenom() + " ?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    boolean resultat = clientService.supprimerClient(client.getId());
                    if (resultat) {
                        afficherMessage("Suppression réussie", "Le client a été supprimé avec succès.", AlertType.INFORMATION);
                        chargerListeClients();
                    } else {
                        afficherMessage("Erreur", "Erreur lors de la suppression du client.", AlertType.ERROR);
                    }
                } catch (Exception e) {
                    System.err.println("Erreur lors de la suppression du client: " + e.getMessage());
                    e.printStackTrace();
                    afficherMessage("Erreur", "Une erreur est survenue: " + e.getMessage(), AlertType.ERROR);
                }
            }
        });
    }

    private void changerStatutClient(ClientResponseDTO client) {
        String nouveauStatut = client.getStatut().equals("Actif") ? "Inactif" : "Actif";
        String message = client.getStatut().equals("Actif") ?
                "Êtes-vous sûr de vouloir désactiver ce client ?" :
                "Êtes-vous sûr de vouloir réactiver ce client ?";

        System.out.println("Changement du statut du client ID: " + client.getId() + " vers: " + nouveauStatut);

        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de changement de statut");
        alert.setHeaderText("Changer le statut du client");
        alert.setContentText(message);

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    boolean resultat = clientService.changerStatutClient(client.getId(), nouveauStatut);
                    if (resultat) {
                        afficherMessage("Statut modifié", "Le statut du client a été modifié avec succès.", AlertType.INFORMATION);
                        chargerListeClients();
                    } else {
                        afficherMessage("Erreur", "Erreur lors de la modification du statut du client.", AlertType.ERROR);
                    }
                } catch (Exception e) {
                    System.err.println("Erreur lors du changement de statut: " + e.getMessage());
                    e.printStackTrace();
                    afficherMessage("Erreur", "Une erreur est survenue: " + e.getMessage(), AlertType.ERROR);
                }
            }
        });
    }



    private void reinitialiserFormulaire() {
        System.out.println("Réinitialisation du formulaire");
        typeCompteField.setValue(CompteService.TYPE_COMPTE_COURANT);
        soldeInitialField.clear();
        updateFraisBancaireLabel(CompteService.TYPE_COMPTE_COURANT);

        nomField.clear();
        prenomField.clear();
        emailField.clear();
        telephoneField.clear();
        adresseField.clear();

        modeEdition = false;
        clientIdEnEdition = null;

        btnEnregistrer.setText("Enregistrer");
    }

    private void afficherMessage(String titre, String message, AlertType type) {
        System.out.println(type + ": " + titre + " - " + message);

        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

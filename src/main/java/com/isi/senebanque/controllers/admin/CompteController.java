package com.isi.senebanque.controllers.admin;

import com.isi.senebanque.dtos.requests.compte.CompteRequestDTO;
import com.isi.senebanque.dtos.responses.client.ClientResponseDTO;
import com.isi.senebanque.dtos.responses.compte.CompteResponseDTO;
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
import javafx.util.StringConverter;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;

import java.net.URL;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.scene.control.Alert.AlertType;
import javafx.application.Platform;

public class CompteController implements Initializable {

    @FXML
    private TableView<CompteResponseDTO> tableComptes;
    @FXML
    private TableColumn<CompteResponseDTO, Long> colId;
    @FXML
    private TableColumn<CompteResponseDTO, String> colNumero;
    @FXML
    private TableColumn<CompteResponseDTO, String> colTypeCompte;
    @FXML
    private TableColumn<CompteResponseDTO, Double> colSolde;
    @FXML
    private TableColumn<CompteResponseDTO, Double> colFraisBancaire;
    @FXML
    private TableColumn<CompteResponseDTO, Date> colDateCreation;
    @FXML
    private TableColumn<CompteResponseDTO, Long> colClientId;
    @FXML
    private TableColumn<CompteResponseDTO, String> colStatut;
    @FXML
    private TableColumn<CompteResponseDTO, Void> colActions;
    @FXML
    private Pagination paginationComptes;
    @FXML
    private TextField rechercheField;
    @FXML
    private ComboBox<String> filtreStatut;
    @FXML
    private ComboBox<String> filtreTypeCompte;
    @FXML
    private Button btnRafraichir;
    @FXML
    private Label infoPage;

    @FXML
    private ComboBox<String> typeCompteField;
    @FXML
    private TextField soldeField;
    @FXML
    private Label labelFraisBancaire;
    @FXML
    private ComboBox<ClientResponseDTO> clientField;
    @FXML
    private Button btnEnregistrer;
    @FXML
    private Button btnReinitialiser;

    private final int ITEMS_PAR_PAGE = 10;
    private ObservableList<CompteResponseDTO> listeComptes = FXCollections.observableArrayList();
    private ObservableList<CompteResponseDTO> listeComptesFiltree = FXCollections.observableArrayList();


    private final CompteService compteService = new CompteService();
    private final ClientService clientService = new ClientService();


    private static final double FRAIS_BANCAIRE_EPARGNE = 5000.0;
    private static final double FRAIS_BANCAIRE_COURANT = 10000.0;


    private final NumberFormat formatMonnaie = NumberFormat.getNumberInstance(Locale.FRANCE);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("Initialisation du contrôleur CompteController");


        formatMonnaie.setMaximumFractionDigits(2);
        formatMonnaie.setMinimumFractionDigits(0);


        filtreStatut.setItems(FXCollections.observableArrayList("Tous", "ACTIF", "INACTIF", "BLOQUE"));
        filtreStatut.setValue("Tous");

        filtreTypeCompte.setItems(FXCollections.observableArrayList("Tous", "EPARGNE", "COURANT"));
        filtreTypeCompte.setValue("Tous");


        typeCompteField.setItems(FXCollections.observableArrayList("EPARGNE", "COURANT"));
        typeCompteField.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                if (newVal.equals("EPARGNE")) {
                    labelFraisBancaire.setText("Frais bancaires: 5 000 FCFA");
                } else {
                    labelFraisBancaire.setText("Frais bancaires: 10 000 FCFA");
                }
            }
        });


        chargerClientsComboBox();


        configurerColonnesTable();


        configurerEcouteurs();


        Platform.runLater(this::chargerListeComptes);
    }

    private void chargerClientsComboBox() {
        try {
            List<ClientResponseDTO> clients = clientService.getAllClients();
            ObservableList<ClientResponseDTO> clientsObservable = FXCollections.observableArrayList(clients);
            clientField.setItems(clientsObservable);

            // Configurer l'affichage des clients dans le ComboBox
            clientField.setConverter(new StringConverter<ClientResponseDTO>() {
                @Override
                public String toString(ClientResponseDTO client) {
                    if (client == null) return "";
                    return client.getId() + " - " + client.getNom() + " " + client.getPrenom();
                }

                @Override
                public ClientResponseDTO fromString(String string) {
                    return null; // Pas nécessaire pour un ComboBox en lecture seule
                }
            });
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des clients: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void configurerColonnesTable() {
        System.out.println("Configuration des colonnes de la table");

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNumero.setCellValueFactory(new PropertyValueFactory<>("numero"));
        colTypeCompte.setCellValueFactory(new PropertyValueFactory<>("typeCompte"));
        colSolde.setCellValueFactory(new PropertyValueFactory<>("solde"));
        colFraisBancaire.setCellValueFactory(new PropertyValueFactory<>("fraisBancaire"));
        colDateCreation.setCellValueFactory(new PropertyValueFactory<>("dateCreation"));
        colClientId.setCellValueFactory(new PropertyValueFactory<>("clientId"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));

        // Formater la date
        colDateCreation.setCellFactory(column -> new TableCell<CompteResponseDTO, Date>() {
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

        // Formater le solde et les frais
        colSolde.setCellFactory(column -> new TableCell<CompteResponseDTO, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(formatMonnaie.format(item) + " FCFA");
                }
            }
        });

        colFraisBancaire.setCellFactory(column -> new TableCell<CompteResponseDTO, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(formatMonnaie.format(item) + " FCFA");
                }
            }
        });

        // Formater le statut avec badge
        colStatut.setCellFactory(column -> new TableCell<CompteResponseDTO, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label badge = new Label(item);
                    badge.getStyleClass().add("badge-statut");

                    if ("ACTIF".equals(item)) {
                        badge.getStyleClass().add("statut-actif");
                    } else if ("INACTIF".equals(item)) {
                        badge.getStyleClass().add("statut-inactif");
                    } else if ("BLOQUE".equals(item)) {
                        badge.getStyleClass().add("statut-bloque");
                    }

                    setGraphic(badge);
                }
            }
        });

        // Configurer la colonne d'actions
        configurerColonneActions();
    }

    private void configurerColonneActions() {
        colActions.setCellFactory(new Callback<TableColumn<CompteResponseDTO, Void>, TableCell<CompteResponseDTO, Void>>() {
            @Override
            public TableCell<CompteResponseDTO, Void> call(TableColumn<CompteResponseDTO, Void> param) {
                return new TableCell<CompteResponseDTO, Void>() {
                    private final Button btnStatut = new Button();
                    private final Button btnBloquer = new Button();

                    {
                        // Bouton de changement de statut
                        FontAwesomeIconView statusIcon = new FontAwesomeIconView();
                        statusIcon.setGlyphName("TOGGLE_ON");
                        statusIcon.setStyle("-fx-fill: #28a745; -fx-font-size: 14px;");
                        btnStatut.setGraphic(statusIcon);
                        btnStatut.setStyle("-fx-background-color: transparent;");
                        btnStatut.setTooltip(new Tooltip("Activer/Désactiver"));

                        // Bouton de blocage
                        FontAwesomeIconView blockIcon = new FontAwesomeIconView();
                        blockIcon.setGlyphName("LOCK");
                        blockIcon.setStyle("-fx-fill: #dc3545; -fx-font-size: 14px;");
                        btnBloquer.setGraphic(blockIcon);
                        btnBloquer.setStyle("-fx-background-color: transparent;");
                        btnBloquer.setTooltip(new Tooltip("Bloquer"));

                        // Actions des boutons
                        btnStatut.setOnAction(event -> {
                            CompteResponseDTO compte = getTableView().getItems().get(getIndex());
                            changerStatutCompte(compte);
                        });

                        btnBloquer.setOnAction(event -> {
                            CompteResponseDTO compte = getTableView().getItems().get(getIndex());
                            bloquerCompte(compte);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            CompteResponseDTO compte = getTableView().getItems().get(getIndex());
                            if ("BLOQUE".equals(compte.getStatut())) {
                                HBox box = new HBox(10, btnStatut);
                                box.setAlignment(Pos.CENTER);
                                setGraphic(box);
                            } else {
                                HBox box = new HBox(10, btnStatut, btnBloquer);
                                box.setAlignment(Pos.CENTER);
                                setGraphic(box);
                            }
                        }
                    }
                };
            }
        });
    }

    private void chargerListeComptes() {
        try {
            System.out.println("Chargement de la liste des comptes");
            List<CompteResponseDTO> comptes = compteService.getAllComptes();
            System.out.println("Nombre de comptes récupérés: " + comptes.size());

            listeComptes.clear();
            listeComptes.addAll(comptes);
            listeComptesFiltree.setAll(listeComptes);

            // Configurer la pagination et afficher les données
            configurerPagination();

            // S'assurer d'afficher la première page
            if (paginationComptes.getPageCount() > 0) {
                paginationComptes.setCurrentPageIndex(0);
                actualiserTableauParPage(0);
            }

            mettreAJourInfoPagination();
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des comptes: " + e.getMessage());
            e.printStackTrace();
            afficherMessage("Erreur", "Impossible de charger la liste des comptes: " + e.getMessage(), AlertType.ERROR);
        }
    }

    private void configurerPagination() {
        int totalItems = listeComptesFiltree.size();
        int pageCount = (totalItems + ITEMS_PAR_PAGE - 1) / ITEMS_PAR_PAGE;  // Arrondi au supérieur

        System.out.println("Configuration de la pagination - Total items: " + totalItems + ", Nombre de pages: " + pageCount);

        paginationComptes.setPageCount(Math.max(1, pageCount));
        paginationComptes.setCurrentPageIndex(0);

        // S'assurer qu'un seul listener est ajouté
        paginationComptes.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) -> {
            System.out.println("Changement de page: " + newIndex.intValue());
            actualiserTableauParPage(newIndex.intValue());
            mettreAJourInfoPagination();
        });
    }

    private void actualiserTableauParPage(int pageIndex) {
        int fromIndex = pageIndex * ITEMS_PAR_PAGE;
        int toIndex = Math.min(fromIndex + ITEMS_PAR_PAGE, listeComptesFiltree.size());

        System.out.println("Actualisation du tableau pour la page " + pageIndex +
                " (index " + fromIndex + " à " + toIndex + " sur " + listeComptesFiltree.size() + " comptes)");

        // Vérifier si les indices sont valides
        if (listeComptesFiltree.isEmpty() || fromIndex >= listeComptesFiltree.size()) {
            System.out.println("Aucun élément à afficher - liste vide ou index invalide");
            tableComptes.setItems(FXCollections.observableArrayList());
        } else {
            ObservableList<CompteResponseDTO> pageItems = FXCollections.observableArrayList(
                    listeComptesFiltree.subList(fromIndex, toIndex)
            );
            System.out.println("Affichage de " + pageItems.size() + " comptes sur cette page");
            tableComptes.setItems(pageItems);
        }
    }

    private void mettreAJourInfoPagination() {
        int pageActuelle = paginationComptes.getCurrentPageIndex();
        int debut = listeComptesFiltree.isEmpty() ? 0 : pageActuelle * ITEMS_PAR_PAGE + 1;
        int fin = Math.min((pageActuelle + 1) * ITEMS_PAR_PAGE, listeComptesFiltree.size());

        if (listeComptesFiltree.isEmpty()) {
            infoPage.setText("Aucun compte trouvé");
        } else {
            infoPage.setText(String.format("Affichage %d-%d sur %d", debut, fin, listeComptesFiltree.size()));
        }
    }

    private void configurerEcouteurs() {
        // Écouteur pour le champ de recherche
        rechercheField.textProperty().addListener((obs, oldVal, newVal) -> {
            filtrerListeComptes();
        });

        // Écouteur pour le filtre de statut
        filtreStatut.valueProperty().addListener((obs, oldVal, newVal) -> {
            filtrerListeComptes();
        });

        // Écouteur pour le filtre de type de compte
        filtreTypeCompte.valueProperty().addListener((obs, oldVal, newVal) -> {
            filtrerListeComptes();
        });

        // Bouton de rafraîchissement
        btnRafraichir.setOnAction(event -> {
            System.out.println("Rafraîchissement de la liste");
            rechercheField.clear();
            filtreStatut.setValue("Tous");
            filtreTypeCompte.setValue("Tous");
            chargerListeComptes();
        });

        // Bouton d'enregistrement
        btnEnregistrer.setOnAction(event -> {
            creerCompte();
        });

        // Bouton de réinitialisation
        btnReinitialiser.setOnAction(event -> {
            reinitialiserFormulaire();
        });
    }

    private void filtrerListeComptes() {
        String recherche = rechercheField.getText();
        String statut = filtreStatut.getValue();
        String typeCompte = filtreTypeCompte.getValue();

        System.out.println("Filtrage des comptes - Recherche: '" + recherche + "', Statut: " + statut + ", Type: " + typeCompte);

        List<CompteResponseDTO> comptesFiltres = compteService.rechercherComptes(recherche, statut, typeCompte);
        listeComptesFiltree.setAll(comptesFiltres);

        System.out.println("Nombre de comptes après filtrage: " + listeComptesFiltree.size());

        // Réinitialiser la pagination
        configurerPagination();
        paginationComptes.setCurrentPageIndex(0);
        actualiserTableauParPage(0);
        mettreAJourInfoPagination();
    }

    private void creerCompte() {
        // Récupérer les données du formulaire
        String typeCompte = typeCompteField.getValue();
        String soldeText = soldeField.getText().trim();
        ClientResponseDTO client = clientField.getValue();

        // Vérification des champs obligatoires
        if (typeCompte == null || soldeText.isEmpty() || client == null) {
            afficherMessage("Champs manquants", "Veuillez remplir tous les champs obligatoires.", AlertType.WARNING);
            return;
        }

        // Convertir et valider le solde
        double solde;
        try {
            solde = Double.parseDouble(soldeText.replace(" ", "").replace(",", "."));
            if (solde <= 0) {
                afficherMessage("Solde invalide", "Le solde doit être un nombre positif.", AlertType.WARNING);
                return;
            }

            // Vérifier si le solde est suffisant pour couvrir les frais bancaires
            double fraisBancaire = "EPARGNE".equals(typeCompte) ? FRAIS_BANCAIRE_EPARGNE : FRAIS_BANCAIRE_COURANT;
            if (solde < fraisBancaire) {
                afficherMessage("Solde insuffisant", "Le solde initial doit être supérieur aux frais bancaires (" + fraisBancaire + " FCFA).", AlertType.WARNING);
                return;
            }

        } catch (NumberFormatException e) {
            afficherMessage("Format invalide", "Le solde doit être un nombre valide.", AlertType.WARNING);
            return;
        }

        // Créer l'objet DTO
        CompteRequestDTO compteRequest = new CompteRequestDTO();
        compteRequest.setTypeCompte(typeCompte);
        compteRequest.setSolde(solde);
        compteRequest.setClientId(client.getId());
        compteRequest.setStatut("ACTIF");

        try {
            // Ajout d'un nouveau compte
            System.out.println("Création d'un nouveau compte");
            CompteResponseDTO resultat = compteService.ajouterCompte(compteRequest);

            if (resultat != null) {
                afficherMessage("Compte créé", "Le compte a été créé avec succès.\nNuméro de compte: " + resultat.getNumero(), AlertType.INFORMATION);
                reinitialiserFormulaire();
                chargerListeComptes();
            } else {
                afficherMessage("Erreur", "Erreur lors de la création du compte.\nVérifiez que le client n'a pas déjà atteint sa limite de comptes ou qu'il ne possède pas déjà un compte du même type.", AlertType.ERROR);
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la création du compte: " + e.getMessage());
            e.printStackTrace();
            afficherMessage("Erreur", "Une erreur est survenue: " + e.getMessage(), AlertType.ERROR);
        }
    }

    private void changerStatutCompte(CompteResponseDTO compte) {
        String nouveauStatut = "ACTIF".equals(compte.getStatut()) ? "INACTIF" : "ACTIF";
        String message = "ACTIF".equals(compte.getStatut()) ?
                "Êtes-vous sûr de vouloir désactiver ce compte ?" :
                "Êtes-vous sûr de vouloir activer ce compte ?";

        System.out.println("Changement du statut du compte ID: " + compte.getId() + " vers: " + nouveauStatut);

        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de changement de statut");
        alert.setHeaderText("Changer le statut du compte");
        alert.setContentText(message);

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    boolean resultat = compteService.changerStatutCompte(compte.getId(), nouveauStatut);
                    if (resultat) {
                        afficherMessage("Statut modifié", "Le statut du compte a été modifié avec succès.", AlertType.INFORMATION);
                        chargerListeComptes();
                    } else {
                        afficherMessage("Erreur", "Erreur lors de la modification du statut du compte.", AlertType.ERROR);
                    }
                } catch (Exception e) {
                    System.err.println("Erreur lors du changement de statut: " + e.getMessage());
                    e.printStackTrace();
                    afficherMessage("Erreur", "Une erreur est survenue: " + e.getMessage(), AlertType.ERROR);
                }
            }
        });
    }

    private void bloquerCompte(CompteResponseDTO compte) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de blocage");
        alert.setHeaderText("Bloquer le compte");
        alert.setContentText("Êtes-vous sûr de vouloir bloquer ce compte ? Cette action est irréversible pour les opérations courantes.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    boolean resultat = compteService.changerStatutCompte(compte.getId(), "BLOQUE");
                    if (resultat) {
                        afficherMessage("Compte bloqué", "Le compte a été bloqué avec succès.", AlertType.INFORMATION);
                        chargerListeComptes();
                    } else {
                        afficherMessage("Erreur", "Erreur lors du blocage du compte.", AlertType.ERROR);
                    }
                } catch (Exception e) {
                    System.err.println("Erreur lors du blocage du compte: " + e.getMessage());
                    e.printStackTrace();
                    afficherMessage("Erreur", "Une erreur est survenue: " + e.getMessage(), AlertType.ERROR);
                }
            }
        });
    }

    private void reinitialiserFormulaire() {
        System.out.println("Réinitialisation du formulaire");

        
        typeCompteField.setValue(null);
        soldeField.clear();
        clientField.setValue(null);
        labelFraisBancaire.setText("Frais bancaires: ");
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

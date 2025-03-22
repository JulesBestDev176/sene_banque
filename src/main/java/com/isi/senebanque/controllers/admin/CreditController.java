package com.isi.senebanque.controllers.admin;

import com.isi.senebanque.dtos.responses.credit.CreditResponseDTO;
import com.isi.senebanque.services.CreditService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class CreditController implements Initializable {

    @FXML
    private TabPane tabPaneCredits;


    @FXML
    private TableView<CreditResponseDTO> tableCreditsEnAttente;
    @FXML
    private TableView<CreditResponseDTO> tableTousCredits;


    @FXML
    private TableColumn<CreditResponseDTO, Long> colIdAttente;
    @FXML
    private TableColumn<CreditResponseDTO, String> colClientAttente;
    @FXML
    private TableColumn<CreditResponseDTO, Double> colMontantAttente;
    @FXML
    private TableColumn<CreditResponseDTO, Double> colTauxAttente;
    @FXML
    private TableColumn<CreditResponseDTO, Date> colDateDemandeAttente;
    @FXML
    private TableColumn<CreditResponseDTO, Double> colMensualiteAttente;
    @FXML
    private TableColumn<CreditResponseDTO, Date> colDateLimiteAttente;
    @FXML
    private TableColumn<CreditResponseDTO, String> colStatutAttente;
    @FXML
    private TableColumn<CreditResponseDTO, Void> colActionsAttente;


    @FXML
    private TableColumn<CreditResponseDTO, Long> colIdTous;
    @FXML
    private TableColumn<CreditResponseDTO, String> colClientTous;
    @FXML
    private TableColumn<CreditResponseDTO, Double> colMontantTous;
    @FXML
    private TableColumn<CreditResponseDTO, Double> colTauxTous;
    @FXML
    private TableColumn<CreditResponseDTO, Date> colDemandeTous;
    @FXML
    private TableColumn<CreditResponseDTO, Double> colMensualiteTous;
    @FXML
    private TableColumn<CreditResponseDTO, Date> colLimiteTous;
    @FXML
    private TableColumn<CreditResponseDTO, String> colStatutTous;


    @FXML
    private TextField champRechercheTous;
    @FXML
    private ComboBox<String> comboStatutTous;

    private final CreditService creditService = new CreditService();
    private ObservableList<CreditResponseDTO> listeCreditsEnAttente = FXCollections.observableArrayList();
    private ObservableList<CreditResponseDTO> listeTousCredits = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("Initialisation du contrôleur CreditController");


        comboStatutTous.setItems(FXCollections.observableArrayList(
                "Tous", "EN ATTENTE", "EN COURS", "REJETE", "TERMINE"));
        comboStatutTous.setValue("Tous");


        configurerColonnesTableEnAttente();
        configurerColonnesTableTous();


        chargerCreditsEnAttente();
        chargerTousCredits();


        configurerEcouteurs();
    }

    private void configurerColonnesTableEnAttente() {

        System.out.println("Configuration des colonnes pour tableau crédits en attente");


        colIdAttente.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleLongProperty(cellData.getValue().getId()).asObject());


        colClientAttente.setCellValueFactory(cellData -> {
            CreditResponseDTO credit = cellData.getValue();
            String nomComplet = (credit.getClientNom() != null ? credit.getClientNom() : "") +
                    " " +
                    (credit.getClientPrenom() != null ? credit.getClientPrenom() : "");
            return javafx.beans.binding.Bindings.createStringBinding(() -> nomComplet);
        });


        colMontantAttente.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getMontant()).asObject());

        colTauxAttente.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getTauxInteret()).asObject());

        colMensualiteAttente.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getMensualite()).asObject());


        colDateDemandeAttente.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getDateDemande()));

        colDateLimiteAttente.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getDateLimite()));


        colStatutAttente.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStatut()));


        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        colDateDemandeAttente.setCellFactory(column -> new TableCell<CreditResponseDTO, Date>() {
            @Override
            protected void updateItem(Date item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(dateFormat.format(item));
                }
            }
        });

        colDateLimiteAttente.setCellFactory(column -> new TableCell<CreditResponseDTO, Date>() {
            @Override
            protected void updateItem(Date item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(dateFormat.format(item));
                }
            }
        });




        colStatutAttente.setCellFactory(column -> new TableCell<CreditResponseDTO, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label badge = new Label(item);
                    badge.getStyleClass().add("badge-statut");

                    if (item.equalsIgnoreCase("EN ATTENTE")) {
                        badge.getStyleClass().add("statut-attente");
                    } else if (item.equalsIgnoreCase("EN COURS")) {
                        badge.getStyleClass().add("statut-encours");
                    } else if (item.equalsIgnoreCase("REJETE")) {
                        badge.getStyleClass().add("statut-rejete");
                    } else if (item.equalsIgnoreCase("TERMINE")) {
                        badge.getStyleClass().add("statut-termine");
                    }

                    setGraphic(badge);
                }
            }
        });


        colActionsAttente.setCellFactory(createActionCellFactory());
    }

    private void configurerColonnesTableTous() {

        System.out.println("Configuration des colonnes pour tableau tous crédits");


        colIdTous.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleLongProperty(cellData.getValue().getId()).asObject());


        colClientTous.setCellValueFactory(cellData -> {
            CreditResponseDTO credit = cellData.getValue();
            String nomComplet = (credit.getClientNom() != null ? credit.getClientNom() : "") +
                    " " +
                    (credit.getClientPrenom() != null ? credit.getClientPrenom() : "");
            return javafx.beans.binding.Bindings.createStringBinding(() -> nomComplet);
        });


        colMontantTous.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getMontant()).asObject());

        colTauxTous.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getTauxInteret()).asObject());

        colMensualiteTous.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getMensualite()).asObject());


        colDemandeTous.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getDateDemande()));

        colLimiteTous.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getDateLimite()));


        colStatutTous.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStatut()));


        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        colDemandeTous.setCellFactory(column -> new TableCell<CreditResponseDTO, Date>() {
            @Override
            protected void updateItem(Date item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(dateFormat.format(item));
                }
            }
        });

        colLimiteTous.setCellFactory(column -> new TableCell<CreditResponseDTO, Date>() {
            @Override
            protected void updateItem(Date item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(dateFormat.format(item));
                }
            }
        });

        
        colMontantTous.setCellFactory(column -> new TableCell<CreditResponseDTO, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%,.2f FCFA", item));
                }
            }
        });

        
        colTauxTous.setCellFactory(column -> new TableCell<CreditResponseDTO, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f%%", item));
                }
            }
        });

        
        colMensualiteTous.setCellFactory(column -> new TableCell<CreditResponseDTO, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%,.2f FCFA", item));
                }
            }
        });

        
        colStatutTous.setCellFactory(column -> new TableCell<CreditResponseDTO, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label badge = new Label(item);
                    badge.getStyleClass().add("badge-statut");

                    if (item.equalsIgnoreCase("EN ATTENTE")) {
                        badge.getStyleClass().add("statut-attente");
                    } else if (item.equalsIgnoreCase("EN COURS")) {
                        badge.getStyleClass().add("statut-encours");
                    } else if (item.equalsIgnoreCase("REJETE")) {
                        badge.getStyleClass().add("statut-rejete");
                    } else if (item.equalsIgnoreCase("TERMINE")) {
                        badge.getStyleClass().add("statut-termine");
                    }

                    setGraphic(badge);
                }
            }
        });
    }

    private Callback<TableColumn<CreditResponseDTO, Void>, TableCell<CreditResponseDTO, Void>> createActionCellFactory() {
        return new Callback<>() {
            @Override
            public TableCell<CreditResponseDTO, Void> call(final TableColumn<CreditResponseDTO, Void> param) {
                return new TableCell<>() {
                    private final Button btnApprouver = new Button("Approuver");
                    private final Button btnRejeter = new Button("Rejeter");
                    private final HBox pane = new HBox(5, btnApprouver, btnRejeter);

                    {
                        btnApprouver.getStyleClass().add("bouton-approuver");
                        btnRejeter.getStyleClass().add("bouton-rejeter");

                        btnApprouver.setOnAction(event -> {
                            CreditResponseDTO credit = getTableView().getItems().get(getIndex());
                            approuverCredit(credit);
                        });

                        btnRejeter.setOnAction(event -> {
                            CreditResponseDTO credit = getTableView().getItems().get(getIndex());
                            rejeterCredit(credit);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(pane);
                        }
                    }
                };
            }
        };
    }

    private void chargerCreditsEnAttente() {
        try {
            System.out.println("Chargement des crédits en attente");
            List<CreditResponseDTO> credits = creditService.getCreditsEnAttente();

            
            System.out.println("Nombre de crédits en attente: " + credits.size());
            for (CreditResponseDTO credit : credits) {
                System.out.println("---------- CRÉDIT ----------");
                System.out.println("ID: " + credit.getId());
                System.out.println("Montant: " + credit.getMontant());
                System.out.println("Taux: " + credit.getTauxInteret());
                System.out.println("Date demande: " + (credit.getDateDemande() != null ? credit.getDateDemande() : "NULL"));
                System.out.println("Mensualité: " + credit.getMensualite());
                System.out.println("Date limite: " + (credit.getDateLimite() != null ? credit.getDateLimite() : "NULL"));
                System.out.println("Client: " + (credit.getClientNom() != null ? credit.getClientNom() : "NULL") +
                        " " + (credit.getClientPrenom() != null ? credit.getClientPrenom() : "NULL"));
                System.out.println("Statut: " + (credit.getStatut() != null ? credit.getStatut() : "NULL"));
                System.out.println("---------------------------");
            }

            listeCreditsEnAttente.setAll(credits);
            tableCreditsEnAttente.setItems(listeCreditsEnAttente);
            System.out.println("Fin du chargement des crédits en attente");
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des crédits en attente: " + e.getMessage());
            e.printStackTrace();
            afficherAlerte("Erreur", "Impossible de charger les crédits en attente", Alert.AlertType.ERROR);
        }
    }

    private void chargerTousCredits() {
        try {
            System.out.println("Chargement de tous les crédits");
            List<CreditResponseDTO> credits = creditService.getAllCredits();

            
            System.out.println("Nombre total de crédits: " + credits.size());
            for (CreditResponseDTO credit : credits) {
                System.out.println("---------- CRÉDIT TOUS ----------");
                System.out.println("ID: " + credit.getId());
                System.out.println("Montant: " + credit.getMontant());
                System.out.println("Taux: " + credit.getTauxInteret());
                System.out.println("Date demande: " + (credit.getDateDemande() != null ? credit.getDateDemande() : "NULL"));
                System.out.println("Mensualité: " + credit.getMensualite());
                System.out.println("Date limite: " + (credit.getDateLimite() != null ? credit.getDateLimite() : "NULL"));
                System.out.println("Client: " + (credit.getClientNom() != null ? credit.getClientNom() : "NULL") +
                        " " + (credit.getClientPrenom() != null ? credit.getClientPrenom() : "NULL"));
                System.out.println("Statut: " + (credit.getStatut() != null ? credit.getStatut() : "NULL"));
                System.out.println("---------------------------");
            }

            listeTousCredits.setAll(credits);
            tableTousCredits.setItems(listeTousCredits);
            System.out.println("Fin du chargement de tous les crédits");
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de tous les crédits: " + e.getMessage());
            e.printStackTrace();
            afficherAlerte("Erreur", "Impossible de charger tous les crédits", Alert.AlertType.ERROR);
        }
    }

    private void configurerEcouteurs() {
        
        champRechercheTous.textProperty().addListener((obs, oldVal, newVal) -> {
            rechercherCredits();
        });

        comboStatutTous.valueProperty().addListener((obs, oldVal, newVal) -> {
            rechercherCredits();
        });
    }

    @FXML
    private void rechercher() {
        rechercherCredits();
    }

    private void rechercherCredits() {
        String terme = champRechercheTous.getText();
        String statut = comboStatutTous.getValue();

        System.out.println("Recherche de crédits - Terme: '" + terme + "', Statut: " + statut);

        try {
            List<CreditResponseDTO> resultats = creditService.rechercherCredits(terme, statut);
            System.out.println("Résultats trouvés: " + resultats.size());
            listeTousCredits.setAll(resultats);
        } catch (Exception e) {
            System.err.println("Erreur lors de la recherche de crédits: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void reinitialiserFiltres() {
        System.out.println("Réinitialisation des filtres");
        champRechercheTous.clear();
        comboStatutTous.setValue("Tous");
        chargerTousCredits();
    }

    private void approuverCredit(CreditResponseDTO credit) {
        System.out.println("Tentative d'approbation du crédit ID: " + credit.getId());
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Approuver ce crédit");
        confirmation.setContentText("Êtes-vous sûr de vouloir approuver ce crédit ? Le montant de " +
                String.format("%,.2f FCFA", credit.getMontant()) +
                " sera crédité sur le compte courant ou d'épargne du client.");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean success = creditService.approuverCredit(credit.getId());
                if (success) {
                    System.out.println("Crédit approuvé avec succès");
                    afficherAlerte("Succès", "Crédit approuvé avec succès. Le compte du client a été crédité.", Alert.AlertType.INFORMATION);
                    chargerCreditsEnAttente();
                    chargerTousCredits();
                } else {
                    System.err.println("Échec de l'approbation du crédit");
                    afficherAlerte("Erreur", "Échec de l'approbation du crédit. Le client ne possède aucun compte actif (courant ou épargne).", Alert.AlertType.ERROR);
                }
            }
        });
    }

    private void rejeterCredit(CreditResponseDTO credit) {
        System.out.println("Tentative de rejet du crédit ID: " + credit.getId());
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Rejeter ce crédit");
        confirmation.setContentText("Êtes-vous sûr de vouloir rejeter ce crédit ?");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean success = creditService.rejeterCredit(credit.getId());
                if (success) {
                    System.out.println("Crédit rejeté avec succès");
                    afficherAlerte("Succès", "Crédit rejeté avec succès", Alert.AlertType.INFORMATION);
                    chargerCreditsEnAttente();
                    chargerTousCredits();
                } else {
                    System.err.println("Échec du rejet du crédit");
                    afficherAlerte("Erreur", "Échec du rejet du crédit", Alert.AlertType.ERROR);
                }
            }
        });
    }

    private void afficherAlerte(String titre, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

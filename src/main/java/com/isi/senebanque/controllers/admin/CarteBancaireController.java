package com.isi.senebanque.controllers.admin;

import com.isi.senebanque.dtos.responses.carteBancaire.CarteBancaireResponseDTO;
import com.isi.senebanque.services.CarteBancaireService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class CarteBancaireController implements Initializable {

    @FXML
    private Label nomUtilisateurHeader;

    @FXML
    private TableView<CarteBancaireResponseDTO> tableCartes;

    @FXML
    private TableColumn<CarteBancaireResponseDTO, Long> colId;

    @FXML
    private TableColumn<CarteBancaireResponseDTO, String> colNumero;

    @FXML
    private TableColumn<CarteBancaireResponseDTO, String> colCompte;

    @FXML
    private TableColumn<CarteBancaireResponseDTO, String> colDateExpiration;

    @FXML
    private TableColumn<CarteBancaireResponseDTO, Double> colSolde;

    @FXML
    private TableColumn<CarteBancaireResponseDTO, String> colStatut;

    @FXML
    private TableColumn<CarteBancaireResponseDTO, Void> colActions;

    @FXML
    private TextField champRecherche;

    @FXML
    private ComboBox<String> comboStatut;

    private final CarteBancaireService carteBancaireService = new CarteBancaireService();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/yy");
    private ObservableList<CarteBancaireResponseDTO> listeCartes = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initialiserTableau();
        initialiserFiltres();
        chargerDonnees();
    }

    private void initialiserTableau() {

        colId.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleLongProperty(cellData.getValue().getId()).asObject());

        colNumero.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getNumero()));

        colCompte.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCompteNumero()));

        colDateExpiration.setCellValueFactory(cellData ->
                new SimpleStringProperty(dateFormat.format(cellData.getValue().getDateExpiration())));

        colSolde.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getSolde()).asObject());

        colStatut.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStatut()));

        colSolde.setCellFactory(column -> new TableCell<CarteBancaireResponseDTO, Double>() {
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


        colStatut.setCellFactory(column -> new TableCell<CarteBancaireResponseDTO, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    getStyleClass().removeAll("statut-actif", "statut-inactif", "statut-bloque");
                } else {
                    setText(item);
                    getStyleClass().removeAll("statut-actif", "statut-inactif", "statut-bloque");

                    if (item.equals("ACTIF")) {
                        getStyleClass().add("statut-actif");
                    } else if (item.equals("INACTIF")) {
                        getStyleClass().add("statut-inactif");
                    } else if (item.equals("BLOQUE")) {
                        getStyleClass().add("statut-bloque");
                    }
                }
            }
        });


        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button btnActiver = new Button("Activer");
            private final Button btnDesactiver = new Button("Désactiver");

            {
                btnActiver.getStyleClass().add("bouton-activer");
                btnDesactiver.getStyleClass().add("bouton-desactiver");

                btnActiver.setOnAction(event -> {
                    CarteBancaireResponseDTO carte = getTableView().getItems().get(getIndex());
                    activerCarte(carte.getId());
                });

                btnDesactiver.setOnAction(event -> {
                    CarteBancaireResponseDTO carte = getTableView().getItems().get(getIndex());
                    desactiverCarte(carte.getId());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    CarteBancaireResponseDTO carte = getTableView().getItems().get(getIndex());

                    if ("ACTIF".equals(carte.getStatut())) {
                        setGraphic(btnDesactiver);
                    } else {
                        setGraphic(btnActiver);
                    }
                }
            }
        });


        tableCartes.setItems(listeCartes);
    }

    private void initialiserFiltres() {

        comboStatut.getItems().addAll("Tous", "ACTIF", "INACTIF", "BLOQUE");
        comboStatut.setValue("Tous");


        comboStatut.valueProperty().addListener((obs, oldValue, newValue) -> filtrerCartes());
        champRecherche.textProperty().addListener((obs, oldValue, newValue) -> filtrerCartes());
    }

    private void chargerDonnees() {
        List<CarteBancaireResponseDTO> cartes = carteBancaireService.getAllCartesBancaires();

        System.out.println("Nombre de cartes chargées: " + cartes.size());
        for (CarteBancaireResponseDTO carte : cartes) {
            System.out.println("Carte ID: " + carte.getId() +
                    ", Numéro: " + carte.getNumero() +
                    ", Compte: " + carte.getCompteNumero() +
                    ", Solde: " + carte.getSolde() +
                    ", Statut: " + carte.getStatut());
        }

        listeCartes.clear();
        listeCartes.addAll(cartes);
        tableCartes.setItems(listeCartes);
    }

    private void filtrerCartes() {
        String terme = champRecherche.getText();
        String statut = comboStatut.getValue();

        List<CarteBancaireResponseDTO> cartesFiltrees = carteBancaireService.rechercherCartesBancaires(terme, statut);
        listeCartes.clear();
        listeCartes.addAll(cartesFiltrees);
    }

    @FXML
    private void rechercher(ActionEvent event) {
        filtrerCartes();
    }

    @FXML
    private void reinitialiserFiltres(ActionEvent event) {
        champRecherche.clear();
        comboStatut.setValue("Tous");
        chargerDonnees();
    }

    private void activerCarte(Long id) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Activer la carte");
        confirmation.setContentText("Êtes-vous sûr de vouloir activer cette carte ?");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = carteBancaireService.activerCarte(id);

            if (success) {
                afficherMessageSucces("Succès", "La carte a été activée avec succès");
                chargerDonnees();
            } else {
                afficherMessageErreur("Erreur", "Impossible d'activer la carte");
            }
        }
    }

    private void desactiverCarte(Long id) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Désactiver la carte");
        confirmation.setContentText("Êtes-vous sûr de vouloir désactiver cette carte ?");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = carteBancaireService.desactiverCarte(id);

            if (success) {
                afficherMessageSucces("Succès", "La carte a été désactivée avec succès");
                chargerDonnees();
            } else {
                afficherMessageErreur("Erreur", "Impossible de désactiver la carte");
            }
        }
    }

    private void afficherMessageSucces(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void afficherMessageErreur(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

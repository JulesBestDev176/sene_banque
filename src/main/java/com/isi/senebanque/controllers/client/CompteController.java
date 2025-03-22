package com.isi.senebanque.controllers.client;

import com.isi.senebanque.dtos.responses.compte.CompteResponseDTO;
import com.isi.senebanque.dtos.responses.transaction.TransactionResponseDTO;
import com.isi.senebanque.models.Model;
import com.isi.senebanque.models.Client;
import com.isi.senebanque.services.CompteService;
import com.isi.senebanque.services.TransactionService;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;

import java.net.URL;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class CompteController implements Initializable {

    @FXML
    private Label lblNomClient;

    @FXML
    private VBox comptesContainer;




    private final CompteService compteService = new CompteService();
    private final TransactionService transactionService = new TransactionService();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private final NumberFormat formatMonnaie = NumberFormat.getCurrencyInstance(new Locale("fr", "SN"));

    private Client clientConnecte;
    private List<CompteResponseDTO> comptes;
    private ObservableList<TransactionResponseDTO> transactionsData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        
        clientConnecte = Model.getInstance().getClientConnecte();

        if (clientConnecte != null) {
            lblNomClient.setText(clientConnecte.getPrenom() + " " + clientConnecte.getNom());

            loadComptes();

            if (!comptes.isEmpty()) {
                updateUI();
            } else {
                showNoAccountsMessage();
            }
        }
    }

    private void loadComptes() {
        comptes = compteService.getComptesByClient(clientConnecte.getId());
    }





    private void loadTransactionsForCompte(Long compteId) {
        List<TransactionResponseDTO> transactions = transactionService.getTransactionsByCompte(compteId);

        transactionsData.clear();
        if (transactions.isEmpty()) {
            showNoTransactionsMessage();
        } else {
            transactionsData.addAll(transactions);
        }
    }

    private void updateUI() {
        comptesContainer.getChildren().clear();

        for (CompteResponseDTO compte : comptes) {
            comptesContainer.getChildren().add(createCompteCard(compte));
        }
    }

    private BorderPane createCompteCard(CompteResponseDTO compte) {
        BorderPane card = new BorderPane();
        card.getStyleClass().add("compte-card");

        
        HBox headerBox = new HBox();
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.setSpacing(10);

        
        FontAwesomeIconView iconCompte = new FontAwesomeIconView();
        if ("EPARGNE".equalsIgnoreCase(compte.getTypeCompte())) {
            iconCompte.setGlyphName("PIGGY_BANK");
        } else {
            iconCompte.setGlyphName("BANK");
        }
        iconCompte.setGlyphStyle("-fx-fill: #007bff; -fx-font-size: 24px;");

        
        VBox typeBox = new VBox(5);
        Label typeLabel = new Label(compte.getTypeCompte());
        typeLabel.getStyleClass().add("compte-type");
        Label numeroLabel = new Label(compte.getNumero());
        numeroLabel.getStyleClass().add("compte-numero");
        typeBox.getChildren().addAll(typeLabel, numeroLabel);

        
        Label statutLabel = new Label(compte.getStatut());
        statutLabel.getStyleClass().add("compte-statut");
        if ("ACTIF".equals(compte.getStatut())) {
            statutLabel.setTextFill(Color.GREEN);
        } else {
            statutLabel.setTextFill(Color.RED);
        }

        headerBox.getChildren().addAll(iconCompte, typeBox);
        HBox.setHgrow(typeBox, Priority.ALWAYS);
        headerBox.getChildren().add(statutLabel);

        
        VBox centerBox = new VBox(10);
        centerBox.setAlignment(Pos.CENTER_LEFT);

        Label soldeLabel = new Label("Solde disponible");
        soldeLabel.getStyleClass().add("solde-label");

        Label montantLabel = new Label(formatMonnaie.format(compte.getSolde()));
        montantLabel.getStyleClass().add("solde-montant");

        centerBox.getChildren().addAll(soldeLabel, montantLabel);

        
        GridPane detailsGrid = new GridPane();
        detailsGrid.setHgap(20);
        detailsGrid.setVgap(10);

        
        Label dateCreationLabel = new Label("Date d'ouverture:");
        dateCreationLabel.getStyleClass().add("detail-label");
        Label dateCreationValue = new Label(dateFormat.format(compte.getDateCreation()));
        dateCreationValue.getStyleClass().add("detail-value");

        
        Label fraisLabel = new Label("Frais bancaires:");
        fraisLabel.getStyleClass().add("detail-label");
        Label fraisValue = new Label(formatMonnaie.format(compte.getFraisBancaire()));
        fraisValue.getStyleClass().add("detail-value");

        detailsGrid.add(dateCreationLabel, 0, 0);
        detailsGrid.add(dateCreationValue, 1, 0);
        detailsGrid.add(fraisLabel, 0, 1);
        detailsGrid.add(fraisValue, 1, 1);





        
        card.setTop(headerBox);
        card.setCenter(centerBox);

        return card;
    }


    private void showNoAccountsMessage() {
        VBox messageBox = new VBox(20);
        messageBox.setAlignment(Pos.CENTER);

        FontAwesomeIconView icon = new FontAwesomeIconView();
        icon.setGlyphName("BANK");
        icon.setGlyphStyle("-fx-fill: #6c757d; -fx-font-size: 48px;");

        Label messageLabel = new Label("Vous n'avez pas encore de compte bancaire. Rendez-vous en agence pour en ouvrir un.");
        messageLabel.getStyleClass().add("no-accounts-message");

        messageBox.getChildren().addAll(icon, messageLabel);
        comptesContainer.getChildren().add(messageBox);
    }

    private void showNoTransactionsMessage() {
        transactionsData.clear();

        VBox messageBox = new VBox(20);
        messageBox.setAlignment(Pos.CENTER);

        FontAwesomeIconView icon = new FontAwesomeIconView();
        icon.setGlyphName("EXCHANGE");
        icon.setGlyphStyle("-fx-fill: #6c757d; -fx-font-size: 48px;");

        Label messageLabel = new Label("Aucune transaction trouvée pour ce compte.");
        messageLabel.getStyleClass().add("no-transactions-message");

        messageBox.getChildren().addAll(icon, messageLabel);
    }
}

package com.isi.senebanque.controllers.client;

import com.isi.senebanque.dtos.requests.carteBancaire.CarteBancaireRequestDTO;
import com.isi.senebanque.dtos.responses.carteBancaire.CarteBancaireResponseDTO;
import com.isi.senebanque.dtos.responses.compte.CompteResponseDTO;
import com.isi.senebanque.dtos.responses.client.ClientResponseDTO;
import com.isi.senebanque.models.Model;
import com.isi.senebanque.models.Client;
import com.isi.senebanque.services.CarteBancaireService;
import com.isi.senebanque.services.ClientService;
import com.isi.senebanque.services.CompteService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Optional;

public class DashboardController implements Initializable {

    @FXML
    private Label lblBienvenue;

    @FXML
    private Label lblNomComplet;

    @FXML
    private Label lblUsername;

    @FXML
    private Label lblEmail;

    @FXML
    private Label lblTelephone;

    @FXML
    private Label lblDateInscription;

    @FXML
    private VBox cartesContainer;

    private final ClientService clientService = new ClientService();
    private final CompteService compteService = new CompteService();
    private final CarteBancaireService carteBancaireService = new CarteBancaireService();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/yy");

    private Client clientConnecte;
    private List<CompteResponseDTO> comptes;
    private List<CarteBancaireResponseDTO> cartes;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        
        clientConnecte = Model.getInstance().getClientConnecte();

        if (clientConnecte != null) {
            loadClientInfo();
            loadComptes();
            loadCartes();
            updateCartesUI();
        } else {
            showErrorMessage("Erreur", "Aucun client connecté");
        }
    }

    private void loadClientInfo() {
        ClientResponseDTO client = clientService.getClientById(clientConnecte.getId());

        if (client != null) {
            lblBienvenue.setText("Bienvenue, " + client.getPrenom());
            lblNomComplet.setText(client.getPrenom() + " " + client.getNom());
            lblUsername.setText(client.getUsername());
            lblEmail.setText(client.getEmail());
            lblTelephone.setText(client.getTelephone());

            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            lblDateInscription.setText(df.format(client.getDate_inscription()));
        }
    }

    private void loadComptes() {
        comptes = compteService.getComptesByClient(clientConnecte.getId());
    }

    private void loadCartes() {
        
        cartes = carteBancaireService.getCartesBancairesByClient(clientConnecte.getId());
    }

    private void updateCartesUI() {
        cartesContainer.getChildren().clear();

        if (comptes.isEmpty()) {
            
            showEmptyCardsMessage("Vous n'avez aucun compte bancaire. Veuillez vous rendre en agence pour en ouvrir un.");
            return;
        }

        
        for (CompteResponseDTO compte : comptes) {
            
            CarteBancaireResponseDTO carte = findCardForAccount(compte.getId());

            if (carte != null) {
                
                addCardToUI(carte, compte);
            } else {
                
                addRequestCardUI(compte);
            }
        }
    }

    private CarteBancaireResponseDTO findCardForAccount(Long compteId) {
        return cartes.stream()
                .filter(carte -> carte.getCompteId().equals(compteId))
                .findFirst()
                .orElse(null);
    }

    private void addCardToUI(CarteBancaireResponseDTO carte, CompteResponseDTO compte) {
        BorderPane cardPane = new BorderPane();
        cardPane.getStyleClass().add("carte-bancaire");

        
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(10);

        Label bankName = new Label("SeneBanque");
        bankName.getStyleClass().add("bank-name");

        FontAwesomeIconView chipIcon = new FontAwesomeIconView();
        chipIcon.setGlyphName("CREDIT_CARD");
        chipIcon.setGlyphStyle("-fx-fill: gold; -fx-font-size: 24px;");

        header.getChildren().addAll(bankName, chipIcon);

        
        String displayNumber;
        if ("ACTIF".equals(carte.getStatut())) {
            displayNumber = formatCardNumber(carte.getNumero());
        } else {
            
            displayNumber = "xxxx xxxx xxxx xxxx";
        }

        Label cardNumber = new Label(displayNumber);
        cardNumber.getStyleClass().add("card-number");

        
        Label compteInfo = new Label("Compte " + compte.getTypeCompte() + " - " + compte.getNumero());
        compteInfo.getStyleClass().add("compte-info");

        
        Label statut = new Label(carte.getStatut());
        statut.getStyleClass().add("card-status");

        if ("ACTIF".equals(carte.getStatut())) {
            statut.setTextFill(Color.GREEN);

            
            GridPane cardInfo = new GridPane();
            cardInfo.setHgap(20);
            cardInfo.setVgap(5);

            
            Label titulaireLbl = new Label("TITULAIRE");
            titulaireLbl.getStyleClass().add("card-info-label");
            Label titulaire = new Label(lblNomComplet.getText());
            titulaire.getStyleClass().add("card-info-value");

            
            Label expirationLbl = new Label("EXPIRATION");
            expirationLbl.getStyleClass().add("card-info-label");
            Label expiration = new Label(dateFormat.format(carte.getDateExpiration()));
            expiration.getStyleClass().add("card-info-value");

            
            Label soldeLbl = new Label("SOLDE");
            soldeLbl.getStyleClass().add("card-info-label");
            Label solde = new Label(String.format("%,.0f FCFA", carte.getSolde()));
            solde.getStyleClass().add("card-info-value");

            cardInfo.add(titulaireLbl, 0, 0);
            cardInfo.add(titulaire, 0, 1);
            cardInfo.add(expirationLbl, 1, 0);
            cardInfo.add(expiration, 1, 1);
            cardInfo.add(soldeLbl, 0, 2);
            cardInfo.add(solde, 0, 3);

            
            VBox securityInfo = new VBox(5);
            securityInfo.setAlignment(Pos.CENTER_RIGHT);

            Label cvvLbl = new Label("CVV: " + carte.getCvv());
            cvvLbl.getStyleClass().add("security-info");
            Label pinLbl = new Label("PIN: " + carte.getCodePin());
            pinLbl.getStyleClass().add("security-info");

            securityInfo.getChildren().addAll(cvvLbl, pinLbl);

            
            VBox leftBox = new VBox(10);
            leftBox.getChildren().addAll(cardNumber, compteInfo, cardInfo);

            cardPane.setTop(header);
            cardPane.setLeft(leftBox);
            cardPane.setRight(securityInfo);

        } else {
            
            statut.setTextFill(Color.ORANGE);

            VBox infoBox = new VBox(15);
            infoBox.setAlignment(Pos.CENTER);

            Label statusLabel = new Label("Statut: " + carte.getStatut());
            statusLabel.getStyleClass().add("card-info-value");

            Label waitingMessage = new Label("Votre demande de carte est en cours de traitement.\nElle sera bientôt activée par notre service client.");
            waitingMessage.getStyleClass().add("waiting-message");
            waitingMessage.setAlignment(Pos.CENTER);

            infoBox.getChildren().addAll(cardNumber, compteInfo, statusLabel, waitingMessage);

            cardPane.setTop(header);
            cardPane.setCenter(infoBox);
        }

        
        cartesContainer.getChildren().add(cardPane);
    }

    private void addRequestCardUI(CompteResponseDTO compte) {
        BorderPane cardPane = new BorderPane();
        cardPane.getStyleClass().add("carte-bancaire-request");

        VBox contentBox = new VBox(20);
        contentBox.setAlignment(Pos.CENTER);

        Label accountLabel = new Label("Compte " + compte.getTypeCompte());
        accountLabel.getStyleClass().add("account-label");

        Label compteNumero = new Label(compte.getNumero());
        compteNumero.getStyleClass().add("compte-numero");

        Label infoLabel = new Label("Vous n'avez pas encore de carte bancaire pour ce compte.");
        infoLabel.getStyleClass().add("info-label");

        Button requestButton = new Button("Demander une carte");
        requestButton.getStyleClass().add("request-card-button");

        requestButton.setOnAction(event -> {
            demanderCarte(compte);
        });

        contentBox.getChildren().addAll(accountLabel, compteNumero, infoLabel, requestButton);
        cardPane.setCenter(contentBox);

        cartesContainer.getChildren().add(cardPane);
    }

    private void showEmptyCardsMessage(String message) {
        BorderPane emptyPane = new BorderPane();
        emptyPane.getStyleClass().add("empty-cards-pane");

        VBox contentBox = new VBox(20);
        contentBox.setAlignment(Pos.CENTER);

        FontAwesomeIconView icon = new FontAwesomeIconView();
        icon.setGlyphName("CREDIT_CARD");
        icon.setGlyphStyle("-fx-fill: #888; -fx-font-size: 48px;");

        Label messageLabel = new Label(message);
        messageLabel.getStyleClass().add("empty-cards-message");

        contentBox.getChildren().addAll(icon, messageLabel);
        emptyPane.setCenter(contentBox);

        cartesContainer.getChildren().add(emptyPane);
    }

    private void demanderCarte(CompteResponseDTO compte) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Demande de carte bancaire");
        confirmation.setHeaderText("Confirmation de demande");
        confirmation.setContentText("Souhaitez-vous demander une carte bancaire pour votre compte " + compte.getTypeCompte() + " ?");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            
            CarteBancaireRequestDTO requestDTO = new CarteBancaireRequestDTO();
            requestDTO.setCompteId(compte.getId());
            requestDTO.setSolde(compte.getSolde());
            requestDTO.setStatut("EN ATTENTE"); 

            CarteBancaireResponseDTO nouvelleCarteResponse = carteBancaireService.ajouterCarteBancaire(requestDTO);

            if (nouvelleCarteResponse != null) {
                showInfoMessage("Succès", "Votre demande de carte a été enregistrée. Elle sera traitée par notre service client dans les plus brefs délais.");

                
                loadCartes();
                updateCartesUI();
            } else {
                showErrorMessage("Erreur", "Impossible de créer la carte. Veuillez réessayer plus tard.");
            }
        }
    }

    private String formatCardNumber(String numero) {
        
        return numero;
    }

    private void showInfoMessage(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorMessage(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

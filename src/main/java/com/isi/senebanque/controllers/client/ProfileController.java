package com.isi.senebanque.controllers.client;

import com.isi.senebanque.dtos.responses.compte.CompteResponseDTO;
import com.isi.senebanque.dtos.responses.credit.CreditResponseDTO;
import com.isi.senebanque.dtos.responses.transaction.TransactionResponseDTO;
import com.isi.senebanque.models.Client;
import com.isi.senebanque.models.Model;
import com.isi.senebanque.services.CompteService;
import com.isi.senebanque.services.CreditService;
import com.isi.senebanque.services.TransactionService;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class ProfileController implements Initializable {


    @FXML
    private Label lblPrenom;

    @FXML
    private Label lblNom;

    @FXML
    private Label lblEmail;

    @FXML
    private Label lblTelephone;

    @FXML
    private Label lblAdresse;

    
    @FXML
    private Label lblNombreComptes;

    @FXML
    private Label lblDateOuverture;

    @FXML
    private Label lblAgence;

    
    @FXML
    private Label lblNombreTransactions;

    @FXML
    private Label lblNombreCredits;

    
    @FXML
    private Button btnModifierPassword;

    @FXML
    private Button btnMettreAJourProfil;

    
    private final CompteService compteService = new CompteService();
    private final TransactionService transactionService = new TransactionService();
    private final CreditService creditService = new CreditService();

    
    private final SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy");

    
    private Client clientConnecte;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
        clientConnecte = Model.getInstance().getClientConnecte();

        if (clientConnecte != null) {
            
            chargerInformationsClient();

            
            chargerInformationsBancaires();

            
            chargerStatistiques();
        }
    }

    private void chargerInformationsClient() {
        
        lblPrenom.setText(clientConnecte.getPrenom());
        lblNom.setText(clientConnecte.getNom());
        lblEmail.setText(clientConnecte.getEmail());
        lblTelephone.setText(clientConnecte.getTelephone());

        
        String adresseComplete = clientConnecte.getAdresse();
        if (clientConnecte.getAdresse() != null && !clientConnecte.getAdresse().isEmpty()) {
            adresseComplete += ", " + clientConnecte.getAdresse();
        }

        lblAdresse.setText(adresseComplete);
    }

    private void chargerInformationsBancaires() {
        
        List<CompteResponseDTO> comptes = compteService.getComptesByClient(clientConnecte.getId());

        
        lblNombreComptes.setText(String.valueOf(comptes.size()));

        
        if (!comptes.isEmpty()) {
            Date dateOuverture = comptes.get(0).getDateCreation();

            for (CompteResponseDTO compte : comptes) {
                if (compte.getDateCreation().before(dateOuverture)) {
                    dateOuverture = compte.getDateCreation();
                }
            }

            lblDateOuverture.setText(formatDate.format(dateOuverture));
        } else {
            lblDateOuverture.setText("-");
        }

        
        lblAgence.setText("Agence Principale");
    }

    private void chargerStatistiques() {
        
        List<CompteResponseDTO> comptes = compteService.getComptesByClient(clientConnecte.getId());

        
        int nombreTransactions = 0;

        for (CompteResponseDTO compte : comptes) {
            List<TransactionResponseDTO> transactions = transactionService.getTransactionsByCompte(compte.getId());
            if (transactions != null) {
                nombreTransactions += transactions.size();
            }
        }

        lblNombreTransactions.setText(String.valueOf(nombreTransactions));

        
        List<CreditResponseDTO> credits = creditService.getCreditsByClient(clientConnecte.getId());
        if (credits != null) {
            lblNombreCredits.setText(String.valueOf(credits.size()));
        } else {
            lblNombreCredits.setText("0");
        }
    }

    @FXML
    private void ouvrirModifierPassword() {
        
        

        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Modification de mot de passe");
        alert.setHeaderText("Fonctionnalité en cours de développement");
        alert.setContentText("La modification du mot de passe sera bientôt disponible.");
        alert.showAndWait();
    }

    @FXML
    private void ouvrirMettreAJourProfil() {
        
        

        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Mise à jour du profil");
        alert.setHeaderText("Fonctionnalité en cours de développement");
        alert.setContentText("La mise à jour du profil sera bientôt disponible.");
        alert.showAndWait();
    }
}

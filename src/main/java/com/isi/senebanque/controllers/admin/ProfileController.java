package com.isi.senebanque.controllers.admin;

import com.isi.senebanque.models.Admin;
import com.isi.senebanque.models.Model;
import com.isi.senebanque.services.AdminService;
import com.isi.senebanque.services.ClientService;
import com.isi.senebanque.services.CompteService;
import com.isi.senebanque.services.CreditService;
import com.isi.senebanque.services.TransactionService;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Paint;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;

import java.net.URL;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class ProfileController implements Initializable {


    @FXML
    private Label lblNombreClients;

    @FXML
    private Label lblNombreComptes;

    @FXML
    private Label lblNombreTransactions;

    @FXML
    private Label lblNombreCredits;


    @FXML
    private Label lblIdentifiant;

    @FXML
    private Label lblNomComplet;

    @FXML
    private Label lblEmail;

    @FXML
    private Label lblRole;



    @FXML
    private Label lblTransactionsAujourdhui;

    @FXML
    private Label lblTransactionsEvolution;

    @FXML
    private Label lblCreditsEnCours;

    @FXML
    private FontAwesomeIconView iconCreditCours;

    @FXML
    private Label lblVolumeTransactions;



    @FXML
    private Button btnModifierPassword;

    @FXML
    private Button btnGererUtilisateurs;

    @FXML
    private Button btnParametresSysteme;


    private final AdminService adminService = new AdminService();
    private final ClientService clientService = new ClientService();
    private final CompteService compteService = new CompteService();
    private final TransactionService transactionService = new TransactionService();
    private final CreditService creditService = new CreditService();


    private final NumberFormat formatMonnaie = NumberFormat.getCurrencyInstance(new Locale("fr", "SN"));


    private Admin adminConnecte;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        adminConnecte = Model.getInstance().getAdminConnecte();

        if (adminConnecte != null) {

            chargerDonneesDashboard();


            chargerInformationsAdmin();


            chargerStatistiquesSysteme();
        }
    }

    private void chargerDonneesDashboard() {
        
        long nombreClients = clientService.getNombreClients();
        lblNombreClients.setText(String.valueOf(nombreClients));

        
        long nombreComptes = compteService.getAllComptes().size();
        lblNombreComptes.setText(String.valueOf(nombreComptes));

        
        long nombreTransactions = transactionService.getNombreTransactionsValidees();
        lblNombreTransactions.setText(String.valueOf(nombreTransactions));

        
        long nombreCredits = creditService.getNombreTotalCredits();
        lblNombreCredits.setText(String.valueOf(nombreCredits));
    }

    private void chargerInformationsAdmin() {
        
        lblIdentifiant.setText(adminConnecte.getUsername());

        
        String nomComplet = adminConnecte.getPrenom() + " " + adminConnecte.getNom();
        lblNomComplet.setText(nomComplet);

        
        lblEmail.setText(adminConnecte.getEmail());

        
        lblRole.setText("Administrateur");


    }

    private void chargerStatistiquesSysteme() {
        
        long transactionsAujourdhui = transactionService.getNombreTransactionsDuJour();
        lblTransactionsAujourdhui.setText(String.valueOf(transactionsAujourdhui));

        
        double evolutionPourcentage = 12.5;
        lblTransactionsEvolution.setText("+" + String.format("%.1f", evolutionPourcentage) + "%");

        
        if (evolutionPourcentage < 0) {
            lblTransactionsEvolution.getStyleClass().remove("positive");
            lblTransactionsEvolution.getStyleClass().add("negative");
        }

        
        long creditsEnCours = creditService.getNombreCreditsEnCours();
        lblCreditsEnCours.setText(String.valueOf(creditsEnCours));

        
        if (creditsEnCours > 5) {
            iconCreditCours.setFill(Paint.valueOf("#ffc107")); 
        } else {
            iconCreditCours.setFill(Paint.valueOf("#28a745")); 
        }

        
        double volumeTransactions = transactionService.getMontantTotalTransactions();
        lblVolumeTransactions.setText(formatMonnaie.format(volumeTransactions).replace("FCFA", "").trim());

    }

    @FXML
    private void modifierMotDePasse() {
        

        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Modification du mot de passe");
        alert.setHeaderText("Fonctionnalité en cours de développement");
        alert.setContentText("La modification du mot de passe administrateur sera bientôt disponible.");
        alert.showAndWait();
    }

    @FXML
    private void gererUtilisateurs() {
        

        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Gestion des utilisateurs");
        alert.setHeaderText("Fonctionnalité en cours de développement");
        alert.setContentText("La gestion des utilisateurs sera bientôt disponible.");
        alert.showAndWait();
    }

    @FXML
    private void ouvrirParametresSysteme() {
        

        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Paramètres système");
        alert.setHeaderText("Fonctionnalité en cours de développement");
        alert.setContentText("Les paramètres système seront bientôt disponibles.");
        alert.showAndWait();
    }
}

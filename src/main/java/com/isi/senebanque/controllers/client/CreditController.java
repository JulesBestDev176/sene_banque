package com.isi.senebanque.controllers.client;

import com.isi.senebanque.dtos.requests.credit.CreditRequestDTO;
import com.isi.senebanque.dtos.responses.credit.CreditResponseDTO;
import com.isi.senebanque.models.Client;
import com.isi.senebanque.models.Model;
import com.isi.senebanque.services.CreditService;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Calendar;

public class CreditController implements Initializable {

    
    @FXML
    private TableView<CreditResponseDTO> tableCredits;

    @FXML
    private TableColumn<CreditResponseDTO, String> colDateDemande;

    @FXML
    private TableColumn<CreditResponseDTO, Double> colMontant;

    @FXML
    private TableColumn<CreditResponseDTO, Double> colTaux;

    @FXML
    private TableColumn<CreditResponseDTO, Double> colMensualite;

    @FXML
    private TableColumn<CreditResponseDTO, String> colDateLimite;

    @FXML
    private TableColumn<CreditResponseDTO, String> colStatut;

    
    @FXML
    private TextField champMontant;

    @FXML
    private Slider sliderDuree;

    @FXML
    private Label lblDuree;

    @FXML
    private Label lblTauxInteret;

    @FXML
    private Label lblMensualite;

    @FXML
    private Label lblTotalRembourser;

    @FXML
    private Label lblMessageErreur;

    @FXML
    private Button btnDemanderCredit;

    
    private final CreditService creditService = new CreditService();

    
    private final SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy");
    private final NumberFormat formatMonnaie = NumberFormat.getCurrencyInstance(new Locale("fr", "SN"));

    
    private Client clientConnecte;
    private ObservableList<CreditResponseDTO> listeCredits = FXCollections.observableArrayList();

    
    private static final double MONTANT_MIN = 100000.0;
    private static final double MONTANT_MAX = 10000000.0;
    private static final int DUREE_MIN = 6;
    private static final int DUREE_MAX = 60;
    private static final double TAUX_MIN = 5.0;
    private static final double TAUX_MAX = 12.0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        
        clientConnecte = Model.getInstance().getClientConnecte();

        if (clientConnecte != null) {
            
            configurerTableCredits();

            
            configurerFormulaireCredit();

            
            chargerCredits();
        }
    }

    private void configurerTableCredits() {
        
        colDateDemande.setCellValueFactory(data ->
                new SimpleStringProperty(formatDate.format(data.getValue().getDateDemande())));

        colMontant.setCellValueFactory(data ->
                new SimpleDoubleProperty(data.getValue().getMontant()).asObject());
        colMontant.setCellFactory(tc -> new TableCell<CreditResponseDTO, Double>() {
            @Override
            protected void updateItem(Double montant, boolean empty) {
                super.updateItem(montant, empty);
                if (empty || montant == null) {
                    setText(null);
                } else {
                    setText(formatMonnaie.format(montant));
                }
            }
        });

        colTaux.setCellValueFactory(data ->
                new SimpleDoubleProperty(data.getValue().getTauxInteret()).asObject());
        colTaux.setCellFactory(tc -> new TableCell<CreditResponseDTO, Double>() {
            @Override
            protected void updateItem(Double taux, boolean empty) {
                super.updateItem(taux, empty);
                if (empty || taux == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f%%", taux));
                }
            }
        });

        colMensualite.setCellValueFactory(data ->
                new SimpleDoubleProperty(data.getValue().getMensualite()).asObject());
        colMensualite.setCellFactory(tc -> new TableCell<CreditResponseDTO, Double>() {
            @Override
            protected void updateItem(Double mensualite, boolean empty) {
                super.updateItem(mensualite, empty);
                if (empty || mensualite == null) {
                    setText(null);
                } else {
                    setText(formatMonnaie.format(mensualite));
                }
            }
        });

        colDateLimite.setCellValueFactory(data ->
                new SimpleStringProperty(formatDate.format(data.getValue().getDateLimite())));

        colStatut.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getStatut()));
        colStatut.setCellFactory(tc -> new TableCell<CreditResponseDTO, String>() {
            @Override
            protected void updateItem(String statut, boolean empty) {
                super.updateItem(statut, empty);
                if (empty || statut == null) {
                    setText(null);
                    getStyleClass().removeAll("statut-en-attente", "statut-approuve", "statut-rejete", "statut-solde");
                } else {
                    setText(statut);
                    getStyleClass().removeAll("statut-en-attente", "statut-approuve", "statut-rejete", "statut-solde");
                    if (statut.equalsIgnoreCase(CreditService.STATUT_EN_ATTENTE)) {
                        getStyleClass().add("statut-en-attente");
                    } else if (statut.equalsIgnoreCase(CreditService.STATUT_APPROUVE)) {
                        getStyleClass().add("statut-approuve");
                    } else if (statut.equalsIgnoreCase(CreditService.STATUT_REJETE)) {
                        getStyleClass().add("statut-rejete");
                    } else if (statut.equalsIgnoreCase(CreditService.STATUT_SOLDE)) {
                        getStyleClass().add("statut-solde");
                    }
                }
            }
        });

        
        tableCredits.setItems(listeCredits);
    }

    private void configurerFormulaireCredit() {
        
        champMontant.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                champMontant.setText(newValue.replaceAll("[^\\d]", ""));
            }
            calculerCredit();
        });

        
        sliderDuree.valueProperty().addListener((observable, oldValue, newValue) -> {
            int duree = newValue.intValue();
            lblDuree.setText(duree + " mois");
            calculerCredit();
        });

        
        sliderDuree.setValue(24);
        lblDuree.setText("24 mois");
    }

    private void chargerCredits() {
        if (clientConnecte != null) {
            
            List<CreditResponseDTO> credits = creditService.getCreditsByClient(clientConnecte.getId());

            
            listeCredits.clear();
            listeCredits.addAll(credits);

            System.out.println("Nombre de crédits chargés : " + listeCredits.size());
        }
    }

    private void calculerCredit() {
        
        lblMessageErreur.setText("");

        
        double montant = 0;
        try {
            if (!champMontant.getText().isEmpty()) {
                montant = Double.parseDouble(champMontant.getText());
            }
        } catch (NumberFormatException e) {
            lblMessageErreur.setText("Montant invalide");
            return;
        }

        int duree = (int) sliderDuree.getValue();

        
        if (montant < MONTANT_MIN) {
            lblTauxInteret.setText("Taux: -");
            lblMensualite.setText("Mensualité: -");
            lblTotalRembourser.setText("Total: -");
            return;
        }

        if (montant > MONTANT_MAX) {
            lblMessageErreur.setText("Le montant maximum est de " + formatMonnaie.format(MONTANT_MAX));
            return;
        }

        
        double tauxInteret = calculerTauxInteret(montant, duree);

        
        double tauxMensuel = tauxInteret / 100 / 12;
        double mensualite = montant * tauxMensuel * Math.pow(1 + tauxMensuel, duree) / (Math.pow(1 + tauxMensuel, duree) - 1);

        
        double totalRembourser = mensualite * duree;

        
        lblTauxInteret.setText("Taux: " + String.format("%.2f", tauxInteret) + "%");
        lblMensualite.setText("Mensualité: " + formatMonnaie.format(mensualite));
        lblTotalRembourser.setText("Total: " + formatMonnaie.format(totalRembourser));
    }

    private double calculerTauxInteret(double montant, int duree) {
        
        
        

        double tauxBase = 8.0; 

        
        double ajustementMontant = -3.0 * (montant - MONTANT_MIN) / (MONTANT_MAX - MONTANT_MIN);

        
        double ajustementDuree = 4.0 * (duree - DUREE_MIN) / (DUREE_MAX - DUREE_MIN);

        
        double taux = tauxBase + ajustementMontant + ajustementDuree;

        
        return Math.max(TAUX_MIN, Math.min(TAUX_MAX, taux));
    }

    @FXML
    private void demanderCredit() {
        
        lblMessageErreur.setText("");

        
        if (!validerFormulaire()) {
            return;
        }

        
        double montant = Double.parseDouble(champMontant.getText());
        int duree = (int) sliderDuree.getValue();
        double tauxInteret = calculerTauxInteret(montant, duree);

        
        double tauxMensuel = tauxInteret / 100 / 12;
        double mensualite = montant * tauxMensuel * Math.pow(1 + tauxMensuel, duree) / (Math.pow(1 + tauxMensuel, duree) - 1);

        
        CreditRequestDTO creditRequestDTO = new CreditRequestDTO();
        creditRequestDTO.setMontant(montant);
        creditRequestDTO.setTauxInteret(tauxInteret);
        creditRequestDTO.setMensualite(mensualite);

        
        Date dateDemande = new Date();
        creditRequestDTO.setDateDemande(dateDemande);

        
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateDemande);
        calendar.add(Calendar.MONTH, duree);
        Date dateLimite = calendar.getTime();
        creditRequestDTO.setDateLimite(dateLimite);

        
        creditRequestDTO.setClientId(clientConnecte.getId());

        
        CreditResponseDTO resultat = creditService.ajouterCredit(creditRequestDTO);

        if (resultat != null) {
            
            afficherAlerte(Alert.AlertType.INFORMATION, "Demande envoyée",
                    "Votre demande de crédit a été envoyée avec succès",
                    "Votre demande sera examinée par un conseiller bancaire. " +
                            "Vous serez notifié dès qu'une décision aura été prise.");

            
            reinitialiserFormulaire();

            
            chargerCredits();
        } else {
            
            afficherAlerte(Alert.AlertType.ERROR, "Erreur",
                    "Erreur lors de l'envoi de la demande",
                    "Une erreur est survenue lors de l'envoi de votre demande de crédit. Veuillez réessayer plus tard.");
        }
    }

    private boolean validerFormulaire() {
        
        if (champMontant.getText().isEmpty()) {
            lblMessageErreur.setText("Veuillez saisir un montant");
            return false;
        }

        
        double montant;
        try {
            montant = Double.parseDouble(champMontant.getText());
        } catch (NumberFormatException e) {
            lblMessageErreur.setText("Montant invalide");
            return false;
        }

        
        if (montant < MONTANT_MIN) {
            lblMessageErreur.setText("Le montant minimum est de " + formatMonnaie.format(MONTANT_MIN));
            return false;
        }

        if (montant > MONTANT_MAX) {
            lblMessageErreur.setText("Le montant maximum est de " + formatMonnaie.format(MONTANT_MAX));
            return false;
        }

        
        boolean aUnCreditEnCours = listeCredits.stream()
                .anyMatch(credit -> credit.getStatut().equalsIgnoreCase(CreditService.STATUT_APPROUVE) ||
                        credit.getStatut().equalsIgnoreCase(CreditService.STATUT_EN_ATTENTE));

        if (aUnCreditEnCours) {
            lblMessageErreur.setText("Vous avez déjà un crédit en cours ou en attente d'approbation");
            return false;
        }

        return true;
    }

    private void reinitialiserFormulaire() {
        champMontant.clear();
        sliderDuree.setValue(24);
        lblDuree.setText("24 mois");
        lblTauxInteret.setText("Taux: -");
        lblMensualite.setText("Mensualité: -");
        lblTotalRembourser.setText("Total: -");
        lblMessageErreur.setText("");
    }

    private void afficherAlerte(Alert.AlertType type, String titre, String entete, String contenu) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(entete);
        alert.setContentText(contenu);
        alert.showAndWait();
    }
}

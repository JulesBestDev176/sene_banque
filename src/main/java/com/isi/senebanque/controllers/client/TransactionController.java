package com.isi.senebanque.controllers.client;

import com.isi.senebanque.dtos.requests.transaction.TransactionRequestDTO;
import com.isi.senebanque.dtos.responses.compte.CompteResponseDTO;
import com.isi.senebanque.dtos.responses.transaction.TransactionResponseDTO;
import com.isi.senebanque.models.Client;
import com.isi.senebanque.models.Model;
import com.isi.senebanque.services.CompteService;
import com.isi.senebanque.services.TransactionService;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class TransactionController implements Initializable {

    
    @FXML
    private TableView<TransactionResponseDTO> tableTransactions;

    @FXML
    private TableColumn<TransactionResponseDTO, String> colDate;

    @FXML
    private TableColumn<TransactionResponseDTO, String> colNumero;

    @FXML
    private TableColumn<TransactionResponseDTO, String> colType;

    @FXML
    private TableColumn<TransactionResponseDTO, Double> colMontant;

    @FXML
    private TableColumn<TransactionResponseDTO, String> colCompteSource;

    @FXML
    private TableColumn<TransactionResponseDTO, String> colCompteDest;

    @FXML
    private TableColumn<TransactionResponseDTO, String> colStatut;

    
    @FXML
    private ComboBox<CompteResponseDTO> comboCompteSource;

    @FXML
    private ComboBox<CompteResponseDTO> comboCompteDestination;

    @FXML
    private TextField champMontant;

    @FXML
    private Button btnEffectuerVirement;

    @FXML
    private Label lblSoldeDisponible;

    @FXML
    private Label lblMessageErreur;

    
    private final CompteService compteService = new CompteService();
    private final TransactionService transactionService = new TransactionService();

    
    private final SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy");
    private final NumberFormat formatMonnaie = NumberFormat.getCurrencyInstance(new Locale("fr", "SN"));

    
    private Client clientConnecte;
    private List<CompteResponseDTO> comptes;
    private ObservableList<TransactionResponseDTO> listeTransactions = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        
        clientConnecte = Model.getInstance().getClientConnecte();

        if (clientConnecte != null) {
            
            chargerComptes();
            configurerTableTransactions();
            configurerComboBoxComptes();

            
            champMontant.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.matches("\\d*(\\.\\d*)?")) {
                    champMontant.setText(oldValue);
                }
            });

            
            comboCompteSource.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    mettreAJourSoldeDisponible(newVal);
                } else {
                    lblSoldeDisponible.setText("Solde disponible: -");
                }
            });

            
            chargerTransactions();
        }
    }

    private void chargerComptes() {
        comptes = compteService.getComptesByClient(clientConnecte.getId());
    }

    private void configurerTableTransactions() {
        
        colDate.setCellValueFactory(data ->
                new SimpleStringProperty(formatDate.format(data.getValue().getDateTransaction())));

        colNumero.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getNumero()));

        colType.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getTypeTransaction()));

        colMontant.setCellValueFactory(data ->
                new SimpleDoubleProperty(data.getValue().getMontant()).asObject());

        colMontant.setCellFactory(tc -> new TableCell<TransactionResponseDTO, Double>() {
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

        colCompteSource.setCellValueFactory(data -> {
            Long compteSourceId = data.getValue().getCompteSourceId();
            if (compteSourceId == null) return new SimpleStringProperty("-");

            
            Optional<CompteResponseDTO> compteOpt = comptes.stream()
                    .filter(compte -> compte.getId().equals(compteSourceId))
                    .findFirst();

            return compteOpt.isPresent()
                    ? new SimpleStringProperty(compteOpt.get().getNumero())
                    : new SimpleStringProperty("-");
        });

        colCompteDest.setCellValueFactory(data -> {
            Long compteDestId = data.getValue().getCompteDestId();
            if (compteDestId == null) return new SimpleStringProperty("-");

            
            Optional<CompteResponseDTO> compteOpt = comptes.stream()
                    .filter(compte -> compte.getId().equals(compteDestId))
                    .findFirst();

            return compteOpt.isPresent()
                    ? new SimpleStringProperty(compteOpt.get().getNumero())
                    : new SimpleStringProperty("-");
        });

        colStatut.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getStatut()));

        colStatut.setCellFactory(tc -> new TableCell<TransactionResponseDTO, String>() {
            @Override
            protected void updateItem(String statut, boolean empty) {
                super.updateItem(statut, empty);
                if (empty || statut == null) {
                    setText(null);
                    getStyleClass().removeAll("statut-valide", "statut-rejete");
                } else {
                    setText(statut);
                    getStyleClass().removeAll("statut-valide", "statut-rejete");
                    if ("VALIDE".equals(statut)) {
                        getStyleClass().add("statut-valide");
                    } else if ("REJETE".equals(statut)) {
                        getStyleClass().add("statut-rejete");
                    }
                }
            }
        });

        
        tableTransactions.setItems(listeTransactions);
    }

    private void chargerTransactions() {
        
        List<TransactionResponseDTO> toutesTransactions = new ArrayList<>();

        
        for (CompteResponseDTO compte : comptes) {
            List<TransactionResponseDTO> transactionsCompte = transactionService.getTransactionsByCompte(compte.getId());

            
            if (transactionsCompte != null && !transactionsCompte.isEmpty()) {
                toutesTransactions.addAll(transactionsCompte);
            }
        }

        
        List<TransactionResponseDTO> transactionsUniques = toutesTransactions.stream()
                .distinct()
                .collect(Collectors.toList());

        
        transactionsUniques.sort(Comparator.comparing(TransactionResponseDTO::getDateTransaction).reversed());

        
        listeTransactions.clear();
        listeTransactions.addAll(transactionsUniques);

        
        System.out.println("Nombre de transactions chargées : " + listeTransactions.size());
    }

    private void configurerComboBoxComptes() {
        
        ObservableList<CompteResponseDTO> comptesObservables = FXCollections.observableArrayList(comptes);

        
        comboCompteSource.setItems(comptesObservables);
        comboCompteSource.setCellFactory(param -> new ListCell<CompteResponseDTO>() {
            @Override
            protected void updateItem(CompteResponseDTO compte, boolean empty) {
                super.updateItem(compte, empty);
                if (empty || compte == null) {
                    setText(null);
                } else {
                    setText(compte.getTypeCompte() + " - " + compte.getNumero());
                }
            }
        });
        comboCompteSource.setButtonCell(new ListCell<CompteResponseDTO>() {
            @Override
            protected void updateItem(CompteResponseDTO compte, boolean empty) {
                super.updateItem(compte, empty);
                if (empty || compte == null) {
                    setText(null);
                } else {
                    setText(compte.getTypeCompte() + " - " + compte.getNumero());
                }
            }
        });

        
        comboCompteDestination.setItems(comptesObservables);
        comboCompteDestination.setCellFactory(param -> new ListCell<CompteResponseDTO>() {
            @Override
            protected void updateItem(CompteResponseDTO compte, boolean empty) {
                super.updateItem(compte, empty);
                if (empty || compte == null) {
                    setText(null);
                } else {
                    setText(compte.getTypeCompte() + " - " + compte.getNumero());
                }
            }
        });
        comboCompteDestination.setButtonCell(new ListCell<CompteResponseDTO>() {
            @Override
            protected void updateItem(CompteResponseDTO compte, boolean empty) {
                super.updateItem(compte, empty);
                if (empty || compte == null) {
                    setText(null);
                } else {
                    setText(compte.getTypeCompte() + " - " + compte.getNumero());
                }
            }
        });
    }


    private void mettreAJourSoldeDisponible(CompteResponseDTO compte) {
        if (compte != null) {
            lblSoldeDisponible.setText("Solde disponible: " + formatMonnaie.format(compte.getSolde()));
        } else {
            lblSoldeDisponible.setText("Solde disponible: -");
        }
    }

    @FXML
    private void effectuerVirement() {
        
        lblMessageErreur.setText("");

        
        if (!validerFormulaire()) {
            return;
        }

        
        CompteResponseDTO compteSource = comboCompteSource.getValue();
        CompteResponseDTO compteDest = comboCompteDestination.getValue();
        double montant = Double.parseDouble(champMontant.getText());

        
        TransactionRequestDTO transactionDTO = new TransactionRequestDTO();
        transactionDTO.setTypeTransaction(TransactionService.TYPE_VIREMENT);
        transactionDTO.setMontant(montant);
        transactionDTO.setCompteSourceId(compteSource.getId());
        transactionDTO.setCompteDestId(compteDest.getId());

        
        TransactionResponseDTO resultat = transactionService.effectuerVirement(transactionDTO);

        if (resultat != null && TransactionService.STATUT_VALIDE.equals(resultat.getStatut())) {
            
            afficherAlerte(Alert.AlertType.INFORMATION, "Virement effectué",
                    "Le virement a été effectué avec succès",
                    "Un montant de " + formatMonnaie.format(montant) + " a été transféré du compte " +
                            compteSource.getNumero() + " vers le compte " + compteDest.getNumero());

            
            reinitialiserFormulaire();

            
            chargerComptes();
            configurerComboBoxComptes();
            chargerTransactions();
        } else {
            
            String messageErreur = "Le virement n'a pas pu être effectué.";
            if (resultat != null && TransactionService.STATUT_REJETE.equals(resultat.getStatut())) {
                messageErreur += " Solde insuffisant.";
            }

            afficherAlerte(Alert.AlertType.ERROR, "Erreur de virement", "Le virement a échoué", messageErreur);
        }
    }

    private boolean validerFormulaire() {
        
        if (comboCompteSource.getValue() == null) {
            lblMessageErreur.setText("Veuillez sélectionner un compte source");
            return false;
        }

        if (comboCompteDestination.getValue() == null) {
            lblMessageErreur.setText("Veuillez sélectionner un compte destination");
            return false;
        }

        if (champMontant.getText().isEmpty()) {
            lblMessageErreur.setText("Veuillez saisir un montant");
            return false;
        }

        
        if (comboCompteSource.getValue().getId().equals(comboCompteDestination.getValue().getId())) {
            lblMessageErreur.setText("Les comptes source et destination doivent être différents");
            return false;
        }

        
        try {
            double montant = Double.parseDouble(champMontant.getText());
            if (montant <= 0) {
                lblMessageErreur.setText("Le montant doit être supérieur à 0");
                return false;
            }

            
            if (montant > comboCompteSource.getValue().getSolde()) {
                lblMessageErreur.setText("Solde insuffisant pour effectuer ce virement");
                return false;
            }
        } catch (NumberFormatException e) {
            lblMessageErreur.setText("Montant invalide");
            return false;
        }

        return true;
    }

    private void reinitialiserFormulaire() {
        comboCompteSource.setValue(null);
        comboCompteDestination.setValue(null);
        champMontant.clear();
        lblSoldeDisponible.setText("Solde disponible: -");
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

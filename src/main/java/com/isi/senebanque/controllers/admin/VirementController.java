package com.isi.senebanque.controllers.admin;

import com.isi.senebanque.dtos.requests.transaction.TransactionRequestDTO;
import com.isi.senebanque.dtos.responses.transaction.TransactionResponseDTO;
import com.isi.senebanque.models.Compte;
import com.isi.senebanque.services.CompteService;
import com.isi.senebanque.services.TransactionService;
import com.isi.senebanque.dtos.responses.compte.CompteResponseDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.StringConverter;

import java.net.URL;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class VirementController implements Initializable {

    @FXML
    private Label nomUtilisateurHeader;

    @FXML
    private TextField montantField;

    @FXML
    private ComboBox<Compte> compteSourceField;

    @FXML
    private ComboBox<Compte> compteDestField;

    @FXML
    private Label infoSoldeSourceLabel;

    @FXML
    private Label recapSourceLabel;

    @FXML
    private Label recapDestLabel;

    @FXML
    private Label recapMontantLabel;

    @FXML
    private Button btnAnnuler;

    @FXML
    private Button btnVerifier;

    @FXML
    private Button btnEffectuer;

    private final CompteService compteService = new CompteService();
    private final TransactionService transactionService = new TransactionService();
    private final NumberFormat formatMonnaie = NumberFormat.getCurrencyInstance(new Locale("fr", "SN"));

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        chargerComptes();
        configurerChamps();
        configurerEvenements();
    }

    private void chargerComptes() {
        
        List<CompteResponseDTO> comptesDTO = compteService.getAllComptes();

        
        List<Compte> comptes = comptesDTO.stream()
                .map(dto -> {
                    Compte compte = new Compte();
                    compte.setId(dto.getId());
                    compte.setNumero(dto.getNumero());
                    compte.setType_compte(dto.getTypeCompte());
                    compte.setSolde(dto.getSolde());
                    compte.setStatut(dto.getStatut());
                    return compte;
                })
                .collect(Collectors.toList());

        ObservableList<Compte> comptesObservable = FXCollections.observableArrayList(comptes);

        compteSourceField.setItems(comptesObservable);
        compteDestField.setItems(comptesObservable);

        
        StringConverter<Compte> converter = new StringConverter<>() {
            @Override
            public String toString(Compte compte) {
                if (compte == null) return null;
                return compte.getNumero() + " - " + compte.getType_compte();
            }

            @Override
            public Compte fromString(String s) {
                return null; 
            }
        };

        compteSourceField.setConverter(converter);
        compteDestField.setConverter(converter);
    }

    private void configurerChamps() {
        
        montantField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                montantField.setText(oldValue);
            }
            mettreAJourRecapitulatif();
        });

        
        compteSourceField.getSelectionModel().selectedItemProperty().addListener((obs, ancienCompte, nouveauCompte) -> {
            if (nouveauCompte != null) {
                infoSoldeSourceLabel.setText("Solde disponible: " + formatMonnaie.format(nouveauCompte.getSolde()));
                mettreAJourRecapitulatif();
            } else {
                infoSoldeSourceLabel.setText("Solde disponible: -");
            }
        });

        
        compteDestField.getSelectionModel().selectedItemProperty().addListener((obs, ancienCompte, nouveauCompte) -> {
            mettreAJourRecapitulatif();
        });
    }

    private void configurerEvenements() {
        btnAnnuler.setOnAction(this::annulerVirement);
        btnVerifier.setOnAction(this::verifierVirement);
        btnEffectuer.setOnAction(this::effectuerVirement);
    }

    private void mettreAJourRecapitulatif() {
        Compte compteSource = compteSourceField.getValue();
        Compte compteDest = compteDestField.getValue();

        
        recapSourceLabel.setText(compteSource != null ? compteSource.getNumero() : "-");
        recapDestLabel.setText(compteDest != null ? compteDest.getNumero() : "-");

        try {
            double montant = montantField.getText().isEmpty() ? 0 : Double.parseDouble(montantField.getText());
            recapMontantLabel.setText(formatMonnaie.format(montant));
        } catch (NumberFormatException e) {
            recapMontantLabel.setText("0 FCFA");
        }
    }

    @FXML
    private void annulerVirement(ActionEvent event) {
        reinitialiserFormulaire();
    }

    @FXML
    private void verifierVirement(ActionEvent event) {
        if (validerFormulaire()) {
            
            btnEffectuer.setDisable(false);

            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Vérification réussie");
            alert.setHeaderText("Le virement peut être effectué");
            alert.setContentText("Veuillez cliquer sur 'Effectuer le virement' pour confirmer l'opération.");
            alert.showAndWait();
        }
    }

    @FXML
    private void effectuerVirement(ActionEvent event) {
        if (!validerFormulaire()) {
            return;
        }

        try {
            
            Compte compteSource = compteSourceField.getValue();
            Compte compteDest = compteDestField.getValue();
            double montant = Double.parseDouble(montantField.getText());

            
            TransactionRequestDTO transactionDTO = new TransactionRequestDTO();
            transactionDTO.setTypeTransaction(TransactionService.TYPE_VIREMENT);
            transactionDTO.setMontant(montant);
            transactionDTO.setCompteSourceId(compteSource.getId());
            transactionDTO.setCompteDestId(compteDest.getId());
            transactionDTO.setDateTransaction(new Date());
            transactionDTO.setStatut(TransactionService.STATUT_VALIDE);

            
            TransactionResponseDTO resultat = transactionService.effectuerVirement(transactionDTO);

            if (resultat != null && TransactionService.STATUT_VALIDE.equals(resultat.getStatut())) {
                
                afficherMessageSucces("Virement effectué avec succès",
                        "Le virement a bien été effectué du compte " + compteSource.getNumero() +
                                " vers le compte " + compteDest.getNumero() +
                                " pour un montant de " + formatMonnaie.format(montant));

                
                reinitialiserFormulaire();

                
                chargerComptes();
            } else {
                
                String messageErreur = "Le virement n'a pas pu être effectué. ";

                if (resultat != null && TransactionService.STATUT_REJETE.equals(resultat.getStatut())) {
                    messageErreur += "Le solde du compte source est insuffisant ou un des comptes n'est pas actif.";
                } else {
                    messageErreur += "Une erreur est survenue lors du traitement.";
                }

                afficherMessageErreur("Échec du virement", messageErreur);
            }
        } catch (Exception e) {
            e.printStackTrace();
            afficherMessageErreur("Erreur", "Une erreur inattendue s'est produite: " + e.getMessage());
        }
    }

    private boolean validerFormulaire() {
        StringBuilder messageErreur = new StringBuilder();

        
        if (montantField.getText().isEmpty()) {
            messageErreur.append("- Le montant du virement est obligatoire\n");
        } else {
            try {
                double montant = Double.parseDouble(montantField.getText());
                if (montant < 1000) {
                    messageErreur.append("- Le montant minimum est de 1000 FCFA\n");
                }
            } catch (NumberFormatException e) {
                messageErreur.append("- Le montant doit être un nombre valide\n");
            }
        }

        
        if (compteSourceField.getValue() == null) {
            messageErreur.append("- Vous devez sélectionner un compte source\n");
        }

        
        if (compteDestField.getValue() == null) {
            messageErreur.append("- Vous devez sélectionner un compte destination\n");
        } else if (compteSourceField.getValue() != null &&
                compteSourceField.getValue().getId().equals(compteDestField.getValue().getId())) {
            messageErreur.append("- Les comptes source et destination ne peuvent pas être identiques\n");
        }

        
        if (compteSourceField.getValue() != null) {
            try {
                double montant = Double.parseDouble(montantField.getText());
                if (compteSourceField.getValue().getSolde() < montant) {
                    messageErreur.append("- Le solde du compte source est insuffisant\n");
                }
            } catch (NumberFormatException ignored) {
                
            }
        }

        
        if (messageErreur.length() > 0) {
            afficherMessageErreur("Validation échouée", messageErreur.toString());
            return false;
        }

        return true;
    }

    private void reinitialiserFormulaire() {
        montantField.clear();
        compteSourceField.getSelectionModel().clearSelection();
        compteDestField.getSelectionModel().clearSelection();
        infoSoldeSourceLabel.setText("Solde disponible: -");
        recapSourceLabel.setText("-");
        recapDestLabel.setText("-");
        recapMontantLabel.setText("0 FCFA");
        btnEffectuer.setDisable(true);
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

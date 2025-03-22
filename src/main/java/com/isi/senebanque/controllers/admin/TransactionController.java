package com.isi.senebanque.controllers.admin;

import com.isi.senebanque.dtos.requests.transaction.TransactionRequestDTO;
import com.isi.senebanque.dtos.responses.compte.CompteResponseDTO;
import com.isi.senebanque.dtos.responses.transaction.TransactionResponseDTO;
import com.isi.senebanque.services.CompteService;
import com.isi.senebanque.services.TransactionService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
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

public class TransactionController implements Initializable {

    
    @FXML
    private TableView<TransactionResponseDTO> tableTransactions;
    @FXML
    private TableColumn<TransactionResponseDTO, Long> colId;
    @FXML
    private TableColumn<TransactionResponseDTO, String> colNumero;
    @FXML
    private TableColumn<TransactionResponseDTO, String> colType;
    @FXML
    private TableColumn<TransactionResponseDTO, Double> colMontant;
    @FXML
    private TableColumn<TransactionResponseDTO, String> colDate;
    @FXML
    private TableColumn<TransactionResponseDTO, String> colCompteSource;
    @FXML
    private TableColumn<TransactionResponseDTO, String> colStatut;

    @FXML
    private Pagination paginationTransactions;
    @FXML
    private TextField rechercheField;
    @FXML
    private ComboBox<String> filtreStatut;
    @FXML
    private ComboBox<String> filtreType;
    @FXML
    private Button btnRafraichir;
    @FXML
    private Label infoPage;

    
    @FXML
    private Label totalTransactionsLabel;
    @FXML
    private Label totalValideesLabel;
    @FXML
    private Label totalRejeteesLabel;
    @FXML
    private Label montantTotalLabel;

    
    @FXML
    private ComboBox<String> typeTransactionField;
    @FXML
    private TextField montantField;
    @FXML
    private ComboBox<CompteResponseDTO> compteField;
    @FXML
    private Label infoSoldeLabel;
    @FXML
    private Button btnEnregistrer;
    @FXML
    private Button btnReinitialiser;

    
    private final int ITEMS_PAR_PAGE = 10;
    private ObservableList<TransactionResponseDTO> listeTransactions = FXCollections.observableArrayList();
    private ObservableList<TransactionResponseDTO> listeTransactionsFiltree = FXCollections.observableArrayList();

    
    private final NumberFormat formatMonnaie = NumberFormat.getNumberInstance(Locale.FRANCE);
    private final SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    
    private final TransactionService transactionService = new TransactionService();
    private final CompteService compteService = new CompteService();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("Initialisation du contrôleur TransactionController");

        
        formatMonnaie.setMaximumFractionDigits(2);
        formatMonnaie.setMinimumFractionDigits(0);

        
        filtreStatut.setItems(FXCollections.observableArrayList("Tous", TransactionService.STATUT_VALIDE, TransactionService.STATUT_REJETE));
        filtreStatut.setValue("Tous");

        filtreType.setItems(FXCollections.observableArrayList("Tous", TransactionService.TYPE_DEPOT, TransactionService.TYPE_RETRAIT));
        filtreType.setValue("Tous");

        
        typeTransactionField.setItems(FXCollections.observableArrayList(TransactionService.TYPE_DEPOT, TransactionService.TYPE_RETRAIT));
        typeTransactionField.setValue(TransactionService.TYPE_DEPOT);

        
        chargerComptesComboBox();

        
        compteField.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                infoSoldeLabel.setText("Solde disponible: " + formatMonnaie.format(newVal.getSolde()) + " FCFA");
            } else {
                infoSoldeLabel.setText("Solde disponible: -");
            }
        });

        
        configurerColonnesTable();

        
        configurerEcouteurs();

        
        Platform.runLater(() -> {
            chargerListeTransactions();
            mettreAJourStatistiques();
        });
    }

    private void chargerComptesComboBox() {
        try {
            List<CompteResponseDTO> comptes = compteService.getAllComptes();
            
            comptes = comptes.stream()
                    .filter(compte -> "ACTIF".equals(compte.getStatut()))
                    .toList();

            ObservableList<CompteResponseDTO> comptesObservable = FXCollections.observableArrayList(comptes);
            compteField.setItems(comptesObservable);

            
            compteField.setConverter(new StringConverter<CompteResponseDTO>() {
                @Override
                public String toString(CompteResponseDTO compte) {
                    if (compte == null) return "";
                    return compte.getNumero() + " (" + compte.getTypeCompte() + ")";
                }

                @Override
                public CompteResponseDTO fromString(String string) {
                    return null; 
                }
            });
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des comptes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void configurerColonnesTable() {
        System.out.println("Configuration des colonnes de la table");

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNumero.setCellValueFactory(new PropertyValueFactory<>("numero"));
        colType.setCellValueFactory(new PropertyValueFactory<>("typeTransaction"));
        colMontant.setCellValueFactory(new PropertyValueFactory<>("montant"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("dateTransaction"));
        colCompteSource.setCellValueFactory(new PropertyValueFactory<>("compteSourceId"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));

        
        colMontant.setCellFactory(column -> new TableCell<TransactionResponseDTO, Double>() {
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

        
        colType.setCellFactory(column -> new TableCell<TransactionResponseDTO, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    FontAwesomeIconView icon = new FontAwesomeIconView();
                    if (TransactionService.TYPE_DEPOT.equals(item)) {
                        icon.setGlyphName("ARROW_DOWN");
                        icon.setStyle("-fx-fill: #4caf50;");
                        setText(" " + item);
                    } else {
                        icon.setGlyphName("ARROW_UP");
                        icon.setStyle("-fx-fill: #f44336;");
                        setText(" " + item);
                    }
                    setGraphic(icon);
                }
            }
        });

        
        colStatut.setCellFactory(column -> new TableCell<TransactionResponseDTO, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label badge = new Label(item);
                    badge.getStyleClass().add("badge-statut");

                    if (TransactionService.STATUT_VALIDE.equals(item)) {
                        badge.getStyleClass().add("statut-valide");
                    } else {
                        badge.getStyleClass().add("statut-rejete");
                    }

                    setGraphic(badge);
                }
            }
        });
    }

    private void chargerListeTransactions() {
        try {
            System.out.println("Chargement de la liste des transactions");
            List<TransactionResponseDTO> transactions = transactionService.getAllTransactions();
            System.out.println("Nombre de transactions récupérées: " + transactions.size());

            listeTransactions.clear();
            listeTransactions.addAll(transactions);
            listeTransactionsFiltree.setAll(listeTransactions);

            
            configurerPagination();

            
            if (paginationTransactions.getPageCount() > 0) {
                paginationTransactions.setCurrentPageIndex(0);
                actualiserTableauParPage(0);
            }

            mettreAJourInfoPagination();
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des transactions: " + e.getMessage());
            e.printStackTrace();
            afficherMessage("Erreur", "Impossible de charger la liste des transactions: " + e.getMessage(), AlertType.ERROR);
        }
    }

    private void mettreAJourStatistiques() {
        try {
            
            long totalTransactions = listeTransactions.size();
            long transactionsValidees = transactionService.getNombreTransactionsValidees();
            long transactionsRejetees = transactionService.getNombreTransactionsRejetees();
            double montantTotal = transactionService.getMontantTotalTransactions();

            totalTransactionsLabel.setText(String.valueOf(totalTransactions));
            totalValideesLabel.setText(String.valueOf(transactionsValidees));
            totalRejeteesLabel.setText(String.valueOf(transactionsRejetees));
            montantTotalLabel.setText(formatMonnaie.format(montantTotal) + " FCFA");
        } catch (Exception e) {
            System.err.println("Erreur lors de la mise à jour des statistiques: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void configurerPagination() {
        int totalItems = listeTransactionsFiltree.size();
        int pageCount = (totalItems + ITEMS_PAR_PAGE - 1) / ITEMS_PAR_PAGE;  

        System.out.println("Configuration de la pagination - Total items: " + totalItems + ", Nombre de pages: " + pageCount);

        paginationTransactions.setPageCount(Math.max(1, pageCount));
        paginationTransactions.setCurrentPageIndex(0);

        
        paginationTransactions.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) -> {
            System.out.println("Changement de page: " + newIndex.intValue());
            actualiserTableauParPage(newIndex.intValue());
            mettreAJourInfoPagination();
        });
    }

    private void actualiserTableauParPage(int pageIndex) {
        int fromIndex = pageIndex * ITEMS_PAR_PAGE;
        int toIndex = Math.min(fromIndex + ITEMS_PAR_PAGE, listeTransactionsFiltree.size());

        System.out.println("Actualisation du tableau pour la page " + pageIndex +
                " (index " + fromIndex + " à " + toIndex + " sur " + listeTransactionsFiltree.size() + " transactions)");

        
        if (listeTransactionsFiltree.isEmpty() || fromIndex >= listeTransactionsFiltree.size()) {
            System.out.println("Aucun élément à afficher - liste vide ou index invalide");
            tableTransactions.setItems(FXCollections.observableArrayList());
        } else {
            ObservableList<TransactionResponseDTO> pageItems = FXCollections.observableArrayList(
                    listeTransactionsFiltree.subList(fromIndex, toIndex)
            );
            System.out.println("Affichage de " + pageItems.size() + " transactions sur cette page");
            tableTransactions.setItems(pageItems);
        }
    }

    private void mettreAJourInfoPagination() {
        int pageActuelle = paginationTransactions.getCurrentPageIndex();
        int debut = listeTransactionsFiltree.isEmpty() ? 0 : pageActuelle * ITEMS_PAR_PAGE + 1;
        int fin = Math.min((pageActuelle + 1) * ITEMS_PAR_PAGE, listeTransactionsFiltree.size());

        if (listeTransactionsFiltree.isEmpty()) {
            infoPage.setText("Aucune transaction trouvée");
        } else {
            infoPage.setText(String.format("Affichage %d-%d sur %d", debut, fin, listeTransactionsFiltree.size()));
        }
    }

    private void configurerEcouteurs() {
        
        rechercheField.textProperty().addListener((obs, oldVal, newVal) -> {
            filtrerListeTransactions();
        });

        
        filtreStatut.valueProperty().addListener((obs, oldVal, newVal) -> {
            filtrerListeTransactions();
        });

        
        filtreType.valueProperty().addListener((obs, oldVal, newVal) -> {
            filtrerListeTransactions();
        });

        
        btnRafraichir.setOnAction(event -> {
            System.out.println("Rafraîchissement de la liste");
            rechercheField.clear();
            filtreStatut.setValue("Tous");
            filtreType.setValue("Tous");
            chargerListeTransactions();
            mettreAJourStatistiques();
        });

        
        btnEnregistrer.setOnAction(event -> {
            effectuerTransaction();
        });

        
        btnReinitialiser.setOnAction(event -> {
            reinitialiserFormulaire();
        });
    }

    private void filtrerListeTransactions() {
        String recherche = rechercheField.getText();
        String statut = filtreStatut.getValue();
        String type = filtreType.getValue();

        System.out.println("Filtrage des transactions - Recherche: '" + recherche + "', Statut: " + statut + ", Type: " + type);

        List<TransactionResponseDTO> transactionsFiltrees = transactionService.rechercherTransactions(recherche, statut, type);
        listeTransactionsFiltree.setAll(transactionsFiltrees);

        System.out.println("Nombre de transactions après filtrage: " + listeTransactionsFiltree.size());

        
        configurerPagination();
        paginationTransactions.setCurrentPageIndex(0);
        actualiserTableauParPage(0);
        mettreAJourInfoPagination();
    }

    private void effectuerTransaction() {
        
        String typeTransaction = typeTransactionField.getValue();
        String montantText = montantField.getText().trim();
        CompteResponseDTO compte = compteField.getValue();

        
        if (typeTransaction == null || montantText.isEmpty() || compte == null) {
            afficherMessage("Champs manquants", "Veuillez remplir tous les champs obligatoires.", AlertType.WARNING);
            return;
        }

        
        double montant;
        try {
            montant = Double.parseDouble(montantText.replace(" ", "").replace(",", "."));
            if (montant <= 0) {
                afficherMessage("Montant invalide", "Le montant doit être un nombre positif.", AlertType.WARNING);
                return;
            }
        } catch (NumberFormatException e) {
            afficherMessage("Format invalide", "Le montant doit être un nombre valide.", AlertType.WARNING);
            return;
        }

        
        if (TransactionService.TYPE_RETRAIT.equals(typeTransaction) && montant > compte.getSolde()) {
            afficherMessage("Solde insuffisant",
                    "Le solde du compte (" + formatMonnaie.format(compte.getSolde()) + " FCFA) est insuffisant pour effectuer ce retrait.",
                    AlertType.WARNING);
        }

        
        TransactionRequestDTO transactionRequest = new TransactionRequestDTO();
        transactionRequest.setTypeTransaction(typeTransaction);
        transactionRequest.setMontant(montant);
        transactionRequest.setCompteSourceId(compte.getId());
        transactionRequest.setDateTransaction(new Date()); 

        try {
            
            System.out.println("Création d'une nouvelle transaction de type " + typeTransaction + " pour un montant de " + montant);
            TransactionResponseDTO resultat = transactionService.effectuerTransaction(transactionRequest);

            if (resultat != null) {
                String message;
                AlertType type;

                if (TransactionService.STATUT_VALIDE.equals(resultat.getStatut())) {
                    message = "La transaction a été effectuée avec succès.\nNuméro de transaction: " + resultat.getNumero();
                    type = AlertType.INFORMATION;
                } else {
                    message = "La transaction a été rejetée.\nRaison: Solde insuffisant ou compte inactif.";
                    type = AlertType.WARNING;
                }

                afficherMessage("Transaction traitée", message, type);

                
                reinitialiserFormulaire();
                chargerListeTransactions();
                mettreAJourStatistiques();

                
                chargerComptesComboBox();
            } else {
                afficherMessage("Erreur", "Une erreur est survenue lors du traitement de la transaction.", AlertType.ERROR);
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la création de la transaction: " + e.getMessage());
            e.printStackTrace();
            afficherMessage("Erreur", "Une erreur est survenue: " + e.getMessage(), AlertType.ERROR);
        }
    }

    private void reinitialiserFormulaire() {
        System.out.println("Réinitialisation du formulaire");

        
        typeTransactionField.setValue(TransactionService.TYPE_DEPOT);
        montantField.clear();
        compteField.setValue(null);
        infoSoldeLabel.setText("Solde disponible: -");
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

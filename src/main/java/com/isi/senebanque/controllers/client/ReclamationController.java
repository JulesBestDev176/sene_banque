package com.isi.senebanque.controllers.client;

import com.isi.senebanque.dtos.requests.ticketSupport.TicketSupportRequestDTO;
import com.isi.senebanque.dtos.responses.ticketSupport.TicketSupportResponseDTO;
import com.isi.senebanque.models.Client;
import com.isi.senebanque.models.Model;
import com.isi.senebanque.services.TicketSupportService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class ReclamationController implements Initializable {

    
    @FXML
    private TabPane tabPane;

    @FXML
    private Tab tabNouvelleDemande;

    @FXML
    private Tab tabDetailTicket;

    
    @FXML
    private TableView<TicketSupportResponseDTO> tableTickets;

    @FXML
    private TableColumn<TicketSupportResponseDTO, String> colId;

    @FXML
    private TableColumn<TicketSupportResponseDTO, String> colSujet;

    @FXML
    private TableColumn<TicketSupportResponseDTO, String> colDateOuverture;

    @FXML
    private TableColumn<TicketSupportResponseDTO, String> colStatut;

    @FXML
    private TableColumn<TicketSupportResponseDTO, Void> colActions;

    
    @FXML
    private TextField champSujet;

    @FXML
    private TextArea champDescription;

    @FXML
    private Label lblMessageErreur;

    
    @FXML
    private Label lblTicketId;

    @FXML
    private Label lblDateOuverture;

    @FXML
    private Label lblStatut;

    @FXML
    private Label lblSujet;

    @FXML
    private Label lblDescription;

    @FXML
    private Button btnCloreTicket;

    
    private final TicketSupportService ticketSupportService = new TicketSupportService();

    
    private Client clientConnecte;
    private ObservableList<TicketSupportResponseDTO> listeTickets = FXCollections.observableArrayList();
    private final SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy");
    private TicketSupportResponseDTO ticketCourant;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
        clientConnecte = Model.getInstance().getClientConnecte();

        if (clientConnecte != null) {
            
            configurerTableTickets();

            
            chargerTickets();

            
            tabPane.getTabs().remove(tabNouvelleDemande);
            tabPane.getTabs().remove(tabDetailTicket);
        }
    }

    private void configurerTableTickets() {
        
        colId.setCellValueFactory(data ->
                new SimpleStringProperty(String.valueOf(data.getValue().getId())));

        colSujet.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getSujet()));

        colDateOuverture.setCellValueFactory(data ->
                new SimpleStringProperty(formatDate.format(data.getValue().getDateOuverture())));

        
        colStatut.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getStatut()));

        colStatut.setCellFactory(column -> new TableCell<TicketSupportResponseDTO, String>() {
            @Override
            protected void updateItem(String statut, boolean empty) {
                super.updateItem(statut, empty);

                if (empty || statut == null) {
                    setText(null);
                    getStyleClass().removeAll("statut-en-cours", "statut-traite", "statut-clos");
                } else {
                    setText(statut);
                    getStyleClass().removeAll("statut-en-cours", "statut-traite", "statut-clos");

                    if (statut.equalsIgnoreCase("en cours")) {
                        getStyleClass().add("statut-en-cours");
                    } else if (statut.equalsIgnoreCase("traité")) {
                        getStyleClass().add("statut-traite");
                    } else if (statut.equalsIgnoreCase("clos")) {
                        getStyleClass().add("statut-clos");
                    }
                }
            }
        });

        
        colActions.setCellFactory(createActionCellFactory());

        
        tableTickets.setItems(listeTickets);
    }

    private Callback<TableColumn<TicketSupportResponseDTO, Void>, TableCell<TicketSupportResponseDTO, Void>> createActionCellFactory() {
        return new Callback<>() {
            @Override
            public TableCell<TicketSupportResponseDTO, Void> call(final TableColumn<TicketSupportResponseDTO, Void> param) {
                return new TableCell<>() {
                    private final Button btnVoir = new Button("Voir");

                    {
                        btnVoir.getStyleClass().add("btn-voir");

                        btnVoir.setOnAction(event -> {
                            TicketSupportResponseDTO ticket = getTableView().getItems().get(getIndex());
                            voirDetailTicket(ticket);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);

                        if (empty) {
                            setGraphic(null);
                        } else {
                            HBox container = new HBox(5, btnVoir);
                            setGraphic(container);
                        }
                    }
                };
            }
        };
    }

    private void chargerTickets() {
        if (clientConnecte != null) {
            
            List<TicketSupportResponseDTO> tickets = ticketSupportService.getTicketsByClient(clientConnecte.getId());

            
            listeTickets.clear();
            listeTickets.addAll(tickets);

            System.out.println("Nombre de tickets chargés : " + listeTickets.size());
        }
    }

    @FXML
    private void ouvrirFormulaireNouvelleDemande() {
        
        champSujet.clear();
        champDescription.clear();
        lblMessageErreur.setText("");

        
        if (!tabPane.getTabs().contains(tabNouvelleDemande)) {
            tabPane.getTabs().add(tabNouvelleDemande);
        }

        
        tabPane.getSelectionModel().select(tabNouvelleDemande);
    }

    @FXML
    private void annulerNouvelleDemande() {
        
        tabPane.getTabs().remove(tabNouvelleDemande);

        
        tabPane.getSelectionModel().selectFirst();
    }

    @FXML
    private void envoyerTicket() {
        
        if (!validerFormulaire()) {
            return;
        }

        
        TicketSupportRequestDTO ticketRequest = new TicketSupportRequestDTO();
        ticketRequest.setSujet(champSujet.getText().trim());
        ticketRequest.setDescription(champDescription.getText().trim());
        ticketRequest.setDateOuverture(new Date());
        ticketRequest.setClientId(clientConnecte.getId());

        
        TicketSupportResponseDTO resultat = ticketSupportService.ajouterTicket(ticketRequest);

        if (resultat != null) {
            
            afficherAlerte(Alert.AlertType.INFORMATION, "Réclamation envoyée",
                    "Votre réclamation a été envoyée avec succès",
                    "Votre réclamation sera traitée par notre service client dans les meilleurs délais.");

            
            tabPane.getTabs().remove(tabNouvelleDemande);

            
            chargerTickets();

            
            tabPane.getSelectionModel().selectFirst();
        } else {
            
            lblMessageErreur.setText("Une erreur est survenue lors de l'envoi de votre réclamation. Veuillez réessayer plus tard.");
        }
    }

    private boolean validerFormulaire() {
        
        if (champSujet.getText().trim().isEmpty()) {
            lblMessageErreur.setText("Veuillez saisir un sujet pour votre réclamation");
            return false;
        }

        
        if (champDescription.getText().trim().isEmpty()) {
            lblMessageErreur.setText("Veuillez saisir une description pour votre réclamation");
            return false;
        }

        
        if (champSujet.getText().trim().length() > 50) {
            lblMessageErreur.setText("Le sujet ne doit pas dépasser 50 caractères");
            return false;
        }

        
        if (champDescription.getText().trim().length() > 1000) {
            lblMessageErreur.setText("La description ne doit pas dépasser 1000 caractères");
            return false;
        }

        return true;
    }

    private void voirDetailTicket(TicketSupportResponseDTO ticket) {
        
        this.ticketCourant = ticket;

        
        lblTicketId.setText(String.valueOf(ticket.getId()));
        lblDateOuverture.setText(formatDate.format(ticket.getDateOuverture()));
        lblStatut.setText(ticket.getStatut());
        lblSujet.setText(ticket.getSujet());
        lblDescription.setText(ticket.getDescription());

        
        lblStatut.getStyleClass().removeAll("statut-en-cours", "statut-traite", "statut-clos");
        if (ticket.getStatut().equalsIgnoreCase("en cours")) {
            lblStatut.getStyleClass().add("statut-en-cours");
        } else if (ticket.getStatut().equalsIgnoreCase("traité")) {
            lblStatut.getStyleClass().add("statut-traite");
        } else if (ticket.getStatut().equalsIgnoreCase("clos")) {
            lblStatut.getStyleClass().add("statut-clos");
        }

        
        boolean peutEtreClos = ticket.getStatut().equalsIgnoreCase("traité");
        btnCloreTicket.setVisible(peutEtreClos);
        btnCloreTicket.setManaged(peutEtreClos);

        
        if (!tabPane.getTabs().contains(tabDetailTicket)) {
            tabPane.getTabs().add(tabDetailTicket);
        }

        
        tabPane.getSelectionModel().select(tabDetailTicket);
    }

    @FXML
    private void retourListeTickets() {
        
        tabPane.getTabs().remove(tabDetailTicket);

        
        tabPane.getSelectionModel().selectFirst();
    }

    @FXML
    private void cloreTicket() {
        if (ticketCourant == null) {
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Clore la réclamation");
        confirmation.setContentText("Êtes-vous sûr de vouloir clore cette réclamation ? Cette action est définitive.");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean success = ticketSupportService.cloreTicket(ticketCourant.getId());

                if (success) {
                    afficherAlerte(Alert.AlertType.INFORMATION, "Réclamation close",
                            "La réclamation a été close avec succès",
                            "Merci d'avoir utilisé notre service de support.");

                    
                    tabPane.getTabs().remove(tabDetailTicket);

                    
                    chargerTickets();

                    
                    tabPane.getSelectionModel().selectFirst();
                } else {
                    afficherAlerte(Alert.AlertType.ERROR, "Erreur",
                            "Impossible de clore la réclamation",
                            "Une erreur est survenue. Veuillez réessayer plus tard.");
                }
            }
        });
    }

    private void afficherAlerte(Alert.AlertType type, String titre, String entete, String contenu) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(entete);
        alert.setContentText(contenu);
        alert.showAndWait();
    }
}

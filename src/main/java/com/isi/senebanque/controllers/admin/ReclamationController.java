package com.isi.senebanque.controllers.admin;

import com.isi.senebanque.dtos.responses.ticketSupport.TicketSupportResponseDTO;
import com.isi.senebanque.services.ClientService;
import com.isi.senebanque.services.TicketSupportService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ResourceBundle;

public class ReclamationController implements Initializable {


    @FXML
    private TableView<TicketSupportResponseDTO> tableTickets;

    @FXML
    private TableColumn<TicketSupportResponseDTO, String> colId;

    @FXML
    private TableColumn<TicketSupportResponseDTO, String> colDateOuverture;

    @FXML
    private TableColumn<TicketSupportResponseDTO, String> colClient;

    @FXML
    private TableColumn<TicketSupportResponseDTO, String> colSujet;

    @FXML
    private TableColumn<TicketSupportResponseDTO, String> colStatut;

    @FXML
    private TableColumn<TicketSupportResponseDTO, Void> colActions;


    @FXML
    private TextField champRecherche;

    @FXML
    private ComboBox<String> comboStatut;


    @FXML
    private Label lblTicketId;

    @FXML
    private Label lblDateOuverture;

    @FXML
    private Label lblStatut;

    @FXML
    private Label lblClient;

    @FXML
    private Label lblSujet;

    @FXML
    private TextArea areaDescription;

    @FXML
    private VBox panneauDetail;

    @FXML
    private Button btnTraiter;

    @FXML
    private Button btnClore;


    private final TicketSupportService ticketSupportService = new TicketSupportService();
    private final ClientService clientService = new ClientService();


    private ObservableList<TicketSupportResponseDTO> listeTickets = FXCollections.observableArrayList();
    private final SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy");
    private TicketSupportResponseDTO ticketSelectionne;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        comboStatut.setItems(FXCollections.observableArrayList(
                "Tous", TicketSupportService.STATUT_EN_COURS,
                TicketSupportService.STATUT_TRAITE, TicketSupportService.STATUT_CLOS));
        comboStatut.setValue("Tous");


        configurerTableTickets();


        configurerEcouteurs();


        if (panneauDetail != null) {
            panneauDetail.setVisible(false);
        } else {
            System.err.println("Erreur: panneauDetail est null dans TicketSupportAdminController");
        }


        chargerTickets();
    }

    private void configurerTableTickets() {

        colId.setCellValueFactory(data ->
                new SimpleStringProperty(String.valueOf(data.getValue().getId())));

        colDateOuverture.setCellValueFactory(data ->
                new SimpleStringProperty(formatDate.format(data.getValue().getDateOuverture())));


        colClient.setCellValueFactory(data -> {
            TicketSupportResponseDTO ticket = data.getValue();
            Long clientId = ticket.getClientId();
            try {
                String nomClient = clientService.getClientNomComplet(clientId);
                return new SimpleStringProperty(nomClient);
            } catch (Exception e) {
                return new SimpleStringProperty("Client " + clientId);
            }
        });

        colSujet.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getSujet()));


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

                    if (statut.equalsIgnoreCase(TicketSupportService.STATUT_EN_COURS)) {
                        getStyleClass().add("statut-en-cours");
                    } else if (statut.equalsIgnoreCase(TicketSupportService.STATUT_TRAITE)) {
                        getStyleClass().add("statut-traite");
                    } else if (statut.equalsIgnoreCase(TicketSupportService.STATUT_CLOS)) {
                        getStyleClass().add("statut-clos");
                    }
                }
            }
        });


        colActions.setCellFactory(createActionCellFactory());


        tableTickets.setItems(listeTickets);


        tableTickets.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                afficherDetailTicket(newVal);
            } else if (panneauDetail != null) {
                panneauDetail.setVisible(false);
            }
        });
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
                            tableTickets.getSelectionModel().select(ticket);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);

                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btnVoir);
                        }
                    }
                };
            }
        };
    }

    private void configurerEcouteurs() {
        
        champRecherche.textProperty().addListener((obs, oldVal, newVal) -> {
            rechercherTickets();
        });

        
        comboStatut.valueProperty().addListener((obs, oldVal, newVal) -> {
            rechercherTickets();
        });
    }

    private void chargerTickets() {
        try {
            List<TicketSupportResponseDTO> tickets = ticketSupportService.getAllTickets();
            listeTickets.setAll(tickets);
            System.out.println("Nombre de tickets chargés: " + tickets.size());
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des tickets: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Une erreur est survenue lors du chargement des tickets.");
        }
    }

    private void rechercherTickets() {
        String terme = champRecherche.getText();
        String statut = comboStatut.getValue();

        if (statut.equals("Tous")) {
            statut = null;
        }

        try {
            List<TicketSupportResponseDTO> tickets = ticketSupportService.rechercherTickets(terme, statut);
            listeTickets.setAll(tickets);
            System.out.println("Recherche - Tickets trouvés: " + tickets.size());
        } catch (Exception e) {
            System.err.println("Erreur lors de la recherche de tickets: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void afficherDetailTicket(TicketSupportResponseDTO ticket) {
        ticketSelectionne = ticket;

        
        if (panneauDetail == null) {
            System.err.println("Erreur: panneauDetail est null dans afficherDetailTicket");
            return;
        }

        lblTicketId.setText(String.valueOf(ticket.getId()));
        lblDateOuverture.setText(formatDate.format(ticket.getDateOuverture()));
        lblStatut.setText(ticket.getStatut());

        
        try {
            String nomClient = clientService.getClientNomComplet(ticket.getClientId());
            lblClient.setText(nomClient);
        } catch (Exception e) {
            lblClient.setText("Client " + ticket.getClientId());
        }

        lblSujet.setText(ticket.getSujet());
        areaDescription.setText(ticket.getDescription());

        
        lblStatut.getStyleClass().removeAll("statut-en-cours", "statut-traite", "statut-clos");
        if (ticket.getStatut().equalsIgnoreCase(TicketSupportService.STATUT_EN_COURS)) {
            lblStatut.getStyleClass().add("statut-en-cours");
            btnTraiter.setVisible(true);
            btnTraiter.setManaged(true);
            btnClore.setVisible(false);
            btnClore.setManaged(false);
        } else if (ticket.getStatut().equalsIgnoreCase(TicketSupportService.STATUT_TRAITE)) {
            lblStatut.getStyleClass().add("statut-traite");
            btnTraiter.setVisible(false);
            btnTraiter.setManaged(false);
            btnClore.setVisible(true);
            btnClore.setManaged(true);
        } else {
            lblStatut.getStyleClass().add("statut-clos");
            btnTraiter.setVisible(false);
            btnTraiter.setManaged(false);
            btnClore.setVisible(false);
            btnClore.setManaged(false);
        }

        panneauDetail.setVisible(true);
    }

    @FXML
    private void rafraichir() {
        chargerTickets();
        if (panneauDetail != null) {
            panneauDetail.setVisible(false);
        }
        tableTickets.getSelectionModel().clearSelection();
    }

    @FXML
    private void traiterTicket() {
        if (ticketSelectionne == null) {
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Marquer comme traité");
        confirmation.setContentText("Êtes-vous sûr de vouloir marquer cette réclamation comme traitée ?");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean success = ticketSupportService.marquerTraite(ticketSelectionne.getId());

                if (success) {
                    showAlert(Alert.AlertType.INFORMATION, "Succès",
                            "La réclamation a été marquée comme traitée.");
                    rafraichir();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Erreur",
                            "Une erreur est survenue lors de la mise à jour du statut.");
                }
            }
        });
    }

    @FXML
    private void cloreTicket() {
        if (ticketSelectionne == null) {
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Clore la réclamation");
        confirmation.setContentText("Êtes-vous sûr de vouloir clore cette réclamation ?");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean success = ticketSupportService.cloreTicket(ticketSelectionne.getId());

                if (success) {
                    showAlert(Alert.AlertType.INFORMATION, "Succès",
                            "La réclamation a été close avec succès.");
                    rafraichir();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Erreur",
                            "Une erreur est survenue lors de la mise à jour du statut.");
                }
            }
        });
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

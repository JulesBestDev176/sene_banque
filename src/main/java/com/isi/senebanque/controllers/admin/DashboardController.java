package com.isi.senebanque.controllers.admin;

import com.isi.senebanque.dtos.responses.ticketSupport.TicketSupportResponseDTO;
import com.isi.senebanque.dtos.responses.transaction.TransactionResponseDTO;
import com.isi.senebanque.services.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {
    @FXML
    private Label totalClients;
    @FXML
    private Label comptesActifs;
    @FXML
    private Label transactionsJour;
    @FXML
    private Label demandesCredit;
    @FXML
    private Label demandesCreditEnAttente;

    @FXML
    private TableView<TransactionResponseDTO> tableOperationsSuspectes;
    @FXML
    private TableColumn<TransactionResponseDTO, String> colDate;
    @FXML
    private TableColumn<TransactionResponseDTO, String> colClient;
    @FXML
    private TableColumn<TransactionResponseDTO, String> colType;
    @FXML
    private TableColumn<TransactionResponseDTO, Double> colMontant;
    @FXML
    private TableColumn<TransactionResponseDTO, String> colStatut;
    @FXML
    private VBox listeTickets;


    private final ClientService clientService = new ClientService();
    private final CompteService compteService = new CompteService();
    private final TransactionService transactionService = new TransactionService();
    private final CreditService creditService = new CreditService();
    private final TicketSupportService ticketSupportService = new TicketSupportService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        long nombreClients = clientService.getNombreClients();
        totalClients.setText(String.valueOf(nombreClients));

        long nombreComptesActif = compteService.getNombreComptesActifs();
        comptesActifs.setText(String.valueOf(nombreComptesActif));

        long nombreTransactionsJour = transactionService.getNombreTransactionsDuJour();
        transactionsJour.setText(String.valueOf(nombreTransactionsJour));

        long nombreDemandesCredit = creditService.getNombreTotalCredits();
        demandesCredit.setText(String.valueOf(nombreDemandesCredit));

        long nombreDemandesCreditEnAttente = creditService.getNombreCreditsEnAttente();
        demandesCreditEnAttente.setText(String.valueOf(nombreDemandesCreditEnAttente));

        configurerTableOperationsSuspectes();
        afficherTicketsRecents();
    }

    private void configurerTableOperationsSuspectes() {
        colDate.setCellValueFactory(new PropertyValueFactory<>("dateTransaction"));
        colClient.setCellValueFactory(new PropertyValueFactory<>("compteSourceId"));
        colType.setCellValueFactory(new PropertyValueFactory<>("typeTransaction"));
        colMontant.setCellValueFactory(new PropertyValueFactory<>("montant"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));

        List<TransactionResponseDTO> transactionsSuspectes = transactionService.getSuspectTransactions();
        ObservableList<TransactionResponseDTO> data = FXCollections.observableArrayList(transactionsSuspectes);

        tableOperationsSuspectes.setItems(data);
    }
    private void afficherTicketsRecents() {
        List<TicketSupportResponseDTO> ticketsRecents = ticketSupportService.getTroisDerniersTickets();
        listeTickets.getChildren().clear();
        for (TicketSupportResponseDTO ticket : ticketsRecents) {
            VBox ticketItem = creerTicketItem(ticket);
            listeTickets.getChildren().add(ticketItem);
        }
    }

    private VBox creerTicketItem(TicketSupportResponseDTO ticket) {
        VBox ticketItem = new VBox();
        ticketItem.getStyleClass().add("ticket-item");
        switch (ticket.getStatut().toLowerCase()) {
            case "en attente admin":
                ticketItem.getStyleClass().add("ticket-urgent");
                break;
            case "en cours":
                ticketItem.getStyleClass().add("ticket-moyen");
                break;
            case "en attente client":
                ticketItem.getStyleClass().add("ticket-faible");
                break;
            default:
                ticketItem.getStyleClass().add("ticket-faible");
                break;
        }


        HBox header = new HBox();
        header.getStyleClass().add("ticket-header");

        Label titre = new Label("#" + ticket.getId() + " - " + ticket.getSujet());
        titre.getStyleClass().add("ticket-titre");

        Label statut = new Label(ticket.getStatut());
        statut.getStyleClass().add("ticket-priorite");

        header.getChildren().addAll(titre, statut);

        Label client = new Label("Client: " + ticket.getClientId());
        client.getStyleClass().add("ticket-client");

        HBox actions = new HBox();
        Hyperlink repondre = new Hyperlink("Répondre");
        repondre.getStyleClass().add("ticket-action");

        actions.getChildren().add(repondre);

        ticketItem.getChildren().addAll(header, client, actions);

        return ticketItem;
    }


}

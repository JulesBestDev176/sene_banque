package com.isi.senebanque.controllers.client;

import com.isi.senebanque.models.Client;
import com.isi.senebanque.models.Model;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ResourceBundle;

public class ClientController implements Initializable {
    public BorderPane page_client;
    public Client userconnecte;



    @Override
    public void initialize(URL location, ResourceBundle resources) {

        Model.getInstance().getViewFactory().getMenuAdminSelect().addListener((observable, oldValue, newValue) -> {
            switch (newValue) {
                case "Dashboard" -> page_client.setCenter(Model.getInstance().getViewFactory().getDashboardClientView());
                case "Profile" -> page_client.setCenter(Model.getInstance().getViewFactory().getProfileClientView());
                case "Comptes" -> page_client.setCenter(Model.getInstance().getViewFactory().getComptesClientView());
                case "Credits" -> page_client.setCenter(Model.getInstance().getViewFactory().getCreditClientView());
                case "Reclamations" -> page_client.setCenter(Model.getInstance().getViewFactory().getReclamationClientView());
                case "Transactions" -> page_client.setCenter(Model.getInstance().getViewFactory().getTransactionsClientView());
                default -> page_client.setCenter(Model.getInstance().getViewFactory().getDashboardClientView());
            }
        });
    }
}

package com.isi.senebanque.controllers.admin;

import com.isi.senebanque.models.Admin;
import com.isi.senebanque.models.Model;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ResourceBundle;

public class AdminController implements Initializable {
    public BorderPane page_admin;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        Model.getInstance().getViewFactory().getMenuAdminSelect().addListener((observable, oldValue, newValue) -> {
            switch (newValue) {
                case "Dashboard" -> page_admin.setCenter(Model.getInstance().getViewFactory().getDashboardAdminView());
                case "Profile" -> page_admin.setCenter(Model.getInstance().getViewFactory().getProfileAdminView());
                case "Clients" -> page_admin.setCenter(Model.getInstance().getViewFactory().getClientsAdminView());
                case "Comptes" -> page_admin.setCenter(Model.getInstance().getViewFactory().getComptesAdminView());
                case "Reclamations" -> page_admin.setCenter(Model.getInstance().getViewFactory().getReclamationAdminView());
                case "Cartes" -> page_admin.setCenter(Model.getInstance().getViewFactory().getCarteAdminView()  );
                case "Credits" -> page_admin.setCenter(Model.getInstance().getViewFactory().getCreditAdminView());
                case "Virements" -> page_admin.setCenter(Model.getInstance().getViewFactory().getVirementAdminView());
                case "Transactions" -> page_admin.setCenter(Model.getInstance().getViewFactory().getTransactionsAdminView());
                default -> page_admin.setCenter(Model.getInstance().getViewFactory().getDashboardAdminView());
            }
        });
    }
}

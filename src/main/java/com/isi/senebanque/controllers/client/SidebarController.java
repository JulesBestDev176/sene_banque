package com.isi.senebanque.controllers.client;

import com.isi.senebanque.SeneBanque;
import com.isi.senebanque.controllers.ConnexionController;
import com.isi.senebanque.models.Admin;
import com.isi.senebanque.models.Client;
import com.isi.senebanque.models.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class SidebarController implements Initializable {
    @FXML
    private Label nomUtilisateur;
    @FXML
    private Button boutonTableauBord;
    @FXML
    private Button boutonComptes;
    @FXML
    private Button boutonTransactions;
    @FXML
    private Button boutonCredits;
    @FXML
    private Button boutonReclamations;
    @FXML
    private Button boutonDeconnexion;
    @FXML
    private Button boutonProfil;


    Client clientConnecte = Model.getInstance().getClientConnecte();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addListener();
        if (clientConnecte != null) {
            nomUtilisateur.setText(clientConnecte.getPrenom() + " " + clientConnecte.getNom());
        }

    }

    private void onDashboard(MouseEvent event) {
        System.out.println("Dashboard");
        Model.getInstance().getViewFactory().getMenuAdminSelect().set("Dashboard");
    }
    private void onTransactions(MouseEvent event) {
        System.out.println("Transactions");
        Model.getInstance().getViewFactory().getMenuAdminSelect().set("Transactions");
    }

    private void onProfile(MouseEvent event) {
        System.out.println("Profile");
        Model.getInstance().getViewFactory().getMenuAdminSelect().set("Profile");
    }

    private void onCompte(MouseEvent event) {
        System.out.println("Comptes");
        Model.getInstance().getViewFactory().getMenuAdminSelect().set("Comptes");
    }

    private void onReclamations(MouseEvent event) {
        System.out.println("Reclamations");
        Model.getInstance().getViewFactory().getMenuAdminSelect().set("Reclamations");
    }




    private void onCredits(MouseEvent event) {
        System.out.println("Credits");
        Model.getInstance().getViewFactory().getMenuAdminSelect().set("Credits");
    }



    public void btnDeconnexionOnAction(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    private void addListener() {
        if (boutonTableauBord != null) {
            boutonTableauBord.setOnMouseClicked(this::onDashboard);
        }
        if (boutonComptes != null) {
            boutonComptes.setOnMouseClicked(this::onCompte);
        }
        if (boutonTransactions != null) {
            boutonTransactions.setOnMouseClicked(this::onTransactions);
        }

        if (boutonReclamations != null) {
            boutonReclamations.setOnMouseClicked(this::onReclamations);
        }
        if (boutonProfil != null) {
            boutonProfil.setOnMouseClicked(this::onProfile);
        }
        if (boutonCredits != null) {
            boutonCredits.setOnMouseClicked(this::onCredits);
        }

    }


}

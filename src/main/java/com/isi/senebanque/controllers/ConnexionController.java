package com.isi.senebanque.controllers;

import com.isi.senebanque.SeneBanque;
import com.isi.senebanque.models.Admin;
import com.isi.senebanque.models.Client;
import com.isi.senebanque.models.Model;
import com.isi.senebanque.services.AuthService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ConnexionController implements Initializable {
    @FXML
    private TextField champUtilisateur;
    @FXML
    private PasswordField champMotDePasse;
    @FXML
    private Button boutonConnexion;
    @FXML
    private Button boutonFermer;
    @FXML
    private Label lblErrorMessage;


    private final AuthService authService;
    public ConnexionController() {
        this.authService = new AuthService();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        boutonConnexion.setOnAction(event -> connexion());
    }

    public void connexion() {
        String username = champUtilisateur.getText();
        String password = champMotDePasse.getText();

        if (username.isBlank() || password.isBlank()) {
            lblErrorMessage.setText("Veuillez remplir tous les champs");
            return;
        }

        Admin admin = authService.authenticateUser(username, password);
        if (admin != null) {
            Stage stage = (Stage) lblErrorMessage.getScene().getWindow();
            Model.getInstance().getViewFactory().fermerStage(stage);
            Model.getInstance().getViewFactory().afficherPageAdmin();
            return;
        }

        Client client = authService.authenticateClient(username, password);
        if (client != null) {
            Stage stage = (Stage) lblErrorMessage.getScene().getWindow();
            Model.getInstance().getViewFactory().fermerStage(stage);
            Model.getInstance().getViewFactory().afficherPageClient();
            return;
        }

        lblErrorMessage.setText("Nom d'utilisateur ou mot de passe incorrect");
    }

    public void btnQuitterAction(ActionEvent event) {
        Stage stage = (Stage) boutonFermer.getScene().getWindow();
        stage.close();
    }
}

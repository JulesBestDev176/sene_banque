package com.isi.senebanque.views;

import com.isi.senebanque.controllers.admin.AdminController;
import com.isi.senebanque.controllers.client.ClientController;
import com.isi.senebanque.models.Admin;
import com.isi.senebanque.models.Client;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;

public class ViewFactory {

    private StringProperty menuAdminSelect;
    private BorderPane dashboardAdminView;
    private BorderPane clientsAdminView;
    private BorderPane comptesAdminView;
    private BorderPane transactionsAdminView;
    private BorderPane virementAdminView;
    private BorderPane creditAdminView;
    private BorderPane carteAdminView;
    private BorderPane profileAdminView;
    private BorderPane reclamationAdminView;
    private StringProperty menuClientSelect;
    private BorderPane dashboardClientView;
    private BorderPane compteClientView;
    private BorderPane transactionClientView;
    private BorderPane creditClientView;
    private BorderPane profileClientView;
    private BorderPane reclamationClientView;



    public ViewFactory() {
        this.menuAdminSelect = new SimpleStringProperty("");
        this.menuClientSelect = new SimpleStringProperty("");
    }


    public StringProperty getMenuAdminSelect() {
        return menuAdminSelect;
    }

    public BorderPane getDashboardAdminView() {
        if (dashboardAdminView == null) {
            try {
                URL dashboardResource = getClass().getResource("/com/isi/senebanque/admin/dashboard.fxml");
                if (dashboardResource == null) {
                    System.err.println("ERREUR : Impossible de trouver le fichier Dashboard.fxml");
                    return null;
                }
                FXMLLoader loader = new FXMLLoader(dashboardResource);
                dashboardAdminView = loader.load();
            } catch (IOException e) {
                System.err.println("Erreur de chargement du Dashboard :");
                e.printStackTrace();
            }
        }
        return dashboardAdminView;
    }
    public BorderPane getClientsAdminView() {
        if (clientsAdminView == null) {
            try {
                URL clientsResource = getClass().getResource("/com/isi/senebanque/admin/clients.fxml");
                if (clientsResource == null) {
                    System.err.println("ERREUR : Impossible de trouver le fichier clients.fxml");
                    return null;
                }
                FXMLLoader loader = new FXMLLoader(clientsResource);
                clientsAdminView = loader.load();
            } catch (IOException e) {
                System.err.println("Erreur de chargement du clients :");
                e.printStackTrace();
            }
        }
        return clientsAdminView;
    }
    public BorderPane getComptesAdminView() {
        if (comptesAdminView == null) {
            try {
                URL comptesResource = getClass().getResource("/com/isi/senebanque/admin/comptes.fxml");
                if (comptesResource == null) {
                    System.err.println("ERREUR : Impossible de trouver le fichier comptes.fxml");
                    return null;
                }
                FXMLLoader loader = new FXMLLoader(comptesResource);
                comptesAdminView = loader.load();
            } catch (IOException e) {
                System.err.println("Erreur de chargement du comptes :");
                e.printStackTrace();
            }
        }
        return comptesAdminView;
    }
    public BorderPane getTransactionsAdminView() {
        if (transactionsAdminView == null) {
            try {
                URL transactionsResource = getClass().getResource("/com/isi/senebanque/admin/transactions.fxml");
                if (transactionsResource == null) {
                    System.err.println("ERREUR : Impossible de trouver le fichier transactions.fxml");
                    return null;
                }
                FXMLLoader loader = new FXMLLoader(transactionsResource);
                transactionsAdminView = loader.load();
            } catch (IOException e) {
                System.err.println("Erreur de chargement du transactions :");
                e.printStackTrace();
            }
        }
        return transactionsAdminView;
    }
    public BorderPane getVirementAdminView() {
        if (virementAdminView == null) {
            try {
                URL virementsResource = getClass().getResource("/com/isi/senebanque/admin/virements.fxml");
                if (virementsResource == null) {
                    System.err.println("ERREUR : Impossible de trouver le fichier virements.fxml");
                    return null;
                }
                FXMLLoader loader = new FXMLLoader(virementsResource);
                virementAdminView = loader.load();
            } catch (IOException e) {
                System.err.println("Erreur de chargement du virements :");
                e.printStackTrace();
            }
        }
        return virementAdminView;
    }
    public BorderPane getCreditAdminView() {
        if (creditAdminView == null) {
            try {
                URL creditsResource = getClass().getResource("/com/isi/senebanque/admin/credit.fxml");
                if (creditsResource == null) {
                    System.err.println("ERREUR : Impossible de trouver le fichier credits.fxml");
                    return null;
                }
                FXMLLoader loader = new FXMLLoader(creditsResource);
                creditAdminView = loader.load();
            } catch (IOException e) {
                System.err.println("Erreur de chargement du credits :");
                e.printStackTrace();
            }
        }
        return creditAdminView;
    }
    public BorderPane getCarteAdminView() {
        if (carteAdminView == null) {
            try {
                URL cartesResource = getClass().getResource("/com/isi/senebanque/admin/CarteBancaire.fxml");
                if (cartesResource == null) {
                    System.err.println("ERREUR : Impossible de trouver le fichier cartes.fxml");
                    return null;
                }
                FXMLLoader loader = new FXMLLoader(cartesResource);
                carteAdminView = loader.load();
            } catch (IOException e) {
                System.err.println("Erreur de chargement du cartes :");
                e.printStackTrace();
            }
        }
        return carteAdminView;
    }
    public BorderPane getProfileAdminView() {
        if (profileAdminView == null) {
            try {
                URL profilesResource = getClass().getResource("/com/isi/senebanque/admin/profile.fxml");
                if (profilesResource == null) {
                    System.err.println("ERREUR : Impossible de trouver le fichier profile.fxml");
                    return null;
                }
                FXMLLoader loader = new FXMLLoader(profilesResource);
                profileAdminView = loader.load();
            } catch (IOException e) {
                System.err.println("Erreur de chargement du profiles :");
                e.printStackTrace();
            }
        }
        return profileAdminView;
    }
    public BorderPane getReclamationAdminView() {
        if (reclamationAdminView == null) {
            try {
                URL reclamationsResource = getClass().getResource("/com/isi/senebanque/admin/reclamation.fxml");
                if (reclamationsResource == null) {
                    System.err.println("ERREUR : Impossible de trouver le fichier reclamation.fxml");
                    return null;
                }
                FXMLLoader loader = new FXMLLoader(reclamationsResource);
                reclamationAdminView = loader.load();
            } catch (IOException e) {
                System.err.println("Erreur de chargement du reclamations :");
                e.printStackTrace();
            }
        }
        return reclamationAdminView;
    }


    public StringProperty getMenuClientSelect() {
        return menuClientSelect;
    }
    public BorderPane getDashboardClientView() {
        if (dashboardClientView == null) {
            try {
                URL dashboardResource = getClass().getResource("/com/isi/senebanque/client/dashboard.fxml");
                if (dashboardResource == null) {
                    System.err.println("ERREUR : Impossible de trouver le fichier Dashboard.fxml");
                    return null;
                }
                FXMLLoader loader = new FXMLLoader(dashboardResource);
                dashboardClientView = loader.load();
            } catch (IOException e) {
                System.err.println("Erreur de chargement du Dashboard :");
                e.printStackTrace();
            }
        }
        return dashboardClientView;
    }
    public BorderPane getTransactionsClientView() {
        if (transactionClientView == null) {
            try {
                URL transactionsResource = getClass().getResource("/com/isi/senebanque/client/transactions.fxml");
                if (transactionsResource == null) {
                    System.err.println("ERREUR : Impossible de trouver le fichier transactions.fxml");
                    return null;
                }
                FXMLLoader loader = new FXMLLoader(transactionsResource);
                transactionClientView = loader.load();
            } catch (IOException e) {
                System.err.println("Erreur de chargement du transactions :");
                e.printStackTrace();
            }
        }
        return transactionClientView;
    }
    public BorderPane getComptesClientView() {
        if (compteClientView == null) {
            try {
                URL comptesResource = getClass().getResource("/com/isi/senebanque/client/comptes.fxml");
                if (comptesResource == null) {
                    System.err.println("ERREUR : Impossible de trouver le fichier comptes.fxml");
                    return null;
                }
                FXMLLoader loader = new FXMLLoader(comptesResource);
                compteClientView = loader.load();
            } catch (IOException e) {
                System.err.println("Erreur de chargement du comptes :");
                e.printStackTrace();
            }
        }
        return compteClientView;
    }
    public BorderPane getCreditClientView() {
        if (creditClientView == null) {
            try {
                URL creditsResource = getClass().getResource("/com/isi/senebanque/client/credits.fxml");
                if (creditsResource == null) {
                    System.err.println("ERREUR : Impossible de trouver le fichier credits.fxml");
                    return null;
                }
                FXMLLoader loader = new FXMLLoader(creditsResource);
                creditClientView = loader.load();
            } catch (IOException e) {
                System.err.println("Erreur de chargement du credits :");
                e.printStackTrace();
            }
        }
        return creditClientView;
    }
    public BorderPane getProfileClientView() {
        if (profileClientView == null) {
            try {
                URL profilesResource = getClass().getResource("/com/isi/senebanque/client/profile.fxml");
                if (profilesResource == null) {
                    System.err.println("ERREUR : Impossible de trouver le fichier profile.fxml");
                    return null;
                }
                FXMLLoader loader = new FXMLLoader(profilesResource);
                profileClientView = loader.load();
            } catch (IOException e) {
                System.err.println("Erreur de chargement du profiles :");
                e.printStackTrace();
            }
        }
        return profileClientView;
    }
    public BorderPane getReclamationClientView() {
        if (reclamationClientView == null) {
            try {
                URL reclamationsResource = getClass().getResource("/com/isi/senebanque/client/reclamation.fxml");
                if (reclamationsResource == null) {
                    System.err.println("ERREUR : Impossible de trouver le fichier reclamation.fxml");
                    return null;
                }
                FXMLLoader loader = new FXMLLoader(reclamationsResource);
                reclamationClientView = loader.load();
            } catch (IOException e) {
                System.err.println("Erreur de chargement du reclamations :");
                e.printStackTrace();
            }
        }
        return reclamationClientView;
    }




    public void afficherPageConnexion() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/isi/senebanque/connexion.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("SENE BANQUE - Connexion");
            stage.setScene(new Scene(root, 360, 640));
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.setOnCloseRequest(event -> event.consume());
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void afficherPageAdmin() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/isi/senebanque/admin/admin.fxml"));
        AdminController utilisateurController = new AdminController();
        loader.setController(utilisateurController);
        Scene scene = null;
        try{
            scene = new Scene(loader.load());
        }catch(Exception e) {
            e.printStackTrace();
        }
        creerStage(scene);
    }

    public void afficherPageClient() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/isi/senebanque/client/client.fxml"));
        ClientController utilisateurController = new ClientController();
        loader.setController(utilisateurController);
        Scene scene = null;
        try{
            scene = new Scene(loader.load());
        }catch(Exception e) {
            e.printStackTrace();
        }
        creerStage(scene);
    }

    private void creerStage(Scene scene) {
        Stage stage = new Stage();
        stage.setTitle("SENE BANQUE");
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.setResizable(false);
        stage.centerOnScreen();
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setOnCloseRequest(event -> event.consume());
        stage.show();
    }
    public void fermerStage(Stage stage) {
        stage.close();
    }
}

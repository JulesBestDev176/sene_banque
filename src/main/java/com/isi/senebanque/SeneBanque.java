package com.isi.senebanque;

import com.isi.senebanque.models.Admin;
import com.isi.senebanque.models.Client;
import com.isi.senebanque.models.Model;
import com.isi.senebanque.utils.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class SeneBanque extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        creerAdmin();
        Model.getInstance().getViewFactory().afficherPageConnexion();
    }




    private void creerAdmin() {
        EntityManager admin = JpaUtil.getEntityManager();
        EntityTransaction transaction = admin.getTransaction();

        try {
            transaction.begin();
            TypedQuery<Long> requeteComptage = admin.createQuery(
                    "SELECT COUNT(a) FROM Admin a WHERE a.username = :nomUtilisateur",
                    Long.class
            );
            requeteComptage.setParameter("nomUtilisateur", "fall.souleymane");

            
            Long nombreUtilisateurs = requeteComptage.getSingleResult();
            if (nombreUtilisateurs == 0) {
                Admin utilisateurAdmin = new Admin();
                utilisateurAdmin.setUsername("fall.souleymane");
                utilisateurAdmin.setNom("fall");
                utilisateurAdmin.setPrenom("souleymane");
                utilisateurAdmin.setEmail("souleymanefall176@gmail.com");
                utilisateurAdmin.setPassword("okok");
                admin.persist(utilisateurAdmin);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            if (admin.isOpen()) {
                admin.close();
            }
        }
    }

    @Override
    public void stop() {
        JpaUtil.close();
    }

    public static void main(String[] args) {
        launch();
    }
}

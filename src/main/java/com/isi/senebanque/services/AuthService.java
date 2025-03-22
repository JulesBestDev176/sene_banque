package com.isi.senebanque.services;

import com.isi.senebanque.SeneBanque;
import com.isi.senebanque.models.Client;
import com.isi.senebanque.models.Admin;
import com.isi.senebanque.models.Model;
import com.isi.senebanque.utils.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

public class AuthService {

    public Admin authenticateUser(String username, String password) {
        EntityManager entityManager = JpaUtil.getEntityManager();
        try {
            TypedQuery<Admin> query = entityManager.createQuery(
                    "SELECT a FROM Admin a WHERE a.username = :username", Admin.class);
            query.setParameter("username", username);
            Admin admin = query.getSingleResult();

            if (admin != null && admin.getPassword().equals(password)) {
                Model.getInstance().setAdminConnecte(admin);
                return admin;
            }
        } catch (NoResultException e) {
            e.printStackTrace();
        } finally {
            entityManager.close();
        }
        return null;
    }

    public Client authenticateClient(String username, String password) {
        EntityManager entityManager = JpaUtil.getEntityManager();
        try {
            TypedQuery<Client> query = entityManager.createQuery(
                    "SELECT c FROM Client c WHERE c.username = :username AND c.statut = 'Actif'", Client.class);
            query.setParameter("username", username);
            Client client = query.getSingleResult();
            if (client != null && client.getPassword().equals(password)) {
                Model.getInstance().setClientConnecte(client);
                return client;
            }
        } catch (NoResultException e) {
            e.printStackTrace();
        } finally {
            entityManager.close();
        }
        return null;
    }
}

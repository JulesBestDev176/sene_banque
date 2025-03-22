package com.isi.senebanque.models;

import com.isi.senebanque.views.ViewFactory;

public class Model {
    private static Model model;
    private final ViewFactory viewFactory;
    private Admin adminConnecte;
    private Client clientConnecte;
    private Model() {
        this.viewFactory = new ViewFactory();
    }

    public static synchronized Model getInstance() {
        if (model == null) {
            model = new Model();
        }
        return model;
    }

    public Admin getAdminConnecte() {
        return adminConnecte;
    }

    public void setAdminConnecte(Admin admin) {
        this.adminConnecte = admin;
    }

    public Client getClientConnecte() {
        return clientConnecte;
    }

    public void setClientConnecte(Client client) {
        this.clientConnecte = client;
    }

    public void deconnecter() {
        this.adminConnecte = null;
        this.clientConnecte = null;
    }

    public ViewFactory getViewFactory() {
        return viewFactory;
    }
}

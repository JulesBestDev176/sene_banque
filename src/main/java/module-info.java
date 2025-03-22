module com.isi.senebanque {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires jakarta.persistence;
    requires static lombok;
    requires de.jensd.fx.glyphs.fontawesome;
    requires jdk.jshell;
    requires org.hibernate.orm.core;
    requires java.mail;
    requires java.management;


    opens com.isi.senebanque to javafx.fxml;
    opens com.isi.senebanque.models to org.hibernate.orm.core;
    opens com.isi.senebanque.dtos.responses.client to javafx.base, javafx.fxml;
    opens com.isi.senebanque.dtos.responses.compte to javafx.base, javafx.fxml;
    opens com.isi.senebanque.dtos.responses.transaction to javafx.base, javafx.fxml;
    //opens com.isi.senebanque.dtos.response.transaction to javafx.base;

    exports com.isi.senebanque;
    exports com.isi.senebanque.controllers;
    opens com.isi.senebanque.controllers to javafx.fxml;
    exports com.isi.senebanque.controllers.admin;
    opens com.isi.senebanque.controllers.admin to javafx.fxml;
    exports com.isi.senebanque.controllers.client;
    exports com.isi.senebanque.models;
    exports com.isi.senebanque.views;
    opens com.isi.senebanque.controllers.client to javafx.fxml;

}

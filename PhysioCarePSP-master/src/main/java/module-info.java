module com.matias.physiocarepsp {
    requires javafx.controls;
    requires javafx.fxml;
    requires kernel;
    requires layout;
    requires com.google.api.client;
    requires com.google.api.client.json.gson;
    requires com.google.api.client.auth;
    requires google.api.client;
    requires com.google.api.services.gmail;
    requires com.google.api.client.extensions.jetty.auth;
    requires com.google.api.client.extensions.java6.auth;
    requires jakarta.mail;
    requires jdk.httpserver;
    requires com.google.gson;
    requires java.desktop;

    opens com.matias.physiocarepsp to javafx.fxml;
    exports com.matias.physiocarepsp;
    exports com.matias.physiocarepsp.utils;
    opens com.matias.physiocarepsp.utils to javafx.fxml;
    opens com.matias.physiocarepsp.models.Patient;
    opens com.matias.physiocarepsp.models.Physio;
    opens com.matias.physiocarepsp.models.Appointment;
    opens com.matias.physiocarepsp.models.Record;

    opens com.matias.physiocarepsp.models.Auth to com.google.gson;
    opens com.matias.physiocarepsp.models to com.google.gson;

    // Add this line to fix the issue
    opens com.matias.physiocarepsp.viewscontroller to javafx.fxml;
}
package com.matias.physiocarepsp;

/**
 * Main application class for the PhysioCare application.
 * This class initializes and starts the JavaFX application.
 */
import com.matias.physiocarepsp.viewscontroller.LoginViewController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {

    /**
     * Starts the JavaFX application by loading the login view.
     *
     * @param stage the primary stage for this application
     * @throws IOException if the FXML file cannot be loaded
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(LoginViewController.class.getResource("/com/matias/physiocarepsp/fxmlviews/login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 350, 500);
        stage.setTitle("PhysioCare");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * The main entry point for the application.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        launch();
    }
}
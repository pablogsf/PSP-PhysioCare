package com.matias.physiocarepsp.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

/**
 * Utility class for common UI-related operations.
 */
public class Utils {

    /**
     * Displays an alert dialog with the specified title, content, and type.
     *
     * @param title   the title of the alert
     * @param content the content/message of the alert
     * @param type    the type of the alert (1 for information, 2 for error)
     */
    public static void showAlert(String title, String content, int type) {
        Alert alert = null;
        if (type == 1) {
            alert = new Alert(Alert.AlertType.INFORMATION);
        } else if (type == 2) {
            alert = new Alert(Alert.AlertType.ERROR);
        }
        assert alert != null;
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Switches the current view to a new FXML view.
     *
     * @param source   the source node triggering the view switch
     * @param fxmlFile the path to the FXML file for the new view
     * @param title    the title of the new view
     */
    public static void switchView(Node source, String fxmlFile, String title) {
        Stage stage = (Stage) source.getScene().getWindow();
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Utils.class.getResource(fxmlFile));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setTitle(title);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Could not load the view: " + e.getMessage(), 2);
        }
    }
}
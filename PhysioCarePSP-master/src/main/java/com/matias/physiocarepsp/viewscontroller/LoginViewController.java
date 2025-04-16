package com.matias.physiocarepsp.viewscontroller;

import com.matias.physiocarepsp.utils.ServiceUtils;
import com.matias.physiocarepsp.utils.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;

/**
 * Controller for the Login view of the application.
 * Handles user authentication and navigation to the main view upon successful login.
 */
public class LoginViewController {

    @FXML
    private TextField txtUsername;

    @FXML
    private TextField txtPassword;

    /**
     * Handles the login action when the user submits their credentials.
     *
     * @param actionEvent the event triggered by the login button
     */
    public void onLogin(ActionEvent actionEvent) {
        String username = txtUsername.getText();
        String password = txtPassword.getText();

        // Validate that all fields are filled
        if (username.isEmpty() || password.isEmpty()) {
            Utils.showAlert("ERROR", "Error: all fields must be filled to continue.", 2);
        } else {
            // Attempt login and navigate to the main view if successful
            if (ServiceUtils.login(username, password)) {
                Node source = (Node) actionEvent.getSource();
                String fxmlFile = "/com/matias/physiocarepsp/fxmlviews/first-view.fxml";
                String title = "Welcome | PhysioCare";
                Utils.switchView(source, fxmlFile, title);
            } else {
                // Show error if login fails
                Utils.showAlert("ERROR", "Error: login failed. User or password incorrect.", 2);
            }
        }
    }
}
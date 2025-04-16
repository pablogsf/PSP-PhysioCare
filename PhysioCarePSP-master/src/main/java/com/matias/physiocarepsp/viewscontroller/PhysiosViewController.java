package com.matias.physiocarepsp.viewscontroller;

import com.google.gson.Gson;
import com.matias.physiocarepsp.models.Physio.Physio;
import com.matias.physiocarepsp.models.Physio.PhysioListResponse;
import com.matias.physiocarepsp.models.Physio.PhysioResponse;
import com.matias.physiocarepsp.utils.ServiceUtils;
import com.matias.physiocarepsp.utils.Utils;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

import static com.matias.physiocarepsp.utils.Utils.showAlert;

/**
 * Controller for the Physios view of the application.
 * Handles CRUD operations for physios and manages the UI interactions.
 */
public class PhysiosViewController implements Initializable {

    public TextField txtSearch;
    @FXML
    private TextField txtName;
    @FXML
    private TextField txtSurname;
    @FXML
    private TextField txtLicenseNumber;
    @FXML
    private TextField txtEmail;
    @FXML
    private ListView<Physio> lsPhysios;
    @FXML
    private Button btnDelete;
    @FXML
    private Button btnAdd;
    @FXML
    private Button btnUpdate;
    @FXML
    private ComboBox<String> cbSpecialization;
    private ObservableList<Physio> allPhysios = FXCollections.observableArrayList();

    private final Gson gson = new Gson();

    /**
     * Initializes the controller and sets up the physio list selection listener.
     *
     * @param url            the location used to resolve relative paths
     * @param resourceBundle the resources used to localize the root object
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cbSpecialization.getItems().addAll("Sports", "Neurological", "Pediatric", "Geriatric", "Oncological");

        lsPhysios.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<Physio>() {
                    @Override
                    public void changed(ObservableValue<? extends Physio> observableValue, Physio oldValue, Physio newValue) {
                        if (newValue != null) {
                            populateFields(newValue);
                        } else {
                            clearFields();
                        }
                    }
                });
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> filterPhysios(newValue));

        getPhysios();
    }

    /**
     * Filters the list of physios based on the search text entered by the user.
     *
     * @param searchText the text to filter the physios
     */
    private void filterPhysios(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            lsPhysios.setItems(allPhysios);
        } else {
            String lowerCaseFilter = searchText.toLowerCase();
            ObservableList<Physio> filteredPhysios = allPhysios.filtered(physio ->
                    physio.getName().toLowerCase().contains(lowerCaseFilter) ||
                            physio.getSurname().toLowerCase().contains(lowerCaseFilter) ||
                            physio.getEmail().toLowerCase().contains(lowerCaseFilter) ||
                            physio.getSpecialty().toLowerCase().contains(lowerCaseFilter)
            );
            lsPhysios.setItems(filteredPhysios);
        }
    }

    /**
     * Fetches the list of physios from the server and populates the ListView.
     */
    private void getPhysios() {
        String url = ServiceUtils.SERVER + "/physios";
        ServiceUtils.getResponseAsync(url, null, "GET")
                .thenApply(json -> gson.fromJson(json, PhysioListResponse.class))
                .thenAccept(response -> {
                    if (!response.isError()) {
                        Platform.runLater(() -> {
                            allPhysios.setAll(response.getPhysios()); // Almacena todos los fisioterapeutas
                            lsPhysios.setItems(allPhysios); // Muestra todos los fisioterapeutas en el ListView
                        });
                    } else {
                        showAlert("Error", response.getErrorMessage(), 2);
                    }
                }).exceptionally(_ -> {
                    showAlert("Error", "Failed to fetch physios", 2);
                    return null;
                });
    }

    /**
     * Handles the action to add a new physio.
     */
    public void onAddPhysio() {
        Physio selectedPhysio = lsPhysios.getSelectionModel().getSelectedItem();

        if (selectedPhysio != null) {
            showAlert("Warning", "To add a new physio, please deselect the selected physio from the list or press the 'Clear Fields' button.", 2);
        } else {
            Physio newPhysio = getValidatedDataFromForm();
            if (newPhysio != null) {
                postPhysio(newPhysio);
            }
        }
    }

    /**
     * Handles the action to update an existing physio.
     */
    public void onUpdatePhysio() {
        Physio selectedPhysio = lsPhysios.getSelectionModel().getSelectedItem();
        if (selectedPhysio == null) {
            showAlert("Error", "Select a physio to update.", 2);
        } else {
            Physio updatedPhysio = getValidatedDataFromForm();
            if (updatedPhysio != null) {
                updatedPhysio.setId(selectedPhysio.getId());
                modifyPhysio(updatedPhysio);
            }
        }
    }

    /**
     * Handles the action to delete a selected physio.
     */
    public void onDeletePhysio() {
        Physio selectedPhysio = lsPhysios.getSelectionModel().getSelectedItem();
        if (selectedPhysio == null) {
            showAlert("ERROR", "Select a physio", 2);
        } else {
            deletePhysio(selectedPhysio);
        }
    }

    /**
     * Sends a POST request to add a new physio.
     *
     * @param physio the physio to be added
     */
    private void postPhysio(Physio physio) {
        btnAdd.setDisable(true);
        String url = ServiceUtils.SERVER + "/physios";
        String jsonRequest = gson.toJson(physio);

        ServiceUtils.getResponseAsync(url, jsonRequest, "POST")
                .thenApply(json -> gson.fromJson(json, PhysioResponse.class))
                .thenAccept(response -> {
                    if (!response.isError()) {
                        Platform.runLater(() -> {
                            showAlert("Physio added", response.getPhysio().getName() + " added", 1);
                            getPhysios();
                            clearFields();
                            btnAdd.setDisable(false);
                        });
                    } else {
                        Platform.runLater(() -> showAlert("Error creating physio", response.getErrorMessage(), 2));
                    }
                }).exceptionally(_ -> {
                    showAlert("Error", "Failed to add physio", 2);
                    return null;
                });
    }

    /**
     * Sends a PUT request to update an existing physio.
     *
     * @param physio the physio to be updated
     */
    private void modifyPhysio(Physio physio) {
        btnUpdate.setDisable(true);
        String url = ServiceUtils.SERVER + "/physios/" + physio.getId();
        String jsonRequest = gson.toJson(physio);

        ServiceUtils.getResponseAsync(url, jsonRequest, "PUT")
                .thenApply(json -> gson.fromJson(json, PhysioResponse.class))
                .thenAccept(response -> {
                    if (!response.isError()) {
                        Platform.runLater(() -> {
                            showAlert("Physio updated", response.getPhysio().getName() + " updated", 1);
                            getPhysios();
                            btnUpdate.setDisable(false);
                            clearFields();
                        });
                    } else {
                        Platform.runLater(() -> showAlert("Error modifying physio", response.getErrorMessage(), 2));
                    }
                }).exceptionally(_ -> {
                    showAlert("Error", "Failed to update physio", 2);
                    return null;
                });
    }

    /**
     * Sends a DELETE request to remove a physio.
     *
     * @param physio the physio to be deleted
     */
    private void deletePhysio(Physio physio) {
        btnDelete.setDisable(true);
        String url = ServiceUtils.SERVER + "/physios/" + physio.getId();

        ServiceUtils.getResponseAsync(url, "", "DELETE")
                .thenApply(json -> gson.fromJson(json, PhysioResponse.class))
                .thenAccept(response -> {
                    if (!response.isError()) {
                        Platform.runLater(() -> {
                            showAlert("Physio deleted", response.getPhysio().getName() + " deleted", 1);
                            getPhysios();
                            btnDelete.setDisable(false);
                            clearFields();
                        });
                    } else {
                        Platform.runLater(() -> showAlert("Error deleting physio", response.getErrorMessage(), 2));
                    }
                }).exceptionally(_ -> {
                    showAlert("Error", "Failed to delete physio", 2);
                    return null;
                });
    }

    /**
     * Clears all input fields and deselects any selected physio.
     */
    private void clearFields() {
        txtName.clear();
        txtSurname.clear();
        txtLicenseNumber.clear();
        txtEmail.clear();
        cbSpecialization.setValue(null);
        lsPhysios.getSelectionModel().clearSelection();
    }

    /**
     * Validates the input fields and returns a Physio object if valid.
     *
     * @return a Physio object or null if validation fails
     */
    private Physio getValidatedDataFromForm() {
        String name = txtName.getText();
        String surname = txtSurname.getText();
        String licenseNumber = txtLicenseNumber.getText();
        String email = txtEmail.getText();
        String specialty = cbSpecialization.getValue();

        if (name.isEmpty() || surname.isEmpty() || licenseNumber.isEmpty() || email.isEmpty() || specialty.isEmpty()) {
            showAlert("Error", "Please fill all the fields.", 2);
            return null;
        }
        return new Physio(name, surname, licenseNumber, specialty, email);
    }

    /**
     * Handles the action to navigate back to the main view.
     *
     * @param actionEvent the event triggered by the back button
     */
    public void onBackButtonClick(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        String fxmlFile = "/com/matias/physiocarepsp/fxmlviews/first-view.fxml";
        String title = "Welcome | PhysioCare";
        Utils.switchView(source, fxmlFile, title);
    }

    /**
     * Handles the action to clear all input fields.
     *
     * @param actionEvent the event triggered by the clear button
     */
    public void clearFieldsAction(ActionEvent actionEvent) {
        clearFields();
    }

    /**
     * Populates the input fields with the data of the selected physio.
     *
     * @param physio the selected physio
     */
    private void populateFields(Physio physio) {
        txtName.setText(physio.getName());
        txtSurname.setText(physio.getSurname());
        txtLicenseNumber.setText(physio.getLicenseNumber());
        txtEmail.setText(physio.getEmail());
        cbSpecialization.setValue(physio.getSpecialty());
    }
}
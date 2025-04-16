package com.matias.physiocarepsp.viewscontroller;

import com.google.gson.Gson;
import com.matias.physiocarepsp.models.Patient.Patient;
import com.matias.physiocarepsp.models.Patient.PatientListResponse;
import com.matias.physiocarepsp.models.Patient.PatientResponse;
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
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.ResourceBundle;

import static com.matias.physiocarepsp.utils.Utils.showAlert;

/**
 * Controller for the Patients view of the application.
 * Handles CRUD operations for patients and manages the UI interactions.
 */
public class PatientsViewController implements Initializable {

    public TextField txtSearch;
    @FXML
    private TextField txtName;
    @FXML
    private TextField txtSurname;
    @FXML
    private DatePicker dpBirthDate;
    @FXML
    private TextField txtAddress;
    @FXML
    private TextField txtInsuranceNumber;
    @FXML
    private TextField txtEmail;
    @FXML
    private ListView<Patient> lsPatients;
    @FXML
    private Button btnDelete;
    @FXML
    private Button btnAdd;
    @FXML
    private Button btnUpdate;
    private ObservableList<Patient> allPatients = FXCollections.observableArrayList();

    private final Gson gson = new Gson();

    /**
     * Initializes the controller and sets up the patient list selection listener.
     *
     * @param url            the location used to resolve relative paths
     * @param resourceBundle the resources used to localize the root object
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lsPatients.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<Patient>() {
                    @Override
                    public void changed(ObservableValue<? extends Patient> observableValue, Patient oldValue, Patient newValue) {
                        if (newValue != null) {
                            populateFields(newValue);
                        } else {
                            clearFields();
                        }
                    }
                });
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> filterPatients(newValue));

        getPatients();
    }

    /**
     * Filters the list of patients based on the search text entered by the user.
     *
     * @param searchText the text to filter the patients
     */
    private void filterPatients(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            lsPatients.setItems(allPatients);
        } else {
            String lowerCaseFilter = searchText.toLowerCase();
            ObservableList<Patient> filteredPatients = allPatients.filtered(patient ->
                    patient.getName().toLowerCase().contains(lowerCaseFilter) ||
                            patient.getSurname().toLowerCase().contains(lowerCaseFilter) ||
                            patient.getEmail().toLowerCase().contains(lowerCaseFilter)
            );
            lsPatients.setItems(filteredPatients);
        }
    }

    /**
     * Fetches the list of patients from the server and populates the ListView.
     */
    private void getPatients() {
        String url = ServiceUtils.SERVER + "/patients";
        ServiceUtils.getResponseAsync(url, null, "GET")
                .thenApply(json -> gson.fromJson(json, PatientListResponse.class))
                .thenAccept(response -> {
                    if (!response.isError()) {
                        Platform.runLater(() -> {
                            allPatients.setAll(response.getPatients()); // Populate allPatients with the server data
                            lsPatients.setItems(allPatients); // Display all patients in the ListView
                        });
                    } else {
                        showAlert("Error", response.getErrorMessage(), 2);
                    }
                }).exceptionally(_ -> {
                    showAlert("Error", "Failed to fetch patients", 2);
                    return null;
                });
    }

    /**
     * Handles the action to add a new patient.
     */
    public void addPatientAction() {
        Patient selectedPatient = lsPatients.getSelectionModel().getSelectedItem();
        if (selectedPatient != null) {
            showAlert("Warning", "To add a new patient, please deselect the selected patient from the list or press the 'Clear Fields' button.", 2);
        } else {
            Patient newPatient = getValidatedDataFromForm();
            if (newPatient != null) {
                postPatient(newPatient);
            }
        }
    }

    /**
     * Handles the action to update an existing patient.
     */
    public void updatePatientAction() {
        Patient selectedPatient = lsPatients.getSelectionModel().getSelectedItem();
        if (selectedPatient == null) {
            showAlert("Error", "Select a patient to update.", 2);
        } else {
            Patient updatedPatient = getValidatedDataFromForm();
            if (updatedPatient != null) {
                updatedPatient.setId(selectedPatient.getId());
                modifyPatient(updatedPatient);
            }
        }
    }

    /**
     * Handles the action to delete a selected patient.
     */
    public void deletePatientAction() {
        Patient selectedPatient = lsPatients.getSelectionModel().getSelectedItem();
        if (selectedPatient == null) {
            showAlert("ERROR", "Select a patient", 2);
        } else {
            deletePatient(selectedPatient);
        }
    }

    /**
     * Handles the action to clear all input fields.
     */
    public void clearFieldsAction() {
        clearFields();
    }

    /**
     * Sends a POST request to add a new patient.
     *
     * @param patient the patient to be added
     */
    private void postPatient(Patient patient) {
        btnAdd.setDisable(true);
        String url = ServiceUtils.SERVER + "/patients";
        String jsonRequest = gson.toJson(patient);

        ServiceUtils.getResponseAsync(url, jsonRequest, "POST")
                .thenApply(json -> gson.fromJson(json, PatientResponse.class))
                .thenAccept(response -> {
                    if (!response.isError()) {
                        Platform.runLater(() -> {
                            showAlert("Added patient", response.getPatient().getName() + " added", 1);
                            getPatients();
                            clearFields();
                            btnAdd.setDisable(false);
                        });
                    } else {
                        Platform.runLater(() -> showAlert("Error creating patient", response.getErrorMessage(), 2));
                    }
                }).exceptionally(_ -> {
                    showAlert("Error", "Failed to add patient", 2);
                    return null;
                });
    }

    /**
     * Sends a PUT request to update an existing patient.
     *
     * @param patient the patient to be updated
     */
    private void modifyPatient(Patient patient) {
        btnUpdate.setDisable(true);
        String url = ServiceUtils.SERVER + "/patients/" + patient.getId();
        String jsonRequest = gson.toJson(patient);

        ServiceUtils.getResponseAsync(url, jsonRequest, "PUT")
                .thenApply(json -> gson.fromJson(json, PatientResponse.class))
                .thenAccept(response -> {
                    if (!response.isError()) {
                        Platform.runLater(() -> {
                            showAlert("Updated patient", response.getPatient().getName() + " updated", 1);
                            getPatients();
                            btnUpdate.setDisable(false);
                            clearFields();
                        });
                    } else {
                        Platform.runLater(() -> showAlert("Error modifying patient", response.getErrorMessage(), 2));
                    }
                }).exceptionally(_ -> {
                    showAlert("Error", "Failed to update patient", 2);
                    return null;
                });
    }

    /**
     * Sends a DELETE request to remove a patient.
     *
     * @param patient the patient to be deleted
     */
    private void deletePatient(Patient patient) {
        btnDelete.setDisable(true);
        String url = ServiceUtils.SERVER + "/patients/" + patient.getId();

        ServiceUtils.getResponseAsync(url, "", "DELETE")
                .thenApply(json -> gson.fromJson(json, PatientResponse.class))
                .thenAccept(response -> {
                    if (!response.isError()) {
                        Platform.runLater(() -> {
                            showAlert("Deleted Patient", response.getPatient().getName() + " deleted", 1);
                            getPatients();
                            btnDelete.setDisable(false);
                            clearFields();
                        });
                    } else {
                        Platform.runLater(() -> showAlert("Error deleting patient", response.getErrorMessage(), 2));
                    }
                }).exceptionally(_ -> {
                    showAlert("Error", "Failed to delete patient", 2);
                    return null;
                });
    }

    /**
     * Clears all input fields and deselects any selected patient.
     */
    private void clearFields() {
        txtName.clear();
        txtSurname.clear();
        txtAddress.clear();
        txtInsuranceNumber.clear();
        txtEmail.clear();
        dpBirthDate.setValue(null);
        lsPatients.getSelectionModel().clearSelection();
    }

    /**
     * Validates the input fields and returns a Patient object if valid.
     *
     * @return a Patient object or null if validation fails
     */
    private Patient getValidatedDataFromForm() {
        String patientName = txtName.getText();
        String surname = txtSurname.getText();
        String address = txtAddress.getText();
        String insuranceNumber = txtInsuranceNumber.getText();
        String email = txtEmail.getText();
        LocalDate localDate = dpBirthDate.getValue();

        if (patientName.isEmpty() || surname.isEmpty() || address.isEmpty()
                || insuranceNumber.isEmpty() || email.isEmpty() || localDate == null) {
            showAlert("Error", "Please fill all the fields.", 2);
            return null;
        }
        Date birthDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        return new Patient(patientName, surname, birthDate, address, insuranceNumber, email);
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
     * Populates the input fields with the data of the selected patient.
     *
     * @param patient the selected patient
     */
    private void populateFields(Patient patient) {
        txtName.setText(patient.getName());
        txtSurname.setText(patient.getSurname());
        txtAddress.setText(patient.getAddress());
        txtInsuranceNumber.setText(patient.getInsuranceNumber());
        txtEmail.setText(patient.getEmail());
        LocalDate localDate = patient.getBirthDate().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        dpBirthDate.setValue(localDate);
    }
}
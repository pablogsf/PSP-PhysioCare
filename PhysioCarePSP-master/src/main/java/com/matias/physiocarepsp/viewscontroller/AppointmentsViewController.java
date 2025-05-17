package com.matias.physiocarepsp.viewscontroller;

import com.google.gson.Gson;
import com.matias.physiocarepsp.models.Appointment.Appointment;
import com.matias.physiocarepsp.models.Appointment.AppointmentResponse;
import com.matias.physiocarepsp.models.Appointment.AppointmentListResponse;
import com.matias.physiocarepsp.models.Patient.Patient;
import com.matias.physiocarepsp.models.Patient.PatientListResponse;
import com.matias.physiocarepsp.models.Physio.Physio;
import com.matias.physiocarepsp.models.Physio.PhysioListResponse;
import com.matias.physiocarepsp.utils.ServiceUtils;
import com.matias.physiocarepsp.utils.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class AppointmentsViewController {
    @FXML private TableView<Appointment> tblAppointments;
    @FXML private TableColumn<Appointment, String> colDate, colPatient, colPhysio, colTreatment, colPrice;
    @FXML private DatePicker dpDate;
    @FXML private ComboBox<Patient> cbPatient;
    @FXML private ComboBox<Physio> cbPhysio;
    @FXML private TextField txtTreatment, txtPrice;

    private final ObservableList<Appointment> appointments = FXCollections.observableArrayList();
    private final Gson gson = new Gson();

    @FXML
    public void initialize() {
        // configura columnas: propertyValueFactory("dateString") etc.
        colDate.setCellValueFactory(c -> c.getValue().dateTimeProperty().asString());
        colPatient.setCellValueFactory(c -> c.getValue().patientNameProperty());
        colPhysio.setCellValueFactory(c -> c.getValue().physioNameProperty());
        colTreatment.setCellValueFactory(c -> c.getValue().treatmentProperty());
        colPrice.setCellValueFactory(c -> c.getValue().priceProperty().asString());

        tblAppointments.setItems(appointments);
        loadPatients();
        loadPhysios();
        loadAppointments();

    }

    private void loadPatients() {
        ServiceUtils.getResponseAsync(ServiceUtils.SERVER + "/patients", null, "GET")
                .thenApply(json -> gson.fromJson(json, PatientListResponse.class))
                .thenAccept(resp -> cbPatient.getItems().setAll(resp.getPatients()));
    }
    private void loadPhysios() {
        ServiceUtils.getResponseAsync(ServiceUtils.SERVER + "/physios", null, "GET")
                .thenApply(json -> gson.fromJson(json, PhysioListResponse.class))
                .thenAccept(resp -> cbPhysio.getItems().setAll(resp.getPhysios()));
    }
    private void loadAppointments() {
        ServiceUtils.getResponseAsync(ServiceUtils.SERVER + "/appointments", null, "GET")
                .thenApply(json -> gson.fromJson(json, AppointmentListResponse.class))
                .thenAccept(resp -> appointments.setAll(resp.getAppointments()));
    }

    @FXML
    public void onAddAppointment() {
        Appointment a = new Appointment();
        LocalDate date = dpDate.getValue();
        a.setDateTime(LocalDateTime.of(date, LocalTime.now()));
        a.setPatientId(cbPatient.getValue().getId());
        a.setPhysioId(cbPhysio.getValue().getId());
        a.setTreatment(txtTreatment.getText());
        a.setPrice(Double.parseDouble(txtPrice.getText()));
        String body = gson.toJson(a);
        ServiceUtils.getResponseAsync(ServiceUtils.SERVER + "/appointments", body, "POST")
                .thenApply(json -> gson.fromJson(json, AppointmentResponse.class))
                .thenAccept(resp -> {
                    if (!resp.isError()) {
                        appointments.add(resp.getAppointment());
                    } else {
                        // alerta de error
                    }
                });
    }

    public void btn_back(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        String fxmlFile = "/com/matias/physiocarepsp/fxmlviews/patients-view.fxml";
        String title = "Welcome | Patients";
        Utils.switchView(source, fxmlFile, title);
    }
}

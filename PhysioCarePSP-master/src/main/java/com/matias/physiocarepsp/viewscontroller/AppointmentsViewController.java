package com.matias.physiocarepsp.viewscontroller;

import com.google.gson.*;
import com.matias.physiocarepsp.models.Appointment.Appointment;
import com.matias.physiocarepsp.models.Appointment.AppointmentResponse;
import com.matias.physiocarepsp.models.Appointment.AppointmentListResponse;
import com.matias.physiocarepsp.models.Patient.Patient;
import com.matias.physiocarepsp.models.Patient.PatientListResponse;
import com.matias.physiocarepsp.models.Physio.Physio;
import com.matias.physiocarepsp.models.Physio.PhysioListResponse;
import com.matias.physiocarepsp.utils.ServiceUtils;
import com.matias.physiocarepsp.utils.Utils;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AppointmentsViewController {
    private static final Logger LOGGER = Logger.getLogger(AppointmentsViewController.class.getName());

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
        colDate.setCellValueFactory(c -> c.getValue().dateTimeProperty().asString());
        colPatient.setCellValueFactory(c -> c.getValue().patientNameProperty());
        colPhysio.setCellValueFactory(c -> c.getValue().physioNameProperty());
        colTreatment.setCellValueFactory(c -> c.getValue().treatmentProperty());
        colPrice.setCellValueFactory(c -> c.getValue().priceProperty().asString());

        tblAppointments.setItems(appointments);
        loadPatients();
        loadPhysios();
        loadAppointments(); // ahora sí puede usar ServiceUtils.getUserId()
    }

    private void loadPatients() {
        ServiceUtils.getResponseAsync(ServiceUtils.SERVER + "/patients", null, "GET")
                .thenApply(json -> gson.fromJson(json, PatientListResponse.class))
                .thenAccept(resp -> cbPatient.getItems().setAll(resp.getPatients()))
                .exceptionally(ex -> {
                    LOGGER.log(Level.SEVERE, "Error cargando pacientes", ex);
                    return null;
                });
    }

    private void loadPhysios() {
        ServiceUtils.getResponseAsync(ServiceUtils.SERVER + "/physios", null, "GET")
                .thenApply(json -> gson.fromJson(json, PhysioListResponse.class))
                .thenAccept(resp -> cbPhysio.getItems().setAll(resp.getPhysios()))
                .exceptionally(ex -> {
                    LOGGER.log(Level.SEVERE, "Error cargando fisioterapeutas", ex);
                    return null;
                });
    }

    private void loadAppointments() {
        String physioId = ServiceUtils.getUserId();
        if (physioId == null) {
            LOGGER.warning("No hay usuario logueado, physioId es null");
            return;
        }

        String url = ServiceUtils.SERVER + "/records/physio/" + physioId + "/appointments";
        LOGGER.info("Cargando citas desde: " + url);

        ServiceUtils.getResponseAsync(url, null, "GET")
                .thenAccept(json -> {
                    try {
                        JsonObject root = JsonParser.parseString(json).getAsJsonObject();
                        JsonArray arr   = root.getAsJsonArray("result");
                        var list = FXCollections.<Appointment>observableArrayList();

                        for (JsonElement el : arr) {
                            JsonObject o = el.getAsJsonObject();
                            Appointment a = new Appointment();
                            a.setId(         o.get("id").getAsString());
                            a.setPatientName(o.get("patientName").getAsString());
                            a.setPhysioName( o.get("physioName").getAsString());
                            a.setDateTime(LocalDateTime.parse(
                                    o.get("date").getAsString(),
                                    DateTimeFormatter.ISO_OFFSET_DATE_TIME
                            ));
                            a.setDiagnosis(   o.get("diagnosis").getAsString());
                            a.setTreatment(   o.get("treatment").getAsString());
                            a.setObservations(o.get("observations").getAsString());
                            if (o.has("price")) {
                                a.setPrice(o.get("price").getAsDouble());
                            }
                            list.add(a);
                        }

                        Platform.runLater(() -> appointments.setAll(list));

                    } catch (Exception ex) {
                        LOGGER.log(Level.SEVERE, "Error parseando citas", ex);
                    }
                })
                .exceptionally(ex -> {
                    LOGGER.log(Level.SEVERE, "Error cargando citas", ex);
                    return null;
                });
    }

    @FXML
    public void onAddAppointment() {
        try {
            // 1) Recojo datos de la UI
            LocalDate date = dpDate.getValue();

            // crea un Instant en UTC, truncado a segundos
            Instant instant = date
                    .atTime(LocalTime.now())
                    .atOffset(ZoneOffset.UTC)
                    .toInstant()
                    .truncatedTo(ChronoUnit.SECONDS);

            String isoDateTime = instant.toString();
            // queda algo como "2025-05-30T21:31:49Z"

            // 2) payload igual que antes
            Map<String, Object> payload = getPayloadMap(isoDateTime);
            String body = gson.toJson(payload);

            LOGGER.info("POST " + ServiceUtils.SERVER + "/records/appointments");
            LOGGER.info("Payload: " + body);

            // 3) envío y manejo de la respuesta
            ServiceUtils.getResponseAsync(
                            ServiceUtils.SERVER + "/records/appointments",
                            body,
                            "POST"
                    )
                    .thenApply(json -> {
                        LOGGER.info("Respuesta raw: " + json);
                        return gson.fromJson(json, AppointmentResponse.class);
                    })
                    .thenAccept(resp -> {
                        if (!resp.isError()) {
                            appointments.add(resp.getAppointment());
                        } else {
                            new Alert(Alert.AlertType.ERROR, resp.getErrorMessage()).showAndWait();
                        }
                    })
                    .exceptionally(ex -> {
                        LOGGER.log(Level.SEVERE, "Excepción al crear cita", ex);
                        // muestra también el cuerpo de la excepción
                        new Alert(Alert.AlertType.ERROR,
                                "No pude crear cita:\n" + ex.getCause().getMessage()
                        ).showAndWait();
                        return null;
                    });

        } catch (NumberFormatException nfe) {
            new Alert(Alert.AlertType.ERROR, "Precio inválido").showAndWait();
        }
    }

    private Map<String, Object> getPayloadMap(String isoDateTime) {
        String patientId = cbPatient.getValue().getId();
        String physioId  = ServiceUtils.getUserId();  // también lo usas aquí si lo necesitas
        String treatment = txtTreatment.getText();
        double price     = Double.parseDouble(txtPrice.getText());

        Map<String, Object> payload = new HashMap<>();
        payload.put("patient",      patientId);
        payload.put("physio",       physioId);
        payload.put("date",         isoDateTime);
        payload.put("treatment",    treatment);
        payload.put("diagnosis",    "");
        payload.put("observations", "");
        payload.put("price",        price);
        return payload;
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
}

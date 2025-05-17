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
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
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
        loadAppointments();
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

//    private void loadAppointments() {
//        ServiceUtils.getResponseAsync(ServiceUtils.SERVER + "/records/appointments", null, "GET")
//                .thenApply(json -> gson.fromJson(json, AppointmentListResponse.class))
//                .thenAccept(resp -> appointments.setAll(resp.getAppointments()))
//                .exceptionally(ex -> {
//                    LOGGER.log(Level.SEVERE, "Error cargando citas", ex);
//                    return null;
//                });
//    }

    private void loadAppointments() {
        ServiceUtils.getResponseAsync(ServiceUtils.SERVER + "/records/appointments", null, "GET")
                .thenAccept(json -> {
                    try {
                        JsonObject root = JsonParser.parseString(json).getAsJsonObject();
                        JsonArray arr   = root.getAsJsonArray("appointments");
                        var list = FXCollections.<Appointment>observableArrayList();

                        for (JsonElement el : arr) {
                            JsonObject o = el.getAsJsonObject();
                            Appointment a = new Appointment();
                            // id
                            a.setId(o.get("_id").getAsString());
                            // paciente
                            a.setPatientId(o.get("patient").getAsString());
                            // Si quieres mostrar el nombre, haz otra llamada o inclúyelo en la respuesta:
                            a.setPatientName(o.has("patientName")
                                    ? o.get("patientName").getAsString()
                                    : "");
                            // fisioterapeuta
                            a.setPhysioId(o.get("physio").getAsString());
                            a.setPhysioName(o.has("physioName")
                                    ? o.get("physioName").getAsString()
                                    : "");
                            // fecha y hora
                            a.setDateTime(
                                    LocalDateTime.parse(
                                            o.get("date").getAsString(),
                                            DateTimeFormatter.ISO_OFFSET_DATE_TIME
                                    )
                            );
                            // tratamiento, observaciones, precio
                            a.setTreatment(   o.get("treatment").getAsString());
                            a.setDiagnosis(   o.has("diagnosis") ? o.get("diagnosis").getAsString() : "");
                            a.setObservations(o.has("observations") ? o.get("observations").getAsString() : "");
                            a.setPrice(       o.get("price").getAsDouble());

                            list.add(a);
                        }

                        // Como estamos fuera del hilo de la UI, volvemos al FX Thread:
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
            String isoDateTime = LocalDateTime.of(date, LocalTime.now())
                    .atOffset(ZoneOffset.UTC)
                    .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

            Map<String, Object> payload = getPayloadMap(isoDateTime);
            String body = gson.toJson(payload);

            // Log payload y endpoint
            LOGGER.info("POST " + ServiceUtils.SERVER + "/appointments");
            LOGGER.info("Payload: " + body);

            // 2) Envío la petición
            ServiceUtils.getResponseAsync(
                            ServiceUtils.SERVER + "/appointments",
                            body,
                            "POST"
                    )
                    .thenApply(json -> {
                        LOGGER.info("Respuesta raw: " + json);
                        return gson.fromJson(json, AppointmentResponse.class);
                    })
                    .thenAccept(resp -> {
                        if (!resp.isError()) {
                            LOGGER.info("Cita creada: " + resp.getAppointment());
                            appointments.add(resp.getAppointment());
                        } else {
                            LOGGER.warning("Error al crear cita: " + resp.getErrorMessage());
                            new Alert(Alert.AlertType.ERROR, resp.getErrorMessage()).showAndWait();
                        }
                    })
                    .exceptionally(ex -> {
                        LOGGER.log(Level.SEVERE, "Excepción en solicitud de creación de cita", ex);
                        new Alert(Alert.AlertType.ERROR, "Error enviando petición:\n" + ex.getMessage())
                                .showAndWait();
                        return null;
                    });

        } catch (NumberFormatException nfe) {
            LOGGER.log(Level.WARNING, "Precio inválido: " + txtPrice.getText(), nfe);
            new Alert(Alert.AlertType.ERROR, "Precio inválido").showAndWait();
        }
    }

    private Map<String, Object> getPayloadMap(String isoDateTime) {
        String patientId = cbPatient.getValue().getId();
        String physioId  = cbPhysio.getValue().getId();
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
}

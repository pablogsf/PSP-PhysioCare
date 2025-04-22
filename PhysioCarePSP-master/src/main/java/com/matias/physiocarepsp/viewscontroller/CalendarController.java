package com.matias.physiocarepsp.viewscontroller;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.view.CalendarView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.matias.physiocarepsp.models.Appointment.Appointment;
import com.matias.physiocarepsp.models.Appointment.AppointmentListResponse;
import com.matias.physiocarepsp.models.Patient.Patient;
import com.matias.physiocarepsp.models.Patient.PatientListResponse;
import com.matias.physiocarepsp.models.Physio.Physio;
import com.matias.physiocarepsp.models.Physio.PhysioListResponse;
import com.matias.physiocarepsp.models.Record.CreateRecordRequest;
import com.matias.physiocarepsp.models.Record.RecordResponse;
import com.matias.physiocarepsp.utils.LocalDateAdapter;
import com.matias.physiocarepsp.utils.LocalDateTimeAdapter;
import com.matias.physiocarepsp.utils.ServiceUtils;
import com.matias.physiocarepsp.utils.Utils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.event.ActionEvent;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

import static com.matias.physiocarepsp.utils.Utils.showAlert;

public class CalendarController implements Initializable {

    @FXML private CalendarView calendarView;
    @FXML private Button onBackButtonClick;
    @FXML private ComboBox<Patient> patientComboBox;
    @FXML private ComboBox<Physio> physioComboBox;
    @FXML private DatePicker datePicker;
    @FXML private Spinner<Integer> hourSpinner;
    @FXML private Button createAppointmentButton;

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    private final Calendar citasCalendar = new Calendar("Citas");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Initializing CalendarController...");
        // Configura calendario
        CalendarSource source = new CalendarSource("Mis Citas");
        source.getCalendars().add(citasCalendar);
        calendarView.getCalendarSources().add(source);
        calendarView.setShowAddCalendarButton(false);
        citasCalendar.setStyle(Calendar.Style.STYLE1);

        // Spinner horas 0–23
        SpinnerValueFactory<Integer> hourFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, LocalTime.now().getHour());
        hourSpinner.setValueFactory(hourFactory);

        System.out.println("Loading initial data...");
        loadAppointments();
        loadPatients();
        loadPhysios();

        createAppointmentButton.setOnAction(evt -> createAppointment());
    }

    private void loadAppointments() {
        System.out.println("loadAppointments: fetching appointments...");
        String url = ServiceUtils.SERVER + "/records/appointments";
        ServiceUtils.getResponseAsync(url, null, "GET")
                .thenApply(json -> {
                    System.out.println("loadAppointments response JSON: " + json);
                    return gson.fromJson(json, AppointmentListResponse.class);
                })
                .thenAccept(response -> {
                    if (!response.isError()) {
                        System.out.println("loadAppointments: parsed appointments, count=" + response.getAppointments().size());
                        Platform.runLater(() -> {
                            citasCalendar.clear();
                            response.getAppointments().forEach(appointment -> {
                                var entry = new com.calendarfx.model.Entry<String>();
                                entry.setTitle(appointment.getPatientName() + " - " + appointment.getDiagnosis());
                                entry.setInterval(appointment.getDate());
                                entry.setFullDay(false);
                                citasCalendar.addEntry(entry);
                            });
                        });
                    } else {
                        System.err.println("loadAppointments error: " + response.getErrorMessage());
                        showAlert("Error", response.getErrorMessage(), 2);
                    }
                })
                .exceptionally(ex -> {
                    System.err.println("loadAppointments exception: " + ex.getMessage());
                    showAlert("Error", "Failed to fetch appointments", 2);
                    return null;
                });
    }

    private void loadPatients() {
        System.out.println("loadPatients: fetching patients...");
        String url = ServiceUtils.SERVER + "/patients";
        ServiceUtils.getResponseAsync(url, null, "GET")
                .thenApply(json -> {
                    System.out.println("loadPatients response JSON: " + json);
                    return gson.fromJson(json, PatientListResponse.class);
                })
                .thenAccept(resp -> {
                    if (!resp.isError()) {
                        System.out.println("loadPatients: loaded patients, count=" + resp.getPatients().size());
                        Platform.runLater(() -> patientComboBox.getItems().setAll(resp.getPatients()));
                    } else {
                        System.err.println("loadPatients error: " + resp.getErrorMessage());
                    }
                })
                .exceptionally(e -> {
                    System.err.println("loadPatients exception: " + e.getMessage());
                    showAlert("Error", "No se cargaron pacientes", 2);
                    return null;
                });
    }

    private void loadPhysios() {
        System.out.println("loadPhysios: fetching physiotherapists...");
        String url = ServiceUtils.SERVER + "/physios";
        ServiceUtils.getResponseAsync(url, null, "GET")
                .thenApply(json -> {
                    System.out.println("loadPhysios response JSON: " + json);
                    return gson.fromJson(json, PhysioListResponse.class);
                })
                .thenAccept(resp -> {
                    if (!resp.isError()) {
                        System.out.println("loadPhysios: loaded physiotherapists, count=" + resp.getPhysios().size());
                        Platform.runLater(() -> physioComboBox.getItems().setAll(resp.getPhysios()));
                    } else {
                        System.err.println("loadPhysios error: " + resp.getErrorMessage());
                    }
                })
                .exceptionally(e -> {
                    System.err.println("loadPhysios exception: " + e.getMessage());
                    showAlert("Error", "No se cargaron fisios", 2);
                    return null;
                });
    }

    private void createAppointment() {
        System.out.println("createAppointment: user triggered creation...");
        Patient p = patientComboBox.getValue();
        Physio f = physioComboBox.getValue();
        LocalDate date = datePicker.getValue();
        int hour = hourSpinner.getValue();

        if (p == null || f == null || date == null) {
            System.err.println("createAppointment: missing selection");
            showAlert("Aviso", "Selecciona paciente, fisio y fecha", 1);
            return;
        }

        String recUrl = ServiceUtils.SERVER + "/records/patient/" + p.getId();
        System.out.println("createAppointment: GET " + recUrl);

        // 1) Intento GET; si falla (404), retornamos null
        ServiceUtils.getResponseAsync(recUrl, null, "GET")
                .exceptionally(err -> {
                    System.out.println("createAppointment: expediente no existe → crear");
                    return null;
                })
                .thenCompose(recJson -> {
                    if (recJson == null) {
                        // 2) POST /records
                        CreateRecordRequest dto = new CreateRecordRequest(p.getId());
                        String createBody = gson.toJson(dto);
                        System.out.println("createAppointment POST /records body: " + createBody);
                        return ServiceUtils.getResponseAsync(ServiceUtils.SERVER + "/records", createBody, "POST")
                                // 3) tras crear, reintento GET para obtener objeto completo
                                .thenCompose(_ignored -> ServiceUtils.getResponseAsync(recUrl, null, "GET"));
                    } else {
                        // 4) Si GET inicial OK, lo usamos
                        return CompletableFuture.completedFuture(recJson);
                    }
                })
                .thenCompose(fullRecJson -> {
                    // 5) Parseamos siempre a RecordResponse con patient=object
                    RecordResponse recResp = gson.fromJson(fullRecJson, RecordResponse.class);
                    String recordId = recResp.getRecord().getId();

                    // 6) POST cita
                    LocalDateTime fechaHora = date.atTime(hour, 0);
                    Appointment apt = new Appointment(fechaHora, f.getId(), "", "", "");
                    String aptBody = gson.toJson(apt);
                    System.out.println("createAppointment POST /records/" + recordId + "/appointments body: " + aptBody);
                    return ServiceUtils.getResponseAsync(
                            ServiceUtils.SERVER + "/records/" + recordId + "/appointments",
                            aptBody, "POST"
                    );
                })
                .thenAccept(aptJson -> {
                    System.out.println("createAppointment appointment created: " + aptJson);
                    Platform.runLater(() -> {
                        showAlert("Éxito", "Cita creada correctamente", 0);
                        loadAppointments();
                    });
                })
                .exceptionally(ex -> {
                    System.err.println("createAppointment exception: " + ex.getMessage());
                    showAlert("Error", "No se pudo crear la cita: " + ex.getMessage(), 2);
                    return null;
                });
    }

    public void onBackButtonClick(ActionEvent actionEvent) {
        System.out.println("Navigating back to menu...");
        Node source = (Node) actionEvent.getSource();
        Utils.switchView(source,
                "/com/matias/physiocarepsp/fxmlviews/first-view.fxml",
                "Welcome | PhysioCare");
    }
}

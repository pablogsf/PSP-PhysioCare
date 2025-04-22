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
    @FXML private ComboBox<Patient> patientComboBox;
    @FXML private ComboBox<Physio> physioComboBox;
    @FXML private DatePicker datePicker;
    @FXML private Spinner<Integer> hourSpinner;
    @FXML private Button createAppointmentButton;

    // Nuevo: lista de citas del paciente
    @FXML private ListView<Appointment> appointmentListView;

    // Campos para edición de la cita
    @FXML private TextField diagnosisField;
    @FXML private TextField treatmentField;
    @FXML private TextField observationsField;
    @FXML private Button updateAppointmentButton;

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    private final Calendar citasCalendar = new Calendar("Citas");

    // Para almacenar IDs seleccionados
    private String selectedRecordId;
    private String selectedAppointmentId;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("initialize(): Starting CalendarController");

        // Configuración del calendario (solo para mostrar todas las citas)
        CalendarSource source = new CalendarSource("Mis Citas");
        source.getCalendars().add(citasCalendar);
        calendarView.getCalendarSources().add(source);
        calendarView.setShowAddCalendarButton(false);
        citasCalendar.setStyle(Calendar.Style.STYLE1);

        // Spinner de horas
        System.out.println("initialize(): Configuring hour spinner");
        SpinnerValueFactory<Integer> hourFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, LocalTime.now().getHour());
        hourSpinner.setValueFactory(hourFactory);

        // Carga inicial de pacientes y fisioterapeutas
        System.out.println("initialize(): Loading patients and physiotherapists");
        loadPatients();
        loadPhysios();

        // Crear cita estándar
        createAppointmentButton.setOnAction(evt -> {
            System.out.println("initialize(): createAppointmentButton clicked");
            createAppointment();
        });

        // Actualizar cita seleccionada
        updateAppointmentButton.setOnAction(evt -> {
            System.out.println("initialize(): updateAppointmentButton clicked");
            updateAppointment();
        });
        updateAppointmentButton.setDisable(true);

        // Cuando el usuario elige un paciente, cargamos su expediente y citas
        patientComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldP, newP) -> {
            System.out.println("patientComboBox: selected patient " + (newP != null ? newP.getId() : "null"));
            if (newP != null) {
                loadRecordForPatient(newP);
            } else {
                appointmentListView.getItems().clear();
                updateAppointmentButton.setDisable(true);
            }
        });

        // Cuando el usuario elige una cita de la lista, rellenamos el formulario
        appointmentListView.getSelectionModel().selectedItemProperty().addListener((obs, oldA, newA) -> {
            System.out.println("appointmentListView: selected appointment " + (newA != null ? newA.getId() : "null"));
            if (newA != null) {
                onAppointmentSelected(newA);
            }
        });
    }

    private void loadPatients() {
        System.out.println("loadPatients(): fetching patients...");
        ServiceUtils.getResponseAsync(ServiceUtils.SERVER + "/patients", null, "GET")
                .thenApply(json -> {
                    System.out.println("loadPatients(): response JSON: " + json);
                    return gson.fromJson(json, PatientListResponse.class);
                })
                .thenAccept(resp -> {
                    if (!resp.isError()) {
                        System.out.println("loadPatients(): loaded " + resp.getPatients().size() + " patients");
                        Platform.runLater(() ->
                                patientComboBox.getItems().setAll(resp.getPatients())
                        );
                    } else {
                        System.err.println("loadPatients(): error: " + resp.getErrorMessage());
                    }
                })
                .exceptionally(e -> {
                    System.err.println("loadPatients(): exception: " + e.getMessage());
                    showAlert("Error", "No se cargaron pacientes", 2);
                    return null;
                });
    }

    private void loadPhysios() {
        System.out.println("loadPhysios(): fetching physiotherapists...");
        ServiceUtils.getResponseAsync(ServiceUtils.SERVER + "/physios", null, "GET")
                .thenApply(json -> {
                    System.out.println("loadPhysios(): response JSON: " + json);
                    return gson.fromJson(json, PhysioListResponse.class);
                })
                .thenAccept(resp -> {
                    if (!resp.isError()) {
                        System.out.println("loadPhysios(): loaded " + resp.getPhysios().size() + " physios");
                        Platform.runLater(() ->
                                physioComboBox.getItems().setAll(resp.getPhysios())
                        );
                    } else {
                        System.err.println("loadPhysios(): error: " + resp.getErrorMessage());
                    }
                })
                .exceptionally(e -> {
                    System.err.println("loadPhysios(): exception: " + e.getMessage());
                    showAlert("Error", "No se cargaron fisioterapeutas", 2);
                    return null;
                });
    }

    private void loadRecordForPatient(Patient p) {
        System.out.println("loadRecordForPatient(): fetching record for patient " + p.getId());
        String url = ServiceUtils.SERVER + "/records/patient/" + p.getId();
        ServiceUtils.getResponseAsync(url, null, "GET")
                .thenApply(json -> {
                    System.out.println("loadRecordForPatient(): response JSON: " + json);
                    return gson.fromJson(json, RecordResponse.class);
                })
                .thenAccept(resp -> {
                    Platform.runLater(() -> {
                        if (!resp.isError()) {
                            selectedRecordId = resp.getRecord().getId();
                            System.out.println("loadRecordForPatient(): recordId=" + selectedRecordId
                                    + ", appointments=" + resp.getRecord().getAppointments().size());
                            appointmentListView.getItems().setAll(resp.getRecord().getAppointments());
                            createAppointmentButton.setDisable(false);
                        } else {
                            System.err.println("loadRecordForPatient(): no record found");
                            selectedRecordId = null;
                            appointmentListView.getItems().clear();
                        }
                        updateAppointmentButton.setDisable(true);
                    });
                })
                .exceptionally(e -> {
                    Platform.runLater(() -> {
                        System.err.println("loadRecordForPatient(): exception: " + e.getMessage());
                        selectedRecordId = null;
                        appointmentListView.getItems().clear();
                        updateAppointmentButton.setDisable(true);
                    });
                    return null;
                });
    }

    private void onAppointmentSelected(Appointment apt) {
        System.out.println("onAppointmentSelected(): appointmentId=" + apt.getId());
        updateAppointmentButton.setDisable(false);
        selectedAppointmentId = apt.getId();

        physioComboBox.getSelectionModel().select(
                physioComboBox.getItems().stream()
                        .filter(f -> f.getId().equals(apt.getPhysio()))
                        .findFirst().orElse(null)
        );

        LocalDateTime dt = apt.getDate();
        datePicker.setValue(dt.toLocalDate());
        hourSpinner.getValueFactory().setValue(dt.getHour());

        diagnosisField.setText(apt.getDiagnosis());
        treatmentField.setText(apt.getTreatment());
        observationsField.setText(apt.getObservations());
    }

    private void createAppointment() {
        System.out.println("createAppointment(): called");
        Patient p = patientComboBox.getValue();
        Physio f = physioComboBox.getValue();
        LocalDate date = datePicker.getValue();
        int hour = hourSpinner.getValue();

        System.out.println("createAppointment(): patient=" + (p!=null?p.getId():"null")
                + ", physio=" + (f!=null?f.getId():"null")
                + ", date=" + date + ", hour=" + hour);

        if (p == null || f == null || date == null) {
            System.err.println("createAppointment(): missing selection");
            showAlert("Aviso", "Selecciona paciente, fisio y fecha", 1);
            return;
        }
        if (selectedRecordId == null) {
            System.out.println("createAppointment(): no record → creating record");
            CreateRecordRequest dto = new CreateRecordRequest(p.getId());
            String body = gson.toJson(dto);
            System.out.println("createAppointment(): POST /records body=" + body);
            ServiceUtils.getResponseAsync(ServiceUtils.SERVER + "/records", body, "POST")
                    .thenCompose(_ignore -> ServiceUtils.getResponseAsync(
                            ServiceUtils.SERVER + "/records/patient/" + p.getId(), null, "GET"))
                    .thenRun(() -> {
                        System.out.println("createAppointment(): record created, reloading");
                        Platform.runLater(() -> loadRecordForPatient(p));
                    })
                    .exceptionally(e -> {
                        System.err.println("createAppointment(): error creating record: " + e.getMessage());
                        showAlert("Error", "No se pudo crear expediente", 2);
                        return null;
                    });
            return;
        }

        System.out.println("createAppointment(): record exists → creating appointment in record " + selectedRecordId);
        LocalDateTime fechaHora = date.atTime(hour, 0);
        Appointment apt = new Appointment(fechaHora, f.getId(), "", "", "");
        String aptBody = gson.toJson(apt);
        System.out.println("createAppointment(): POST /records/" + selectedRecordId + "/appointments body=" + aptBody);
        ServiceUtils.getResponseAsync(
                        ServiceUtils.SERVER + "/records/" + selectedRecordId + "/appointments",
                        aptBody, "POST")
                .thenRun(() -> {
                    System.out.println("createAppointment(): appointment created, reloading");
                    Platform.runLater(() -> loadRecordForPatient(p));
                })
                .exceptionally(e -> {
                    System.err.println("createAppointment(): error creating appointment: " + e.getMessage());
                    showAlert("Error", "No se pudo crear la cita", 2);
                    return null;
                });
    }

    private void updateAppointment() {
        System.out.println("updateAppointment(): called");
        if (selectedRecordId == null || selectedAppointmentId == null) {
            System.err.println("updateAppointment(): no record or appointment selected");
            showAlert("Error", "Selecciona primero una cita", 2);
            return;
        }
        LocalDate date = datePicker.getValue();
        int hour = hourSpinner.getValue();
        String diag = diagnosisField.getText().trim();
        String trat = treatmentField.getText().trim();
        String obs  = observationsField.getText().trim();

        System.out.println("updateAppointment(): recordId=" + selectedRecordId
                + ", appointmentId=" + selectedAppointmentId
                + ", diag=" + diag + ", trat=" + trat + ", obs=" + obs);

        if (diag.isEmpty() || trat.isEmpty() || obs.isEmpty()) {
            System.err.println("updateAppointment(): missing text fields");
            showAlert("Aviso", "Completa diagnóstico, tratamiento y observaciones", 1);
            return;
        }

        LocalDateTime dt = date.atTime(hour, 0);
        Appointment apt = new Appointment(dt, physioComboBox.getValue().getId(), diag, trat, obs);
        apt.setId(selectedAppointmentId);
        String body = gson.toJson(apt);
        System.out.println("updateAppointment(): PUT /records/" + selectedRecordId
                + "/appointments/" + selectedAppointmentId + " body=" + body);

        ServiceUtils.getResponseAsync(
                        ServiceUtils.SERVER
                                + "/records/" + selectedRecordId
                                + "/appointments/" + selectedAppointmentId,
                        body, "PUT")
                .thenRun(() -> {
                    System.out.println("updateAppointment(): appointment updated, reloading");
                    Platform.runLater(() -> {
                        showAlert("Éxito", "Cita actualizada correctamente", 0);
                        loadRecordForPatient(patientComboBox.getValue());
                    });
                })
                .exceptionally(e -> {
                    System.err.println("updateAppointment(): error updating appointment: " + e.getMessage());
                    showAlert("Error", "No se pudo actualizar", 2);
                    return null;
                });
    }

    public void onBackButtonClick(ActionEvent actionEvent) {
        System.out.println("onBackButtonClick(): navigating back to menu");
        Node source = (Node) actionEvent.getSource();
        Utils.switchView(source,
                "/com/matias/physiocarepsp/fxmlviews/first-view.fxml",
                "Welcome | PhysioCare");
    }
}

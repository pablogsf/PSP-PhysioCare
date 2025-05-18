package com.matias.physiocarepsp.viewscontroller;

import com.google.api.services.gmail.Gmail;
import com.google.gson.Gson;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.matias.physiocarepsp.models.Appointment.Appointment;
import com.matias.physiocarepsp.models.Appointment.AppointmentDto;
import com.matias.physiocarepsp.models.Appointment.AppointmentListDto;
import com.matias.physiocarepsp.models.Appointment.AppointmentResponse;
import com.matias.physiocarepsp.models.Patient.Patient;
import com.matias.physiocarepsp.models.Patient.PatientListResponse;
import com.matias.physiocarepsp.models.Physio.Physio;
import com.matias.physiocarepsp.models.Physio.PhysioListResponse;
import com.matias.physiocarepsp.utils.EmailUtil;
import com.matias.physiocarepsp.utils.PDFUtil;
import com.matias.physiocarepsp.utils.ServiceUtils;
import com.matias.physiocarepsp.utils.Utils;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static com.matias.physiocarepsp.utils.Utils.showAlert;

/**
 * Controller for the appointments management view in the application.
 * Handles loading, creation, and display of appointments.
 */
public class AppointmentsViewController {

    /**
     * Table that displays the appointments.
     */
    @FXML
    private TableView<Appointment> tblAppointments;

    /**
     * Column that displays the appointment date.
     */
    @FXML
    private TableColumn<Appointment, String> colDate;

    /**
     * Column that displays the patient's name.
     */
    @FXML
    private TableColumn<Appointment, String> colPatient;

    /**
     * Column that displays the physiotherapist's name.
     */
    @FXML
    private TableColumn<Appointment, String> colPhysio;

    /**
     * Column that displays the treatment of the appointment.
     */
    @FXML
    private TableColumn<Appointment, String> colTreatment;

    /**
     * Column that displays the price of the appointment.
     */
    @FXML
    private TableColumn<Appointment, Number> colPrice;

    /**
     * Date picker for selecting the appointment date.
     */
    @FXML
    private DatePicker dpDate;

    /**
     * ComboBox for selecting a patient.
     */
    @FXML
    private ComboBox<Patient> cbPatient;

    /**
     * ComboBox for selecting a physiotherapist.
     */
    @FXML
    private ComboBox<Physio> cbPhysio;

    /**
     * Text field for entering the treatment.
     */
    @FXML
    private TextField txtTreatment;

    /**
     * Text field for entering the price of the appointment.
     */
    @FXML
    private TextField txtPrice;

    /**
     * Observable list containing the appointments.
     */
    private final ObservableList<Appointment> appointments = FXCollections.observableArrayList();

    /**
     * Gson object for JSON serialization and deserialization.
     */
    private final Gson gson = new Gson();

    /**
     * Initializes the controller and sets up the table and initial data.
     */
    @FXML
    public void initialize() {
        colDate.setCellValueFactory(cell -> {
            LocalDateTime dt = cell.getValue().getDateTime();
            String formatted = dt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            return new SimpleStringProperty(formatted);
        });

        // Paciente
        colPatient.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getPatientName())
        );

        // Fisioterapeuta
        colPhysio.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getPhysioName())
        );

        // Tratamiento
        colTreatment.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getTreatment())
        );

        // Precio
        colPrice.setCellValueFactory(cell ->
                new SimpleDoubleProperty(cell.getValue().getPrice())
        );

        tblAppointments.setItems(appointments);

        loadPatients();
        loadPhysios();
        loadAppointments();
    }

    /**
     * Loads the list of patients from the server and adds them to the ComboBox.
     */
    private void loadPatients() {
        ServiceUtils.getResponseAsync(ServiceUtils.SERVER + "/patients", null, "GET")
                .thenApply(json -> gson.fromJson(json, PatientListResponse.class))
                .thenAccept(resp -> Platform.runLater(() ->
                        cbPatient.getItems().setAll(resp.getPatients())
                ));
    }

    /**
     * Loads the list of physiotherapists from the server and adds them to the ComboBox.
     */
    private void loadPhysios() {
        ServiceUtils.getResponseAsync(ServiceUtils.SERVER + "/physios", null, "GET")
                .thenApply(json -> gson.fromJson(json, PhysioListResponse.class))
                .thenAccept(resp -> Platform.runLater(() ->
                        cbPhysio.getItems().setAll(resp.getPhysios())
                ));
    }

    /**
     * Loads the list of appointments from the server and displays them in the table.
     */
    private void loadAppointments() {
        String physioId = ServiceUtils.getUserId();
        if (physioId == null) {
            showAlert("Error", "No hay usuario logueado.", 2);
            return;
        }

        String url = ServiceUtils.SERVER + "/records/physio/" + physioId + "/appointments";

        ServiceUtils.getResponseAsync(url, null, "GET")
                // 1. Deserializamos JSON en DTO plano
                .thenApply(json -> gson.fromJson(json, AppointmentListDto.class))
                .thenAccept(dto -> {
                    if (!dto.isOk()) {
                        Platform.runLater(() ->
                                showAlert("Error", "Error cargando citas.", 2)
                        );
                        return;
                    }

                    // 2. Convertimos cada AppointmentDto en Appointment
                    List<Appointment> list = new ArrayList<>();
                    for (AppointmentDto d : dto.getResult()) {
                        Appointment a = new Appointment();
                        a.setId(d.getId());
                        a.setPatientName(d.getPatientName());
                        a.setPhysioName(d.getPhysioName());
                        a.setDateTime(LocalDateTime.parse(
                                d.getDate(), DateTimeFormatter.ISO_OFFSET_DATE_TIME
                        ));
                        a.setDiagnosis(d.getDiagnosis());
                        a.setTreatment(d.getTreatment());
                        a.setObservations(d.getObservations());
                        if (d.getPrice() != null) {
                            a.setPrice(d.getPrice());
                        }
                        list.add(a);
                    }

                    // 3. Actualizamos la tabla en el hilo de UI
                    Platform.runLater(() -> appointments.setAll(list));
                })
                .exceptionally(ex -> {
                    Platform.runLater(() ->
                            showAlert("Error", "No se pudieron cargar las citas", 2)
                    );
                    return null;
                });
    }

    /**
     * Handles the action of adding a new appointment.
     * Validates the entered data, creates the appointment, and sends it to the server.
     */
    @FXML
    public void onAddAppointment() {
        try {
            LocalDate date = dpDate.getValue();
            // Instant en UTC sin nanos
            String isoDateTime = date
                    .atTime(LocalTime.now())
                    .atOffset(ZoneOffset.UTC)
                    .truncatedTo(ChronoUnit.SECONDS)
                    .toString();

            Map<String, Object> payload = new HashMap<>();
            payload.put("patient", cbPatient.getValue().getId());
            payload.put("physio", ServiceUtils.getUserId());
            payload.put("date", isoDateTime);
            payload.put("treatment", txtTreatment.getText());
            payload.put("diagnosis", "");
            payload.put("observations", "");
            payload.put("price", Double.parseDouble(txtPrice.getText()));

            String body = gson.toJson(payload);

            ServiceUtils.getResponseAsync(ServiceUtils.SERVER + "/records/appointments", body, "POST")
                    .thenApply(json -> gson.fromJson(json, AppointmentResponse.class))
                    .thenAccept(resp -> {
                        if (!resp.isError()) {
                            Platform.runLater(() -> appointments.add(resp.getAppointment()));
                        } else {
                            Platform.runLater(() ->
                                    showAlert("Error", resp.getErrorMessage(), 2)

                            );
                        }
                    })
                    .exceptionally(ex -> {
                        Platform.runLater(() -> {
                            showAlert("Error", "Error al crear cita", 2);
                            System.out.println("Error: " + ex.getMessage());
                        });
                        return null;
                    });

        } catch (NumberFormatException e) {
            showAlert("Error", "Precio inválido", 2);
        }

        ServiceUtils.getResponseAsync(ServiceUtils.SERVER +"/records/"+cbPatient.getValue().getId()+"/appointments", null, "GET")
                .thenApply(json -> gson.fromJson(json, AppointmentListDto.class))
                .thenAccept(resp -> {
                    if (resp.isOk()) {
                        Platform.runLater(()->{
                            System.out.println(resp.getResult().size());
                            if(resp.getResult().size() < 8){
                                if(!resp.getResult().isEmpty()){
                                    String pdfPath = cbPatient.getValue().getName()+"-appointments.pdf";
                                    PdfDocument pdf = PDFUtil.createPdfDocument(resp.getResult(), pdfPath);
                                    if(pdf != null){
                                        try {
                                            Gmail service = EmailUtil.getService();
                                            MimeMessage emailContent = EmailUtil.createEmailWithAttachment(
                                                    "capitanadri@hotmail.com",
                                                    "capitanadri12@gmail.com",
                                                    "Citas de " + cbPatient.getValue().getName(),
                                                    "Adjunto las citas de " + cbPatient.getValue().getName(),
                                                    pdfPath
                                            );

                                            EmailUtil.sendMessage(service,"me", emailContent);

                                        } catch (MessagingException e) {
                                            throw new RuntimeException(e);
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        } catch (Exception e) {
                                            throw new RuntimeException(e);
                                        }
                                    }
                                }
                            }
                        });
                    } else {
                        // alerta de error
                        Platform.runLater(()->{
                            System.out.println("Error: " + resp.getResult());
                        });
                    }
                }).exceptionally(ex->{
                    // alerta de error
                    Platform.runLater(()->{
                        System.out.println("Error: " + ex.getMessage());
                    });
                    return null;
                });

    }

    /**
     * Handles the action of the button to return to the main view.
     *
     * @param event Event triggered by the button.
     */
    public void onBackButtonClick(ActionEvent event) {
        Node source = (Node) event.getSource();
        Utils.switchView(source,
                "/com/matias/physiocarepsp/fxmlviews/first-view.fxml",
                "Welcome | PhysioCare");
    }
}

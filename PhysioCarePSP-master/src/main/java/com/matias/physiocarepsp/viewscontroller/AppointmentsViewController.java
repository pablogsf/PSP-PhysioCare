package com.matias.physiocarepsp.viewscontroller;

import com.google.api.services.gmail.Gmail;
import com.google.gson.Gson;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.matias.physiocarepsp.models.Appointment.*;
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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.logging.Logger;

import static com.matias.physiocarepsp.utils.Utils.showAlert;

public class AppointmentsViewController {

    @FXML private TableView<Appointment> tblAppointments;
    @FXML private TableColumn<Appointment, String> colDate;
    @FXML private TableColumn<Appointment, String> colPatient;
    @FXML private TableColumn<Appointment, String> colPhysio;
    @FXML private TableColumn<Appointment, String> colTreatment;
    @FXML private TableColumn<Appointment, Number> colPrice;

    @FXML private DatePicker dpDate;
    @FXML private ComboBox<Patient> cbPatient;
    @FXML private ComboBox<Physio> cbPhysio;
    @FXML private TextField txtTreatment, txtPrice, txtDiagnosis, txtObservation;

    private final ObservableList<Appointment> appointments = FXCollections.observableArrayList();
    private final Gson gson = new Gson();

    private static final Logger LOGGER = Logger.getLogger(AppointmentsViewController.class.getName());

    @FXML
    public void initialize() {
        // Fecha: formateamos LocalDateTime a String
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

        dpDate.valueProperty().addListener((obs, oldDate, newDate) -> {
            if(newDate != null && newDate.isAfter(LocalDate.now())) {
                txtObservation.setDisable(true);
                txtDiagnosis.setDisable(true);
            }else{
                txtObservation.setDisable(false);
                txtDiagnosis.setDisable(false);
            }
        });

        loadPatients();
        loadPhysios();
        loadAppointments();
    }

    private void loadPatients() {
        ServiceUtils.getResponseAsync(ServiceUtils.SERVER + "/patients", null, "GET")
                .thenApply(json -> gson.fromJson(json, PatientListResponse.class))
                .thenAccept(resp -> Platform.runLater(() ->
                        cbPatient.getItems().setAll(resp.getPatients())
                ));
    }

    private void loadPhysios() {
        ServiceUtils.getResponseAsync(ServiceUtils.SERVER + "/physios", null, "GET")
                .thenApply(json -> gson.fromJson(json, PhysioListResponse.class))
                .thenAccept(resp -> Platform.runLater(() ->
                        cbPhysio.getItems().setAll(resp.getPhysios())
                ));
    }

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

    @FXML
    public void onAddAppointment() {
        boolean isValid = true;
        boolean disabled = true;
        if(!txtDiagnosis.isDisable()){
            if(txtDiagnosis.getText().isEmpty() || txtDiagnosis.getText().isEmpty()){
                disabled = false;
            }
        }

        if(txtTreatment.getText().isEmpty() ||
                txtPrice.getText().isEmpty() ||
                cbPatient.getValue() == null ||
                cbPhysio.getValue() == null ||
                dpDate.getValue() == null){
            isValid = false;
        }

        if(isValid && disabled){

            checkIfPatientHasAppointmentsLeft(isOk ->{
                if(isOk){
                    System.out.println("Insertando cita");
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
                        payload.put("diagnosis", txtDiagnosis.getText().isEmpty() ? "por determinar en cita" : txtDiagnosis.getText());
                        payload.put("observations", txtObservation.getText().isEmpty() ? "por determinar en cita" : txtObservation.getText());
                        payload.put("price", Double.parseDouble(txtPrice.getText()));

                        String body = gson.toJson(payload);

                        insertAppointment(body);

                    } catch (NumberFormatException e) {
                        showAlert("Error", "Precio inválido", 2);
                    }

                    createPdf();
                }else{
                    System.out.println("No se puede insertar cita");
                }
            });

        }else{
            showAlert("Error", "Rellene todos los campos", 2);
        }
    }

    private void insertAppointment(String body) {
        ServiceUtils.getResponseAsync(
                        ServiceUtils.SERVER + "/records/appointments",
                        body,
                        "POST"
                )
                .thenApply(json -> gson.fromJson(json, AppointmentDtoResponse.class))
                .thenAccept(resp -> {
                    //Falta arreglar esto
                    if (resp.isOk()) {
                        loadAppointments();
                        Platform.runLater(()-> resetFormFields());
                    } else {
                        Platform.runLater(() ->
                                showAlert("Error", "Error creating Appointment", 2)
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
    }

    private void createPdf() {
        ServiceUtils.getResponseAsync(ServiceUtils.SERVER +"/records/"+cbPatient.getValue().getId()+"/appointments", null, "GET")
                .thenApply(json -> gson.fromJson(json, AppointmentListDto.class))
                .thenAccept(resp -> {
                    if (resp.isOk()) {
                        Platform.runLater(()->{
                            System.out.println(resp.getResult().size());
                            if(resp.getResult().size() >= 8 && resp.getResult().size() <= 10){
                                int appointmentsLeft = 10 - resp.getResult().size();
                                if(!resp.getResult().isEmpty()){
                                    String pdfPath = cbPatient.getValue().getName()+"-appointments.pdf";
                                    PdfDocument pdf = PDFUtil.createPdfDocument(resp.getResult(), pdfPath);
                                    if(pdf != null){
                                        try {
                                            Gmail service = EmailUtil.getService();
                                            MimeMessage emailContent = EmailUtil.createEmailWithAttachment(
                                                    "capitanadri@hotmail.com",
                                                    "capitanadri12@gmail.com",
                                                    "Appointments " + cbPatient.getValue().getName(),
                                                    "You have "+appointmentsLeft+" appointments left: " + cbPatient.getValue().getName(),
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

    public void onBackButtonClick(ActionEvent event) {
        Node source = (Node) event.getSource();
        Utils.switchView(source,
                "/com/matias/physiocarepsp/fxmlviews/first-view.fxml",
                "Welcome | PhysioCare");
    }

    private void resetFormFields() {
        dpDate.setValue(null);
        cbPatient.setValue(null);
        cbPhysio.setValue(null);
        txtTreatment.clear();
        txtPrice.clear();
        txtDiagnosis.clear();
        txtObservation.clear();
    }


    private void checkIfPatientHasAppointmentsLeft(Consumer<Boolean> callback){
        ServiceUtils.getResponseAsync(ServiceUtils.SERVER +"/records/"+cbPatient.getValue().getId()+"/appointments", null, "GET")
                .thenApply(json -> gson.fromJson(json, AppointmentListDto.class))
                .thenAccept(resp -> {
                    if (resp.isOk()) {
                        Platform.runLater(()->{
                            System.out.println(resp.getResult().size());
                            if(resp.getResult().size() > 10){
                                 callback.accept(false);
                                 showAlert("Error", "No tienes citas disponibles", 2);

                            }else{
                                callback.accept(true);
                            }
                        });
                    } else {
                        // alerta de error
                        Platform.runLater(()->{
                            System.out.println("Error: " + resp.getResult());
                            callback.accept(false);
                        });
                    }
                }).exceptionally(ex->{
                    // alerta de error
                    Platform.runLater(()->{
                        System.out.println("Error: " + ex.getMessage());
                        callback.accept(false);
                    });
                    return null;
                });
    }
}

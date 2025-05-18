package com.matias.physiocarepsp.viewscontroller;

import com.google.gson.*;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.matias.physiocarepsp.models.Record.RecordListResponse;
import com.matias.physiocarepsp.utils.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Node;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.matias.physiocarepsp.utils.Utils.showAlert;

/**
 * Controller for the first view of the application.
 * Provides navigation to other views such as Patients and Physios.
 */
public class FirstViewController {

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
                @Override
                public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                        throws JsonParseException {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");
                    return LocalDateTime.parse(json.getAsString(), formatter);
                }
            })
            .registerTypeAdapter(LocalDateTime.class, new JsonSerializer<LocalDateTime>() {
                @Override
                public JsonElement serialize(LocalDateTime src, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
                    return new JsonPrimitive(src.toString()); // ISO 8601 format
                }
            })
            .create();


    /**
     * Handles the action to open the Patients view.
     *
     * @param actionEvent the event triggered by the user
     */
    public void openPatientsAction(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        String fxmlFile = "/com/matias/physiocarepsp/fxmlviews/patients-view.fxml";
        String title = "Patients | PhysioCare";
        Utils.switchView(source, fxmlFile, title);
    }

    /**
     * Handles the action to open the Physios view.
     *
     * @param actionEvent the event triggered by the user
     */
    public void openPhysiosAction(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        String fxmlFile = "/com/matias/physiocarepsp/fxmlviews/physios-view.fxml";
        String title = "Physios | PhysioCare";
        Utils.switchView(source, fxmlFile, title);
    }

    /**
     * Handles the action to open the Calendar view.
     *
     * @param event the event triggered by the user
     */
    public void openCalendarAction(ActionEvent event) {
        Node source = (Node) event.getSource();
        String fxmlFile = "/com/matias/physiocarepsp/fxmlviews/calendar-view.fxml";
        String title = "Calendar | PhysioCare";
        Utils.switchView(source, fxmlFile, title);
    }

    /**
     * Handles the action to open the Appointments view.
     *
     * @param event the event triggered by the user
     */
    public void openAppointmentsAction(ActionEvent event) {
        Node source = (Node) event.getSource();
        String fxmlFile = "/com/matias/physiocarepsp/fxmlviews/appointments-view.fxml";
        String title = "Appointments | PhysioCare";
        Utils.switchView(source, fxmlFile, title);
    }

    public void btn_SaveAction(ActionEvent actionEvent) {
        String url = ServiceUtils.SERVER + "/records/";
        ServiceUtils.getResponseAsync(url, null, "GET")
                .thenApply(json -> gson.fromJson(json, RecordListResponse.class))
                .thenAccept(response -> {
                    if (!response.isError()) {
                        Platform.runLater(() -> {
                            PdfDocument pdf = PDFUtil.createRecordPdf(response.getRecords(), "Records.pdf");
                            if (pdf != null) {
                                showAlert("Success", "PDF generated successfully!", 1);

                                byte[] pdfBytes = null;
                                try {
                                    pdfBytes = PDFUtil.getPdfBytes("Records.pdf");
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }

                                SftpUploader.uploadAsync(
                                                "matiasborra.es", 22,
                                                "usuario", "contraseña",    // Insertar credenciales
                                                pdfBytes, "Records.pdf"
                                        )
                                        .thenAccept(success -> {
                                            Platform.runLater(() -> {
                                                if (success) {
                                                    showAlert("Success", "PDF uploaded successfully!", 1);
                                                } else {
                                                    showAlert("Error",   "Failed to upload PDF.", 2);
                                                }
                                            });
                                        });

                            } else {
                                showAlert("Error", "Failed to generate PDF.", 2);
                            }
                        });
                    } else {
                        Platform.runLater(() ->
                                showAlert("Error", response.getErrorMessage(), 2)
                        );
                    }
                })
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    Platform.runLater(() ->
                            showAlert("Error", "Failed to fetch records: " + ex.getMessage(), 2)
                    );
                    return null;
                });
    }

    public void logout(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        String fxmlFile = "/com/matias/physiocarepsp/fxmlviews/login-view.fxml";
        String title = "Login | PhysioCare";
        Utils.switchView(source, fxmlFile, title);
    }
}
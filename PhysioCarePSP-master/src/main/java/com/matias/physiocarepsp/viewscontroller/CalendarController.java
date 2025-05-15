package com.matias.physiocarepsp.viewscontroller;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import com.calendarfx.view.CalendarView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.matias.physiocarepsp.models.Appointment.AppointmentListResponse;
import com.matias.physiocarepsp.utils.LocalDateAdapter;
import com.matias.physiocarepsp.utils.ServiceUtils;
import com.matias.physiocarepsp.utils.Utils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

import static com.matias.physiocarepsp.utils.Utils.showAlert;

public class CalendarController implements Initializable {

    @FXML
    private CalendarView calendarView;
    @FXML
    private Button onBackButtonClick;

    private final Gson gson = new Gson().newBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();

    private final Calendar citasCalendar = new Calendar("Citas");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Configura fuente y aspecto
        CalendarSource source = new CalendarSource("Mis Citas");
        source.getCalendars().add(citasCalendar);
        calendarView.getCalendarSources().add(source);
        calendarView.setShowAddCalendarButton(false);
        citasCalendar.setStyle(Calendar.Style.STYLE1);

        // Carga datos
        loadAppointments();
    }

//    private void loadAppointments() {
//        String url = ServiceUtils.SERVER + "/records/appointments";
//        ServiceUtils.getResponseAsync(url, null, "GET")
//                .thenApply(json -> {
//                    System.out.println("JSON Response: " + json); // Imprimir la respuesta
//                    return gson.fromJson(json, AppointmentListResponse.class);
//                })
//                .thenAccept(response -> {
//                    if (!response.isError()) {
//                        Platform.runLater(() -> {
//                            System.out.println("Appointments loaded successfully");
//                            response.getAppointments().forEach(appointment -> {
//                                // Crear una entrada para cada cita
//                                com.calendarfx.model.Entry<String> entry = new com.calendarfx.model.Entry<>();
//                                entry.setTitle(appointment.getPatientName() + " - " + appointment.getDiagnosis());
//                                entry.setInterval(appointment.getDate());
//                                entry.setFullDay(false); // Configurar si es un evento de todo el día
//                                citasCalendar.addEntry(entry); // Añadir la entrada al calendario
//                            });
//                        });
//                    } else {
//                        System.out.println("Error: " + response.getErrorMessage());
//                        showAlert("Error", response.getErrorMessage(), 2);
//                    }
//                }).exceptionally(_ -> {
//                    System.out.println("Error fetching appointments");
//                    showAlert("Error", "Failed to fetch appointments", 2);
//                    return null;
//                });
//    }

    private void loadAppointments() {
        String url = ServiceUtils.SERVER + "/records/appointments";
        ServiceUtils.getResponseAsync(url, null, "GET")
                .thenApply(json -> {
                    System.out.println("JSON Response: " + json);
                    return gson.fromJson(json, AppointmentListResponse.class);
                })
                .thenAccept(response -> {
                    if (!response.isError()) {
                        Platform.runLater(() -> {
                            System.out.println("Appointments loaded successfully");
                            citasCalendar.clear(); // limpia entradas anteriores
                            response.getAppointments().forEach(a -> {
                                // Título del evento
                                String title = a.getPatientName() + " – " + a.getDiagnosis();
                                Entry<String> entry = new Entry<>(title);

                                // Rango de fecha/hora: usamos dateTime y +1h de duración
                                LocalDateTime start = a.getDateTime();
                                LocalDateTime end = start.plusHours(1);
                                entry.setInterval(start, end);

                                // No es todo el día
                                entry.setFullDay(false);

                                citasCalendar.addEntry(entry);
                            });
                        });
                    } else {
                        System.out.println("Error: " + response.getErrorMessage());
                        showAlert("Error", response.getErrorMessage(), 2);
                    }
                }).exceptionally(ex -> {
                    ex.printStackTrace();
                    showAlert("Error", "Failed to fetch appointments", 2);
                    return null;
                });
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
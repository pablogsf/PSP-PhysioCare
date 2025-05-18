package com.matias.physiocarepsp.viewscontroller;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import com.calendarfx.view.CalendarView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.matias.physiocarepsp.models.Appointment.AppointmentDto;
import com.matias.physiocarepsp.models.Appointment.AppointmentListDto;
import com.matias.physiocarepsp.utils.ServiceUtils;
import com.matias.physiocarepsp.utils.Utils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller for the calendar view in the application.
 * Handles loading appointments and navigation between views.
 */
public class CalendarController implements Initializable {

    /**
     * Calendar view that displays the appointments.
     */
    @FXML
    private CalendarView calendarView;

    /**
     * Button to return to the main view.
     */
    @FXML
    private Button onBackButtonClick;

    /**
     * Calendar containing the user's appointments.
     */
    private final Calendar citasCalendar = new Calendar("Appointments");

    /**
     * Gson object for JSON serialization and deserialization.
     */
    private final Gson gson = new GsonBuilder().create();

    /**
     * Initializes the controller and sets up the calendar view.
     *
     * @param location URL of the resource location.
     * @param resources Resources used for initialization.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        CalendarSource source = new CalendarSource("My Appointments");
        source.getCalendars().add(citasCalendar);
        calendarView.getCalendarSources().add(source);
        calendarView.setShowAddCalendarButton(false);
        citasCalendar.setStyle(Calendar.Style.STYLE1);
        loadAppointments();
    }

    /**
     * Loads the user's appointments from the server and displays them in the calendar.
     */
    private void loadAppointments() {
        String physioId = ServiceUtils.getUserId();
        if (physioId == null) {
            Utils.showAlert("Error", "No logged-in user.", 2);
            return;
        }

        String url = ServiceUtils.SERVER + "/records/physio/" + physioId + "/appointments";

        ServiceUtils.getResponseAsync(url, null, "GET")
                .thenApply(json -> gson.fromJson(json, AppointmentListDto.class))
                .thenAccept(dto -> {
                    if (!dto.isOk()) {
                        Platform.runLater(() ->
                                Utils.showAlert("Error", "Error loading appointments.", 2)
                        );
                        return;
                    }

                    List<Entry<String>> entries = new ArrayList<>();
                    for (AppointmentDto d : dto.getResult()) {
                        Entry<String> entry = new Entry<>(
                                d.getPatientName() + " – " + d.getDiagnosis()
                        );
                        LocalDateTime start = LocalDateTime.parse(
                                d.getDate(),
                                DateTimeFormatter.ISO_OFFSET_DATE_TIME
                        );
                        entry.setInterval(start, start.plusHours(1));
                        entry.setFullDay(false);
                        entries.add(entry);
                    }

                    Platform.runLater(() -> {
                        citasCalendar.clear();
                        entries.forEach(citasCalendar::addEntry);
                    });
                })
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    Platform.runLater(() ->
                            Utils.showAlert("Error", "Could not load appointments", 2)
                    );
                    return null;
                });
    }

    /**
     * Handles the action of the button to return to the main view.
     *
     * @param actionEvent Event triggered by the button.
     */
    public void onBackButtonClick(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        Utils.switchView(source,
                "/com/matias/physiocarepsp/fxmlviews/first-view.fxml",
                "Welcome | PhysioCare");
    }
}
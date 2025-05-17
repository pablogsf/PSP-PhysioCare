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

public class CalendarController implements Initializable {

    @FXML private CalendarView calendarView;
    @FXML private Button onBackButtonClick;

    private final Calendar citasCalendar = new Calendar("Citas");
    private final Gson gson = new GsonBuilder().create();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        CalendarSource source = new CalendarSource("Mis Citas");
        source.getCalendars().add(citasCalendar);
        calendarView.getCalendarSources().add(source);
        calendarView.setShowAddCalendarButton(false);
        citasCalendar.setStyle(Calendar.Style.STYLE1);

        loadAppointments();
    }

    private void loadAppointments() {
        String physioId = ServiceUtils.getUserId();
        if (physioId == null) {
            Utils.showAlert("Error", "No hay usuario logueado.", 2);
            return;
        }

        String url = ServiceUtils.SERVER
                + "/records/physio/" + physioId + "/appointments";

        ServiceUtils.getResponseAsync(url, null, "GET")
                // 1) Deserializamos en el DTO de lista
                .thenApply(json -> gson.fromJson(json, AppointmentListDto.class))
                .thenAccept(dto -> {
                    if (!dto.isOk()) {
                        Platform.runLater(() ->
                                Utils.showAlert("Error", "Error cargando citas.", 2)
                        );
                        return;
                    }

                    // 2) Transformamos cada DTO en una Entry del calendario
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

                    // 3) Actualizamos la vista en el hilo de UI
                    Platform.runLater(() -> {
                        citasCalendar.clear();
                        entries.forEach(citasCalendar::addEntry);
                    });
                })
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    Platform.runLater(() ->
                            Utils.showAlert("Error", "No se pudieron cargar las citas", 2)
                    );
                    return null;
                });
    }

    @FXML
    public void onBackButtonClick(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        Utils.switchView(source,
                "/com/matias/physiocarepsp/fxmlviews/first-view.fxml",
                "Welcome | PhysioCare");
    }
}

package com.matias.physiocarepsp.utils;

import com.google.gson.Gson;
import com.matias.physiocarepsp.models.Appointment.AppointmentListResponse;

/**
 * Service class for handling appointment-related operations.
 */
public class AppointmentService {
    private static final Gson gson = new Gson();

    /**
     * Retrieves all appointments from the server.
     *
     * @param token JWT authentication token.
     * @return Parsed AppointmentListResponse object.
     * @throws Exception If the request or parsing fails.
     */
    public static AppointmentListResponse fetchAll(String token) throws Exception {
        ServiceUtils.setToken(token);
        String json = ServiceUtils.getResponse(
                ServiceUtils.SERVER + "/records/appointments",
                null,
                "GET"
        );
        return gson.fromJson(json, AppointmentListResponse.class);
    }
}
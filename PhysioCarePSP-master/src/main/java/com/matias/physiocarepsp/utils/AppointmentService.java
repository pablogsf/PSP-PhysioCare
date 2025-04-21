package com.matias.physiocarepsp.utils;

import com.google.gson.Gson;
import com.matias.physiocarepsp.models.Appointment.AppointmentListResponse;

public class AppointmentService {
    private static final Gson gson = new Gson();

    /**
     * Obtiene todas las citas del servidor.
     *
     * @param token JWT de autenticación
     * @return objeto AppointmentListResponse parseado
     * @throws Exception si falla la petición o el parsing
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
package com.matias.physiocarepsp.models.Appointment;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * DTO que representa la respuesta del endpoint
 * GET /records/physio/:physio_id/appointments
 */
public class AppointmentListDto {

    @SerializedName("ok")
    private boolean ok;

    /**
     * En el JSON viene bajo "result"
     */
    @SerializedName("result")
    private List<AppointmentDto> result;

    // Getters y setters

    public boolean isOk() {
        return ok;
    }
    public void setOk(boolean ok) {
        this.ok = ok;
    }

    public List<AppointmentDto> getResult() {
        return result;
    }
    public void setResult(List<AppointmentDto> result) {
        this.result = result;
    }
}
package com.matias.physiocarepsp.models.Appointment;

import com.google.gson.annotations.SerializedName;

public class AppointmentDtoResponse {

    @SerializedName("ok")
    private boolean ok;

    @SerializedName("appointment")
    private AppointmentDto result;

    // Getters y setters

    public boolean isOk() {
        return ok;
    }
    public void setOk(boolean ok) {
        this.ok = ok;
    }

    public AppointmentDto getResult() {
        return result;
    }
    public void setResult(AppointmentDto result) {
        this.result = result;
    }
}

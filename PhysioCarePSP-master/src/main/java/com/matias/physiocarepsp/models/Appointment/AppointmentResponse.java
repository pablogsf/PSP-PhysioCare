package com.matias.physiocarepsp.models.Appointment;

import com.google.gson.annotations.SerializedName;
import com.matias.physiocarepsp.models.BaseResponse;

/**
 * Represents a response containing a single appointment.
 * Extends the BaseResponse class to include error handling.
 */
public class AppointmentResponse extends BaseResponse {
    @SerializedName("result")
    private Appointment result;

    /**
     * Retrieves the appointment from the response.
     *
     * @return the appointment object
     */
    public Appointment getAppointment() {
        return result;
    }

    /**
     * Sets the appointment in the response.
     *
     * @param result the appointment object
     */
    public void setAppointment(Appointment result) {
        this.result = result;
    }
}
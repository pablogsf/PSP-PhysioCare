package com.matias.physiocarepsp.models.Appointment;

import com.google.gson.annotations.SerializedName;
import com.matias.physiocarepsp.models.BaseResponse;

import java.util.List;

/**
 * Represents a response containing a list of appointments.
 * Extends the BaseResponse class to include error handling.
 */
public class AppointmentListResponse extends BaseResponse {
    private boolean ok;
    @SerializedName("result")
    private List<Appointment> result;

    public boolean isError() {
        return !ok;
    }

    /**
     * Retrieves the list of appointments from the response.
     *
     * @return a list of appointments
     */
    public List<Appointment> getAppointments() {
        return result;
    }

    /**
     * Sets the list of appointments in the response.
     *
     * @param result the list of appointments
     */
    public void setAppointments(List<Appointment> result) {
        this.result = result;
    }
}
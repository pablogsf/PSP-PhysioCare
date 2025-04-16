package com.matias.physiocarepsp.models.Patient;

import com.matias.physiocarepsp.models.BaseResponse;

/**
 * Represents a response containing a single patient.
 * Extends the BaseResponse class to include error handling.
 */
public class PatientResponse extends BaseResponse {

    private Patient result;

    /**
     * Retrieves the patient from the response.
     *
     * @return the patient object
     */
    public Patient getPatient() {
        return result;
    }

    /**
     * Sets the patient in the response.
     *
     * @param result the patient object
     */
    public void setPatient(Patient result) {
        this.result = result;
    }
}
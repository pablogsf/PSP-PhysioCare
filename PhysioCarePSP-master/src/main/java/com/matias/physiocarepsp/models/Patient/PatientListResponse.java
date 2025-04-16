package com.matias.physiocarepsp.models.Patient;

import com.matias.physiocarepsp.models.BaseResponse;

import java.util.List;

/**
 * Represents a response containing a list of patients.
 * Extends the BaseResponse class to include error handling.
 */
public class PatientListResponse extends BaseResponse {

    private List<Patient> result;

    /**
     * Retrieves the list of patients from the response.
     *
     * @return a list of patients
     */
    public List<Patient> getPatients() {
        return result;
    }

    /**
     * Sets the list of patients in the response.
     *
     * @param result the list of patients
     */
    public void setPatients(List<Patient> result) {
        this.result = result;
    }
}
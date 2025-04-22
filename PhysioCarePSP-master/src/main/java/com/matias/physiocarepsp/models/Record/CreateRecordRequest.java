package com.matias.physiocarepsp.models.Record;

import com.google.gson.annotations.SerializedName;

public class CreateRecordRequest {

    @SerializedName("patient")
    private String patientId;

    public CreateRecordRequest(String patientId) {
        this.patientId = patientId;
    }

    public String getPatientId() {
        return patientId;
    }
}

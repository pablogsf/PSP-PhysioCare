package com.matias.physiocarepsp.models.Appointment;

import com.google.gson.annotations.SerializedName;

/**
 * DTO que representa una cita tal como la devuelve el servidor.
 */
public class AppointmentDto {

    @SerializedName("id")
    private String id;

    @SerializedName("patientName")
    private String patientName;

    @SerializedName("physioName")
    private String physioName;

    @SerializedName("date")
    private String date;

    @SerializedName("diagnosis")
    private String diagnosis;

    @SerializedName("treatment")
    private String treatment;

    @SerializedName("observations")
    private String observations;

    /**
     * Puede no venir en todas las respuestas
     */
    @SerializedName("price")
    private Double price;


    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getPatientName() {
        return patientName;
    }
    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPhysioName() {
        return physioName;
    }
    public void setPhysioName(String physioName) {
        this.physioName = physioName;
    }

    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    public String getDiagnosis() {
        return diagnosis;
    }
    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getTreatment() {
        return treatment;
    }
    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    public String getObservations() {
        return observations;
    }
    public void setObservations(String observations) {
        this.observations = observations;
    }

    public Double getPrice() {
        return price;
    }
    public void setPrice(Double price) {
        this.price = price;
    }
}
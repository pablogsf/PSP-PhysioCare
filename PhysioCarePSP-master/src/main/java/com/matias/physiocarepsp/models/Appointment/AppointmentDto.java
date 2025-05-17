package com.matias.physiocarepsp.models.Appointment;

import com.google.gson.annotations.SerializedName;

/**
 * DTO que representa una cita tal como la devuelve el servidor.
 */
public class AppointmentDto {

    @SerializedName("appointmentId")
    private String appointmentId;

    @SerializedName("id")
    private String internalId;

    @SerializedName("date")
    private String date;

    @SerializedName("physio")
    private String physioId;

    @SerializedName("diagnosis")
    private String diagnosis;

    @SerializedName("treatment")
    private String treatment;

    @SerializedName("observations")
    private String observations;

    @SerializedName("price")
    private Double price;

    @SerializedName("patientName")
    private String patientName;

    @SerializedName("physioName")
    private String physioName;


    public String getAppointmentId() {
        return appointmentId;
    }
    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getId() {
        return internalId;
    }
    public void setId(String internalId) {
        this.internalId = internalId;
    }

    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    public String getPhysioId() {
        return physioId;
    }
    public void setPhysioId(String physioId) {
        this.physioId = physioId;
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
}

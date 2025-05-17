package com.matias.physiocarepsp.models.Appointment;

import com.google.gson.annotations.SerializedName;
import java.time.LocalDateTime;

/**
 * POJO para representar una cita.
 */
public class Appointment {

    @SerializedName("_id")
    private String id;

    private String patientId;
    private String patientName;
    private String physioId;
    private String physioName;
    @SerializedName("date")
    private LocalDateTime dateTime;
    private String diagnosis;
    private String treatment;
    private String observations;
    private double price;

    public Appointment() {
    }

    // ID
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    // Patient ID
    public String getPatientId() {
        return patientId;
    }
    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    // Patient name
    public String getPatientName() {
        return patientName;
    }
    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    // Physio ID
    public String getPhysioId() {
        return physioId;
    }
    public void setPhysioId(String physioId) {
        this.physioId = physioId;
    }

    // Physio name
    public String getPhysioName() {
        return physioName;
    }
    public void setPhysioName(String physioName) {
        this.physioName = physioName;
    }

    // Date and time
    public LocalDateTime getDateTime() {
        return dateTime;
    }
    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    // Diagnosis
    public String getDiagnosis() {
        return diagnosis;
    }
    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    // Treatment
    public String getTreatment() {
        return treatment;
    }
    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    // Observations
    public String getObservations() {
        return observations;
    }
    public void setObservations(String observations) {
        this.observations = observations;
    }

    // Price
    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Appointment{" +
                "id='" + id + '\'' +
                ", patientId='" + patientId + '\'' +
                ", patientName='" + patientName + '\'' +
                ", physioId='" + physioId + '\'' +
                ", physioName='" + physioName + '\'' +
                ", dateTime=" + dateTime +
                ", diagnosis='" + diagnosis + '\'' +
                ", treatment='" + treatment + '\'' +
                ", observations='" + observations + '\'' +
                ", price=" + price +
                '}';
    }
}

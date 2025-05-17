package com.matias.physiocarepsp.models.Appointment;
//DTO de Appointment
public class AppointmentRequest {
    private String patient;
    private String physio;
    private String date;
    private String diagnosis;
    private String treatment;
    private String observations;
    private double price;

    public AppointmentRequest(String patient, String physio, String date, String diagnosis, String treatment,
                              String observations, double price) {
        this.patient = patient;
        this.physio = physio;
        this.date = date;
        this.diagnosis = diagnosis;
        this.treatment = treatment;
        this.observations = observations;
        this.price = price;
    }

    public String getPatient() {
        return patient;
    }

    public void setPatient(String patient) {
        this.patient = patient;
    }

    public String getPhysio() {
        return physio;
    }

    public void setPhysio(String physio) {
        this.physio = physio;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}

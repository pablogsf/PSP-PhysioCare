package com.matias.physiocarepsp.models.Appointment;

import com.google.gson.annotations.SerializedName;
import java.time.LocalDateTime;

/**
 * POJO to represent an appointment.
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

    /**
     * Default constructor for Appointment.
     */
    public Appointment() {
    }

    /**
     * Gets the appointment ID.
     * @return the appointment ID.
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the appointment ID.
     * @param id the appointment ID.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the patient ID.
     * @return the patient ID.
     */
    public String getPatientId() {
        return patientId;
    }

    /**
     * Sets the patient ID.
     * @param patientId the patient ID.
     */
    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    /**
     * Gets the patient name.
     * @return the patient name.
     */
    public String getPatientName() {
        return patientName;
    }

    /**
     * Sets the patient name.
     * @param patientName the patient name.
     */
    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    /**
     * Gets the physiotherapist ID.
     * @return the physiotherapist ID.
     */
    public String getPhysioId() {
        return physioId;
    }

    /**
     * Sets the physiotherapist ID.
     * @param physioId the physiotherapist ID.
     */
    public void setPhysioId(String physioId) {
        this.physioId = physioId;
    }

    /**
     * Gets the physiotherapist name.
     * @return the physiotherapist name.
     */
    public String getPhysioName() {
        return physioName;
    }

    /**
     * Sets the physiotherapist name.
     * @param physioName the physiotherapist name.
     */
    public void setPhysioName(String physioName) {
        this.physioName = physioName;
    }

    /**
     * Gets the date and time of the appointment.
     * @return the date and time of the appointment.
     */
    public LocalDateTime getDateTime() {
        return dateTime;
    }

    /**
     * Sets the date and time of the appointment.
     * @param dateTime the date and time of the appointment.
     */
    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    /**
     * Gets the diagnosis of the appointment.
     * @return the diagnosis of the appointment.
     */
    public String getDiagnosis() {
        return diagnosis;
    }

    /**
     * Sets the diagnosis of the appointment.
     * @param diagnosis the diagnosis of the appointment.
     */
    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    /**
     * Gets the treatment of the appointment.
     * @return the treatment of the appointment.
     */
    public String getTreatment() {
        return treatment;
    }

    /**
     * Sets the treatment of the appointment.
     * @param treatment the treatment of the appointment.
     */
    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    /**
     * Gets the observations of the appointment.
     * @return the observations of the appointment.
     */
    public String getObservations() {
        return observations;
    }

    /**
     * Sets the observations of the appointment.
     * @param observations the observations of the appointment.
     */
    public void setObservations(String observations) {
        this.observations = observations;
    }

    /**
     * Gets the price of the appointment.
     * @return the price of the appointment.
     */
    public double getPrice() {
        return price;
    }

    /**
     * Sets the price of the appointment.
     * @param price the price of the appointment.
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * Returns a string representation of the appointment.
     * @return a string representation of the appointment.
     */
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
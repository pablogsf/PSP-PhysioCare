package com.matias.physiocarepsp.models.Appointment;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDate;

/**
 * Represents an appointment with details such as date, physiotherapist, diagnosis, treatment, and observations.
 */
public class Appointment {

    @SerializedName("_id")
    private String id;
    private LocalDate date;
    private String physio;
    private String diagnosis;
    private String treatment;
    private String observations;

    /**
     * Constructor to create an appointment without an ID.
     *
     * @param date         the date of the appointment
     * @param physio       the name of the physiotherapist
     * @param diagnosis    the diagnosis for the appointment
     * @param treatment    the treatment prescribed
     * @param observations additional observations
     */
    public Appointment(LocalDate date, String physio, String diagnosis, String treatment, String observations) {
        this.date = date;
        this.physio = physio;
        this.diagnosis = diagnosis;
        this.treatment = treatment;
        this.observations = observations;
    }

    /**
     * Constructor to create an appointment with an ID.
     *
     * @param id           the unique identifier of the appointment
     * @param date         the date of the appointment
     * @param physio       the name of the physiotherapist
     * @param diagnosis    the diagnosis for the appointment
     * @param treatment    the treatment prescribed
     * @param observations additional observations
     */
    public Appointment(String id, LocalDate date, String physio, String diagnosis, String treatment, String observations) {
        this.id = id;
        this.date = date;
        this.physio = physio;
        this.diagnosis = diagnosis;
        this.treatment = treatment;
        this.observations = observations;
    }

    /**
     * Gets the ID of the appointment.
     *
     * @return the appointment ID
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the ID of the appointment.
     *
     * @param id the appointment ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the date of the appointment.
     *
     * @return the appointment date
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Sets the date of the appointment.
     *
     * @param date the appointment date
     */
    public void setDate(LocalDate date) {
        this.date = date;
    }

    /**
     * Gets the name of the physiotherapist.
     *
     * @return the physiotherapist's name
     */
    public String getPhysio() {
        return physio;
    }

    /**
     * Sets the name of the physiotherapist.
     *
     * @param physio the physiotherapist's name
     */
    public void setPhysio(String physio) {
        this.physio = physio;
    }

    /**
     * Gets the diagnosis for the appointment.
     *
     * @return the diagnosis
     */
    public String getDiagnosis() {
        return diagnosis;
    }

    /**
     * Sets the diagnosis for the appointment.
     *
     * @param diagnosis the diagnosis
     */
    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    /**
     * Gets the treatment prescribed for the appointment.
     *
     * @return the treatment
     */
    public String getTreatment() {
        return treatment;
    }

    /**
     * Sets the treatment prescribed for the appointment.
     *
     * @param treatment the treatment
     */
    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    /**
     * Gets additional observations for the appointment.
     *
     * @return the observations
     */
    public String getObservations() {
        return observations;
    }

    /**
     * Sets additional observations for the appointment.
     *
     * @param observations the observations
     */
    public void setObservations(String observations) {
        this.observations = observations;
    }

    /**
     * Returns a string representation of the appointment.
     *
     * @return a string containing appointment details
     */
    @Override
    public String toString() {
        return "Appointment{" +
                "date=" + date +
                ", physio='" + physio + '\'' +
                ", diagnosis='" + diagnosis + '\'' +
                ", treatment='" + treatment + '\'' +
                ", observations='" + observations + '\'' +
                '}';
    }
}
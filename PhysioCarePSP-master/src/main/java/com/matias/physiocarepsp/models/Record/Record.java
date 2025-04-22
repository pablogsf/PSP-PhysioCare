package com.matias.physiocarepsp.models.Record;

import com.google.gson.annotations.SerializedName;
import com.matias.physiocarepsp.models.Appointment.Appointment;
import com.matias.physiocarepsp.models.Patient.Patient;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a medical record containing patient details, medical history, and associated appointments.
 */
public class Record {

    @SerializedName("_id")
    private String id;
    private Patient patient;
    private String medicalRecord;
    private List<Appointment> appointments;

    public Record() { }

    /**
     * Constructs a new Record without an ID.
     *
     * @param patient       the name of the patient
     * @param medicalRecord the medical history or record of the patient
     * @param appointments  the list of appointments associated with the patient
     */
    public Record(Patient patient, String medicalRecord, List<Appointment> appointments) {
        this.patient = patient;
        this.medicalRecord = medicalRecord;
        this.appointments = appointments;
    }

    /**
     * Constructs a new Record with an ID.
     *
     * @param id            the unique identifier of the record
     * @param patient       the name of the patient
     * @param medicalRecord the medical history or record of the patient
     * @param appointments  the list of appointments associated with the patient
     */
    public Record(String id, Patient patient, String medicalRecord, List<Appointment> appointments) {
        this.id = id;
        this.patient = patient;
        this.medicalRecord = medicalRecord;
        this.appointments = appointments;
    }

    /**
     * Retrieves the unique identifier of the record.
     *
     * @return the record ID
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the record.
     *
     * @param id the record ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Retrieves the name of the patient.
     *
     * @return the patient's name
     */
    public Patient getPatient() {
        return patient;
    }

    /**
     * Sets the name of the patient.
     *
     * @param patient the patient's name
     */
    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    /**
     * Retrieves the medical history or record of the patient.
     *
     * @return the medical record
     */
    public String getMedicalRecord() {
        return medicalRecord;
    }

    /**
     * Sets the medical history or record of the patient.
     *
     * @param medicalRecord the medical record
     */
    public void setMedicalRecord(String medicalRecord) {
        this.medicalRecord = medicalRecord;
    }

    /**
     * Retrieves the list of appointments associated with the patient.
     *
     * @return the list of appointments
     */
    public List<Appointment> getAppointments() {
        return appointments;
    }

    /**
     * Sets the list of appointments associated with the patient.
     *
     * @param appointments the list of appointments
     */
    public void setAppointments(List<Appointment> appointments) {
        this.appointments = appointments;
    }

    /**
     * Returns a string representation of the medical record.
     *
     * @return a string containing the patient's name, medical record, and appointments
     */
    @Override
    public String toString() {
        return "Record{" +
                "patient='" + patient + '\'' +
                ", medicalRecord='" + medicalRecord + '\'' +
                ", appointments=" + appointments +
                '}';
    }
}
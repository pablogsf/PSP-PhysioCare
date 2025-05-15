package com.matias.physiocarepsp.models.Appointment;

import com.google.gson.annotations.SerializedName;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDateTime;

/**
 * Represents an appointment with details such as date/time, patient, physiotherapist,
 * treatment, observations, and pricing information.
 */
public class Appointment {

    @SerializedName("_id")
    private String id;

    private final StringProperty patientName = new SimpleStringProperty();
    private final StringProperty patientId = new SimpleStringProperty();
    private final StringProperty physioName = new SimpleStringProperty();
    private final StringProperty physioId = new SimpleStringProperty();
    private final ObjectProperty<LocalDateTime> dateTime = new SimpleObjectProperty<>();
    private final StringProperty diagnosis = new SimpleStringProperty();
    private final StringProperty treatment = new SimpleStringProperty();
    private final StringProperty observations = new SimpleStringProperty();
    private final DoubleProperty price = new SimpleDoubleProperty();

    public Appointment() {
    }

    // ID
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    // Patient name
    public String getPatientName() {
        return patientName.get();
    }
    public void setPatientName(String patientName) {
        this.patientName.set(patientName);
    }
    public StringProperty patientNameProperty() {
        return patientName;
    }

    // Patient ID
    public String getPatientId() {
        return patientId.get();
    }
    public void setPatientId(String patientId) {
        this.patientId.set(patientId);
    }
    public StringProperty patientIdProperty() {
        return patientId;
    }

    // Physiotherapist name
    public String getPhysioName() {
        return physioName.get();
    }
    public void setPhysioName(String physioName) {
        this.physioName.set(physioName);
    }
    public StringProperty physioNameProperty() {
        return physioName;
    }

    // Physiotherapist ID
    public String getPhysioId() {
        return physioId.get();
    }
    public void setPhysioId(String physioId) {
        this.physioId.set(physioId);
    }
    public StringProperty physioIdProperty() {
        return physioId;
    }

    // Date and time
    public LocalDateTime getDateTime() {
        return dateTime.get();
    }
    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime.set(dateTime);
    }
    public ObjectProperty<LocalDateTime> dateTimeProperty() {
        return dateTime;
    }

    // Diagnosis
    public String getDiagnosis() {
        return diagnosis.get();
    }
    public void setDiagnosis(String diagnosis) {
        this.diagnosis.set(diagnosis);
    }
    public StringProperty diagnosisProperty() {
        return diagnosis;
    }

    // Treatment
    public String getTreatment() {
        return treatment.get();
    }
    public void setTreatment(String treatment) {
        this.treatment.set(treatment);
    }
    public StringProperty treatmentProperty() {
        return treatment;
    }

    // Observations
    public String getObservations() {
        return observations.get();
    }
    public void setObservations(String observations) {
        this.observations.set(observations);
    }
    public StringProperty observationsProperty() {
        return observations;
    }

    // Price
    public double getPrice() {
        return price.get();
    }
    public void setPrice(double price) {
        this.price.set(price);
    }
    public DoubleProperty priceProperty() {
        return price;
    }

    @Override
    public String toString() {
        return "Appointment{" +
                "id='" + id + '\'' +
                ", patientName='" + getPatientName() + '\'' +
                ", patientId='" + getPatientId() + '\'' +
                ", physioName='" + getPhysioName() + '\'' +
                ", physioId='" + getPhysioId() + '\'' +
                ", dateTime=" + getDateTime() +
                ", diagnosis='" + getDiagnosis() + '\'' +
                ", treatment='" + getTreatment() + '\'' +
                ", observations='" + getObservations() + '\'' +
                ", price=" + getPrice() +
                '}';
    }
}

package com.matias.physiocarepsp.models.Patient;

import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Represents a patient with personal details such as name, surname, birth date, address,
 * insurance number, and email.
 */
public class Patient {

    @SerializedName("_id")
    private String id;
    private String name;
    private String surname;
    private Date birthDate;
    private String address;
    private String insuranceNumber;
    private String email;

    /**
     * Constructs a new Patient without an ID.
     *
     * @param name            the patient's first name
     * @param surname         the patient's last name
     * @param birthDate       the patient's birth date
     * @param address         the patient's address
     * @param insuranceNumber the patient's insurance number
     * @param email           the patient's email address
     */
    public Patient(String name, String surname, Date birthDate, String address, String insuranceNumber, String email) {
        this.name = name;
        this.surname = surname;
        this.birthDate = birthDate;
        this.address = address;
        this.insuranceNumber = insuranceNumber;
        this.email = email;
    }

    /**
     * Constructs a new Patient with an ID.
     *
     * @param id              the unique identifier of the patient
     * @param name            the patient's first name
     * @param surname         the patient's last name
     * @param birthDate       the patient's birth date
     * @param address         the patient's address
     * @param insuranceNumber the patient's insurance number
     * @param email           the patient's email address
     */
    public Patient(String id, String name, String surname, Date birthDate, String address, String insuranceNumber, String email) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.birthDate = birthDate;
        this.address = address;
        this.insuranceNumber = insuranceNumber;
        this.email = email;
    }

    /**
     * Retrieves the patient's first name.
     *
     * @return the patient's first name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the patient's first name.
     *
     * @param name the patient's first name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Retrieves the patient's last name.
     *
     * @return the patient's last name
     */
    public String getSurname() {
        return surname;
    }

    /**
     * Sets the patient's last name.
     *
     * @param surname the patient's last name
     */
    public void setSurname(String surname) {
        this.surname = surname;
    }

    /**
     * Retrieves the patient's birth date.
     *
     * @return the patient's birth date
     */
    public Date getBirthDate() {
        return birthDate;
    }

    /**
     * Sets the patient's birth date.
     *
     * @param birthDate the patient's birth date
     */
    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    /**
     * Retrieves the patient's address.
     *
     * @return the patient's address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the patient's address.
     *
     * @param address the patient's address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Retrieves the patient's insurance number.
     *
     * @return the patient's insurance number
     */
    public String getInsuranceNumber() {
        return insuranceNumber;
    }

    /**
     * Sets the patient's insurance number.
     *
     * @param insuranceNumber the patient's insurance number
     */
    public void setInsuranceNumber(String insuranceNumber) {
        this.insuranceNumber = insuranceNumber;
    }

    /**
     * Retrieves the patient's unique identifier.
     *
     * @return the patient's ID
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the patient's unique identifier.
     *
     * @param id the patient's ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Retrieves the patient's email address.
     *
     * @return the patient's email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the patient's email address.
     *
     * @param email the patient's email address
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns a string representation of the patient.
     *
     * @return a string containing the patient's name, surname, birth date, and insurance number
     */
    @Override
    public String toString() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        return name + " " + surname + " | " + formatter.format(birthDate) + " | " + insuranceNumber;
    }
}
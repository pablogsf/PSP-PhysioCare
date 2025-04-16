package com.matias.physiocarepsp.models.Physio;

import com.google.gson.annotations.SerializedName;

/**
 * Represents a physiotherapist with personal and professional details such as name, surname,
 * specialty, license number, and email.
 */
public class Physio {

    @SerializedName("_id")
    private String id;
    private String name;
    private String surname;
    private String specialty;
    private String licenseNumber;
    private String email;

    /**
     * Constructs a new Physio without an ID.
     *
     * @param name          the physiotherapist's first name
     * @param surname       the physiotherapist's last name
     * @param licenseNumber the physiotherapist's license number
     * @param specialty     the physiotherapist's specialty
     * @param email         the physiotherapist's email address
     */
    public Physio(String name, String surname, String licenseNumber, String specialty, String email) {
        this.name = name;
        this.surname = surname;
        this.licenseNumber = licenseNumber;
        this.specialty = specialty;
        this.email = email;
    }

    /**
     * Constructs a new Physio with an ID.
     *
     * @param id            the unique identifier of the physiotherapist
     * @param name          the physiotherapist's first name
     * @param surname       the physiotherapist's last name
     * @param specialty     the physiotherapist's specialty
     * @param licenseNumber the physiotherapist's license number
     * @param email         the physiotherapist's email address
     */
    public Physio(String id, String name, String surname, String specialty, String licenseNumber, String email) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.specialty = specialty;
        this.licenseNumber = licenseNumber;
        this.email = email;
    }

    /**
     * Retrieves the physiotherapist's unique identifier.
     *
     * @return the physiotherapist's ID
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the physiotherapist's unique identifier.
     *
     * @param id the physiotherapist's ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Retrieves the physiotherapist's first name.
     *
     * @return the physiotherapist's first name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the physiotherapist's first name.
     *
     * @param name the physiotherapist's first name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Retrieves the physiotherapist's license number.
     *
     * @return the physiotherapist's license number
     */
    public String getLicenseNumber() {
        return licenseNumber;
    }

    /**
     * Sets the physiotherapist's license number.
     *
     * @param licenseNumber the physiotherapist's license number
     */
    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    /**
     * Retrieves the physiotherapist's specialty.
     *
     * @return the physiotherapist's specialty
     */
    public String getSpecialty() {
        return specialty;
    }

    /**
     * Sets the physiotherapist's specialty.
     *
     * @param specialty the physiotherapist's specialty
     */
    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    /**
     * Retrieves the physiotherapist's last name.
     *
     * @return the physiotherapist's last name
     */
    public String getSurname() {
        return surname;
    }

    /**
     * Sets the physiotherapist's last name.
     *
     * @param surname the physiotherapist's last name
     */
    public void setSurname(String surname) {
        this.surname = surname;
    }

    /**
     * Retrieves the physiotherapist's email address.
     *
     * @return the physiotherapist's email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the physiotherapist's email address.
     *
     * @param email the physiotherapist's email address
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns a string representation of the physiotherapist.
     *
     * @return a string containing the physiotherapist's name, surname, license number, and specialty
     */
    @Override
    public String toString() {
        return name + " " + surname + " | " + licenseNumber + " | " + specialty;
    }
}
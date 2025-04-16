package com.matias.physiocarepsp.models.Auth;

/**
 * Represents a login request containing user credentials.
 * Used to authenticate a user by providing a login and password.
 */
public class LoginRequest {

    private String login;
    private String password;

    /**
     * Constructs a new LoginRequest with the specified login and password.
     *
     * @param login    the user's login or username
     * @param password the user's password
     */
    public LoginRequest(String login, String password) {
        this.login = login;
        this.password = password;
    }

    /**
     * Retrieves the user's login or username.
     *
     * @return the login or username
     */
    public String getLogin() {
        return login;
    }

    /**
     * Sets the user's login or username.
     *
     * @param login the login or username
     */
    public void setLogin(String login) {
        this.login = login;
    }

    /**
     * Retrieves the user's password.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the user's password.
     *
     * @param password the password
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
package com.matias.physiocarepsp.models.Auth;

/**
 * Represents the response for an authentication request.
 * Contains information about the success of the request and the authentication token.
 */
public class AuthResponse {

    private boolean ok;
    private String token;

    /**
     * Checks if the authentication request was successful.
     *
     * @return true if the request was successful, false otherwise
     */
    public boolean isOk() {
        return ok;
    }

    /**
     * Retrieves the authentication token from the response.
     *
     * @return the authentication token
     */
    public String getToken() {
        return token;
    }
}
package com.matias.physiocarepsp.models;

/**
 * Base response class for handling API responses.
 * This class provides methods to check for errors and retrieve error messages.
 */
public class BaseResponse {

    private String error;

    /**
     * Checks if the response contains an error.
     *
     * @return true if there is an error, false otherwise
     */
    public boolean isError() {
        return error != null;
    }

    /**
     * Retrieves the error message from the response.
     *
     * @return the error message, or null if no error exists
     */
    public String getErrorMessage() {
        return error;
    }
}
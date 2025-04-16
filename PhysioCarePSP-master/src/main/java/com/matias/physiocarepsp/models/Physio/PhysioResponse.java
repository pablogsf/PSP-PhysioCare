package com.matias.physiocarepsp.models.Physio;

import com.matias.physiocarepsp.models.BaseResponse;

/**
 * Represents a response containing a single physiotherapist.
 * Extends the BaseResponse class to include error handling.
 */
public class PhysioResponse extends BaseResponse {

    private Physio result;

    /**
     * Retrieves the physiotherapist from the response.
     *
     * @return the physiotherapist object
     */
    public Physio getPhysio() {
        return result;
    }

    /**
     * Sets the physiotherapist in the response.
     *
     * @param result the physiotherapist object
     */
    public void setPhysio(Physio result) {
        this.result = result;
    }
}
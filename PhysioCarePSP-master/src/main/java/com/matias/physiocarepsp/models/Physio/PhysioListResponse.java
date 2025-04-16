package com.matias.physiocarepsp.models.Physio;

import com.matias.physiocarepsp.models.BaseResponse;

import java.util.List;

/**
 * Represents a response containing a list of physiotherapists.
 * Extends the BaseResponse class to include error handling.
 */
public class PhysioListResponse extends BaseResponse {

    private List<Physio> result;

    /**
     * Retrieves the list of physiotherapists from the response.
     *
     * @return a list of physiotherapists
     */
    public List<Physio> getPhysios() {
        return result;
    }

    /**
     * Sets the list of physiotherapists in the response.
     *
     * @param result the list of physiotherapists
     */
    public void setPhysios(List<Physio> result) {
        this.result = result;
    }
}
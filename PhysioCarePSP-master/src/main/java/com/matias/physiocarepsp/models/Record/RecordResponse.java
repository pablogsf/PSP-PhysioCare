package com.matias.physiocarepsp.models.Record;

import com.matias.physiocarepsp.models.BaseResponse;

/**
 * Represents a response containing a single medical record.
 * Extends the BaseResponse class to include error handling.
 */
public class RecordResponse extends BaseResponse {

    private Record result;

    /**
     * Retrieves the medical record from the response.
     *
     * @return the medical record object
     */
    public Record getRecord() {
        return result;
    }

    /**
     * Sets the medical record in the response.
     *
     * @param result the medical record object
     */
    public void setRecord(Record result) {
        this.result = result;
    }
}
package com.matias.physiocarepsp.models.Record;

import com.matias.physiocarepsp.models.BaseResponse;

import java.util.List;

/**
 * Represents a response containing a list of medical records.
 * Extends the BaseResponse class to include error handling.
 */
public class RecordListResponse extends BaseResponse {

    private List<Record> result;

    /**
     * Retrieves the list of medical records from the response.
     *
     * @return a list of medical records
     */
    public List<Record> getRecords() {
        return result;
    }

    /**
     * Sets the list of medical records in the response.
     *
     * @param result the list of medical records
     */
    public void setRecords(List<Record> result) {
        this.result = result;
    }
}
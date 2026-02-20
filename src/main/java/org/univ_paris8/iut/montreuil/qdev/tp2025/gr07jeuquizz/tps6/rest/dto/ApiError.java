package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.rest.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
public class ApiError {

    private int status;
    private String error;
    private String message;
    private List<String> details;
    private Date timestamp;

    public ApiError() {
        this.timestamp = new Date();
        this.details = new ArrayList<>();
    }

    public ApiError(int status, String error, String message) {
        this();
        this.status = status;
        this.error = error;
        this.message = message;
    }

    public ApiError(int status, String error, String message, List<String> details) {
        this(status, error, message);
        this.details = details;
    }
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getDetails() {
        return details;
    }

    public void setDetails(List<String> details) {
        this.details = details;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}


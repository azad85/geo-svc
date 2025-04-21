package com.example.geosvc.dto;

public class ErrorResponse {
    private String path;
    private String message;
    private String error;
    private int status;

    public ErrorResponse() {
    }

    public ErrorResponse(String path, String message, String error, int status) {
        this.path = path;
        this.message = message;
        this.error = error;
        this.status = status;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
} 
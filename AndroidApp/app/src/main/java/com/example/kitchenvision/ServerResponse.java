package com.example.kitchenvision;

public class ServerResponse {

    private boolean success;
    private String message;

    // Default constructor
    public ServerResponse() {
    }

    // Constructor with fields
    public ServerResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    // Getters and setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

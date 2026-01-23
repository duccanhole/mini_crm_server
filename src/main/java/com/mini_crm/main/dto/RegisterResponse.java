package com.mini_crm.main.dto;

public class RegisterResponse {
    private String message;
    private String email;
    private boolean success;

    public RegisterResponse() {
    }

    public RegisterResponse(String message, String email, boolean success) {
        this.message = message;
        this.email = email;
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}

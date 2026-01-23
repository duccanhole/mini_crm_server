package com.mini_crm.main.dto;

public class SuccessResponse<T> {
    private String message;
    private int status;
    private T data;
    private long timestamp;

    public SuccessResponse() {
        this.message = "Success";
        this.status = 200;
        this.timestamp = System.currentTimeMillis();
    }

    public SuccessResponse(T data) {
        this.message = "Success";
        this.status = 200;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    public SuccessResponse(String message, int status, T data) {
        this.message = message;
        this.status = status;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}

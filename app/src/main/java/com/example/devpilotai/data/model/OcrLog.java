package com.example.devpilotai.data.model;

import java.io.Serializable;

public class OcrLog implements Serializable {
    private String id;
    private String userEmail;
    private String status; // "SUCCESS", "FAILED"
    private String errorMessage;
    private long timestamp;
    private String resultSnippet; // A preview of the recognized text

    public OcrLog() {
        // Required for Firestore
    }

    public OcrLog(String userEmail, String status, String errorMessage, long timestamp, String resultSnippet) {
        this.userEmail = userEmail;
        this.status = status;
        this.errorMessage = errorMessage;
        this.timestamp = timestamp;
        this.resultSnippet = resultSnippet;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getResultSnippet() { return resultSnippet; }
    public void setResultSnippet(String resultSnippet) { this.resultSnippet = resultSnippet; }
}

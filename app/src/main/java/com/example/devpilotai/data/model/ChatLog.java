package com.example.devpilotai.data.model;

import java.io.Serializable;

public class ChatLog implements Serializable {
    private String id;
    private String userEmail;
    private String prompt;
    private String response;
    private String status; // "SUCCESS", "FAILED"
    private String errorMessage;
    private long timestamp;
    private int tokens;

    public ChatLog() {
        // Required for Firestore
    }

    public ChatLog(String userEmail, String prompt, String response, String status, String errorMessage, long timestamp, int tokens) {
        this.userEmail = userEmail;
        this.prompt = prompt;
        this.response = response;
        this.status = status;
        this.errorMessage = errorMessage;
        this.timestamp = timestamp;
        this.tokens = tokens;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getPrompt() { return prompt; }
    public void setPrompt(String prompt) { this.prompt = prompt; }

    public String getResponse() { return response; }
    public void setResponse(String response) { this.response = response; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public int getTokens() { return tokens; }
    public void setTokens(int tokens) { this.tokens = tokens; }
}

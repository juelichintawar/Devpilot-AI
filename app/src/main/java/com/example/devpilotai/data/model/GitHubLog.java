package com.example.devpilotai.data.model;

import java.io.Serializable;

public class GitHubLog implements Serializable {
    private String id;
    private String userEmail;
    private String action; // e.g., "LOGIN", "FETCH_REPOS", "ANALYZE_REPO", "LOGOUT"
    private String details; // e.g., Repo name or error message
    private String status; // "SUCCESS", "FAILED"
    private long timestamp;

    public GitHubLog() {
        // Required for Firestore
    }

    public GitHubLog(String userEmail, String action, String details, String status, long timestamp) {
        this.userEmail = userEmail;
        this.action = action;
        this.details = details;
        this.status = status;
        this.timestamp = timestamp;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}

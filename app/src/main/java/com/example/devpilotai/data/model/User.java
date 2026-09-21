package com.example.devpilotai.data.model;

import java.io.Serializable;

public class User implements Serializable {
    private String uid;
    private String email;
    private String displayName;
    private String status; // "Active", "Deactivated"
    private String role;   // "User", "Admin"
    private long createdAt;
    private boolean isGitHubConnected;

    public User() {
        // Required for Firestore
    }

    public User(String uid, String email, String displayName, String status, String role, long createdAt) {
        this.uid = uid;
        this.email = email;
        this.displayName = displayName;
        this.status = status;
        this.role = role;
        this.createdAt = createdAt;
        this.isGitHubConnected = false;
    }

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public boolean isGitHubConnected() { return isGitHubConnected; }
    public void setGitHubConnected(boolean gitHubConnected) { isGitHubConnected = gitHubConnected; }
}

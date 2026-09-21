package com.example.devpilotai.data.model;

import java.io.Serializable;

public class AdminLog implements Serializable {
    private String id;
    private String adminEmail;
    private String action; // e.g., "LOGIN", "USER_DEACTIVATED", "USER_DELETED", "SETTINGS_CHANGED"
    private String details;
    private String target; // Email or ID of the affected item/user
    private long timestamp;

    public AdminLog() {
        // Required for Firestore
    }

    public AdminLog(String adminEmail, String action, String details, String target, long timestamp) {
        this.adminEmail = adminEmail;
        this.action = action;
        this.details = details;
        this.target = target;
        this.timestamp = timestamp;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getAdminEmail() { return adminEmail; }
    public void setAdminEmail(String adminEmail) { this.adminEmail = adminEmail; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public String getTarget() { return target; }
    public void setTarget(String target) { this.target = target; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}

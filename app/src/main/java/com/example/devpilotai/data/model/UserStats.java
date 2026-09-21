package com.example.devpilotai.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_stats")
public class UserStats {
    @PrimaryKey
    private int id = 1; // Only one row for global stats
    
    private long totalTokensUsed;
    private int totalSnippetsSaved;
    private int totalOcrScans;
    private int totalNotesCreated;

    public UserStats() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getTotalTokensUsed() {
        return totalTokensUsed;
    }

    public void setTotalTokensUsed(long totalTokensUsed) {
        this.totalTokensUsed = totalTokensUsed;
    }

    public int getTotalSnippetsSaved() {
        return totalSnippetsSaved;
    }

    public void setTotalSnippetsSaved(int totalSnippetsSaved) {
        this.totalSnippetsSaved = totalSnippetsSaved;
    }

    public int getTotalOcrScans() {
        return totalOcrScans;
    }

    public void setTotalOcrScans(int totalOcrScans) {
        this.totalOcrScans = totalOcrScans;
    }

    public int getTotalNotesCreated() {
        return totalNotesCreated;
    }

    public void setTotalNotesCreated(int totalNotesCreated) {
        this.totalNotesCreated = totalNotesCreated;
    }
}

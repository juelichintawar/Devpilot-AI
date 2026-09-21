package com.example.devpilotai.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "challenge_results")
public class ChallengeResult {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int challengeId;
    private String userEmail; // For future Firebase sync
    private long completionTime; // Time taken in seconds
    private long timestamp;
    private boolean isSuccess;
    private int score;

    public ChallengeResult(int challengeId, String userEmail, long completionTime, long timestamp, boolean isSuccess, int score) {
        this.challengeId = challengeId;
        this.userEmail = userEmail;
        this.completionTime = completionTime;
        this.timestamp = timestamp;
        this.isSuccess = isSuccess;
        this.score = score;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getChallengeId() { return challengeId; }
    public String getUserEmail() { return userEmail; }
    public long getCompletionTime() { return completionTime; }
    public long getTimestamp() { return timestamp; }
    public boolean isSuccess() { return isSuccess; }
    public int getScore() { return score; }
}

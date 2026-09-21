package com.example.devpilotai.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "challenges")
public class Challenge {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String description;
    private String difficulty; // Easy, Medium, Hard
    private String category;
    private String question;
    private String correctAnswer;
    private int timeLimit; // in seconds

    public Challenge(String title, String description, String difficulty, String category, String question, String correctAnswer, int timeLimit) {
        this.title = title;
        this.description = description;
        this.difficulty = difficulty;
        this.category = category;
        this.question = question;
        this.correctAnswer = correctAnswer;
        this.timeLimit = timeLimit;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getDifficulty() { return difficulty; }
    public String getCategory() { return category; }
    public String getQuestion() { return question; }
    public String getCorrectAnswer() { return correctAnswer; }
    public int getTimeLimit() { return timeLimit; }
}

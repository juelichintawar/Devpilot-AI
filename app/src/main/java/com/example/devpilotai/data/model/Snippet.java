package com.example.devpilotai.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "snippets")
public class Snippet {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String content;
    private String category;
    private String tags; // Comma separated tags
    private boolean isFavorite;
    private long createdAt;

    public Snippet(String title, String content, String category, String tags, boolean isFavorite, long createdAt) {
        this.title = title;
        this.content = content;
        this.category = category;
        this.tags = tags;
        this.isFavorite = isFavorite;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}

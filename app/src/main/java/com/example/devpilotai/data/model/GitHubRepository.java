package com.example.devpilotai.data.model;

import com.google.gson.annotations.SerializedName;

public class GitHubRepository {
    @SerializedName("id")
    private long id;

    @SerializedName("name")
    private String name;

    @SerializedName("full_name")
    private String fullName;

    @SerializedName("description")
    private String description;

    @SerializedName("owner")
    private GitHubUser owner;

    @SerializedName("html_url")
    private String htmlUrl;

    @SerializedName("stargazers_count")
    private int stargazersCount;

    @SerializedName("forks_count")
    private int forksCount;

    @SerializedName("language")
    private String language;

    @SerializedName("updated_at")
    private String updatedAt;

    public long getId() { return id; }
    public String getName() { return name; }
    public String getFullName() { return fullName; }
    public String getDescription() { return description; }
    public GitHubUser getOwner() { return owner; }
    public String getHtmlUrl() { return htmlUrl; }
    public int getStargazersCount() { return stargazersCount; }
    public int getForksCount() { return forksCount; }
    public String getLanguage() { return language; }
    public String getUpdatedAt() { return updatedAt; }
}

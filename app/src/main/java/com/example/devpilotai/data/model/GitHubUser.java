package com.example.devpilotai.data.model;

import com.google.gson.annotations.SerializedName;

public class GitHubUser {
    @SerializedName("login")
    private String login;

    @SerializedName("name")
    private String name;

    @SerializedName("avatar_url")
    private String avatarUrl;

    @SerializedName("bio")
    private String bio;

    @SerializedName("followers")
    private int followers;

    @SerializedName("following")
    private int following;

    @SerializedName("public_repos")
    private int publicRepos;

    @SerializedName("html_url")
    private String htmlUrl;

    public String getLogin() { return login; }
    public String getName() { return name; }
    public String getAvatarUrl() { return avatarUrl; }
    public String getBio() { return bio; }
    public int getFollowers() { return followers; }
    public int getFollowing() { return following; }
    public int getPublicRepos() { return publicRepos; }
    public String getHtmlUrl() { return htmlUrl; }
}

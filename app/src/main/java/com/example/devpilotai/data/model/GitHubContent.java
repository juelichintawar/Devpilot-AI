package com.example.devpilotai.data.model;

import com.google.gson.annotations.SerializedName;

public class GitHubContent {
    public String name;
    public String path;
    public String sha;
    public long size;
    public String url;
    @SerializedName("html_url")
    public String htmlUrl;
    @SerializedName("git_url")
    public String gitUrl;
    @SerializedName("download_url")
    public String downloadUrl;
    public String type; // "file" or "dir"
    public String content; // base64 encoded content
    public String encoding;

    public boolean isFile() {
        return "file".equals(type);
    }

    public boolean isDir() {
        return "dir".equals(type);
    }
}

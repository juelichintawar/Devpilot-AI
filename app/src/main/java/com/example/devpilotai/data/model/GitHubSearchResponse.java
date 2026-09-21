package com.example.devpilotai.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class GitHubSearchResponse {
    @SerializedName("total_count")
    private int totalCount;

    @SerializedName("items")
    private List<GitHubRepo> items;

    public int getTotalCount() { return totalCount; }
    public List<GitHubRepo> getItems() { return items; }
}

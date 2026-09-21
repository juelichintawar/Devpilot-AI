package com.example.devpilotai.data.model;

import com.google.gson.annotations.SerializedName;

public class GitHubBranch {
    @SerializedName("name")
    private String name;

    @SerializedName("commit")
    private BranchCommit commit;

    public String getName() { return name; }
    public BranchCommit getCommit() { return commit; }

    public static class BranchCommit {
        @SerializedName("sha")
        private String sha;

        @SerializedName("url")
        private String url;

        public String getSha() { return sha; }
        public String getUrl() { return url; }
    }
}

package com.example.devpilotai.data.model;

import com.google.gson.annotations.SerializedName;

public class GitHubCommit {
    @SerializedName("sha")
    private String sha;

    @SerializedName("commit")
    private CommitDetail commit;

    @SerializedName("author")
    private GitHubUser author;

    public String getSha() { return sha; }
    public CommitDetail getCommit() { return commit; }
    public GitHubUser getAuthor() { return author; }

    public static class CommitDetail {
        @SerializedName("message")
        private String message;

        @SerializedName("author")
        private AuthorInfo author;

        public String getMessage() { return message; }
        public AuthorInfo getAuthor() { return author; }
    }

    public static class AuthorInfo {
        @SerializedName("name")
        private String name;

        @SerializedName("date")
        private String date;

        public String getName() { return name; }
        public String getDate() { return date; }
    }
}

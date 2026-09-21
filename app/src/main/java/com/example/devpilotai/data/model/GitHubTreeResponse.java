package com.example.devpilotai.data.model;

import java.util.List;

public class GitHubTreeResponse {
    public String sha;
    public String url;
    public List<TreeEntry> tree;
    public boolean truncated;

    public static class TreeEntry {
        public String path;
        public String mode;
        public String type; // "blob" (file) or "tree" (directory)
        public String sha;
        public long size;
        public String url;
    }
}

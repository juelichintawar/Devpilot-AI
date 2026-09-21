package com.example.devpilotai.data.model;

public class SearchResult {
    public enum Type {
        SNIPPET, NOTE, CHAT, REPOSITORY
    }

    private final String id;
    private final String title;
    private final String content;
    private final Type type;
    private final Object originalObject;

    public SearchResult(String id, String title, String content, Type type, Object originalObject) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.type = type;
        this.originalObject = originalObject;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public Type getType() { return type; }
    public Object getOriginalObject() { return originalObject; }
}

package com.myassistant;

public class Document {
    private String title;
    private String content;
    private String[] tokens;

    public Document(String title, String content) {
        this.title = title;
        this.content = content;
        this.tokens = tokenize(content);
    }

    private String[] tokenize(String text) {
        // Lowercase + split on non-letter characters
        return text.toLowerCase().replaceAll("[^a-z ]", " ").split("\\s+");
    }

    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String[] getTokens() { return tokens; }
}


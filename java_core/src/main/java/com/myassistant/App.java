package com.myassistant;

public class App {
    public static void main(String[] args) {
        try {
            String text = FileLoader.loadFile("data/sample.txt");
            Document doc = new Document("Sample Document", text);
            System.out.println("Title: " + doc.getTitle());
            System.out.println("Number of tokens: " + doc.getTokens().length);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

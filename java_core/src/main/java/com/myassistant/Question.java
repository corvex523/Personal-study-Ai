package com.myassistant;

public class Question {
    String text;
    String keyWord;

    public Question(String text, String keyWord) {
        this.text = text;
        this.keyWord = keyWord;
    }

    public boolean checkAnswer(String answer) {
        return answer.equals(keyWord);
    }

    public String getText() { return text; }
    public String toString() { return text; }
}
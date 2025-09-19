package com.myassistant;
import java.util.ArrayList;

public class DocumentIndex {
    private final Document document;
    private ArrayList<Integer> index;

    public DocumentIndex(Document document) {
        this.document = document;
        this.index = new ArrayList<>();
    }

    public DocumentIndex(Document document, int index){
	this.document = document;
	this.index = new ArrayList<>();
	this.index.add(index);
    }

    public void addIndex(Integer integer) {
        index.add(integer);
    }

    @Override
    public String toString() {
    String title = document.toString();
    return "occurs " + index.size() + " times in " + title + " at " + index;
    }

    public Document getDocument() { return document; }
    public ArrayList<Integer> getIndex() { return index; }
}


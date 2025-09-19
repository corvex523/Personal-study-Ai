package com.myassistant;
import java.util.ArrayList;
import java.util.HashMap;

public class InvertedIndex {
    private static HashMap<String, ArrayList<DocumentIndex>> index = new HashMap<>();

    public static void addWord(String word, Integer integer, Document doc) {
        ArrayList<DocumentIndex> list = index.getOrDefault(word, new ArrayList<>());

        boolean found = false;
        for (DocumentIndex di : list) {
            if (di.getDocument().equals(doc)) {
                di.addIndex(integer);
                found = true;
                break;
            }
        }

        if (!found) {
	        DocumentIndex di = new DocumentIndex(doc);
	        di.addIndex(integer);
            list.add(di);
        }
	
        index.put(word, list);
    }

    public static ArrayList<DocumentIndex> search(String search) {
	String[] terms = search.toLowerCase().split("[^a-z0-9']+");
	ArrayList<DocumentIndex> firstList = index.getOrDefault(terms[0], new ArrayList<>());
	if(terms.length < 2){
	    return firstList;
	}
	ArrayList<DocumentIndex> out = new ArrayList<>();
	for(DocumentIndex di : firstList){
	    Document doc = di.getDocument();
	    boolean docAdded = false;
	    ArrayList<Integer> positions = di.getIndex();
	    for(int pos : positions){
		boolean connected = true;
		int currentPos = pos;
		for(int i = 1; i < terms.length; i++){
		    ArrayList<DocumentIndex> locations = index.getOrDefault(terms[i], new ArrayList<>());
		    DocumentIndex next = locations.stream().filter(p -> p.getDocument().equals(doc)).findFirst().orElse(null);
		    if(next == null || !next.getIndex().contains(currentPos+1)){
			connected = false;
	            }
		    currentPos++;
		}
		if(connected){
		    if(!docAdded){
			DocumentIndex phraseIndex = new DocumentIndex(doc, currentPos);
			out.add(phraseIndex);
			docAdded = true;
		    } else {
			DocumentIndex phraseIndex = out.get(out.size()-1);
			phraseIndex.addIndex(currentPos);
			out.set(out.size()-1, phraseIndex);
		    }
		}
	    }
	}
        return out;
    }
}


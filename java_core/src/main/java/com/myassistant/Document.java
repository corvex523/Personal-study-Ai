package com.myassistant;
import java.util.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

public class Document {
    private String title;
    private String content;
    private String[] tokens;
    private ArrayList<Sentence> sentences;
    private HashMap<String, Integer> wordCount;
    private static int totalDocuments = 0;
    private HashMap<String, Double> tfidf;
    Set<String> stopWords = new HashSet<>(Arrays.asList(
    "a", "about", "above", "after", "again", "against", "all", "also", "am",
    "an", "and", "any", "are", "aren't", "as", "at", "be", "because",
    "been", "before", "being", "below", "between", "both", "but", "by",
    "can't", "cannot", "could", "couldn't", "did", "didn't", "do",
    "does", "doesn't", "doing", "don't", "down", "during", "each",
    "few", "for", "from", "further", "had", "hadn't", "has", "hasn't",
    "have", "haven't", "having", "he", "he'd", "he'll", "he's", "her",
    "here", "here's", "hers", "herself", "him", "himself", "his",
    "how", "how's", "i", "i'd", "i'll", "i'm", "i've", "if", "in",
    "into", "is", "isn't", "it", "it's", "its", "itself", "let's",
    "me", "more", "most", "mustn't", "my", "myself", "new", "no", "nor", 
    "not", "of", "off", "on", "once", "only", "or", "other", "ought",
    "our", "ours", "ourselves", "out", "over", "own", "s", "same", "shan't",
    "she", "she'd", "she'll", "she's", "should", "shouldn't", "so",
    "some", "such", "than", "that", "that's", "the", "their", "theirs",
    "them", "themselves", "then", "there", "there's", "these", "they",
    "they'd", "they'll", "they're", "they've", "this", "those",
    "through", "to", "too", "under", "until", "up", "very", "was",
    "wasn't", "will", "we", "we'd", "we'll", "we're", "we've", "were",
    "weren't", "what", "what's", "when", "when's", "where", "where's",
    "which", "while", "who", "who's", "whom", "why", "why's", "with",
    "won't", "would", "wouldn't", "you", "you'd", "you'll", "you're",
    "you've", "your", "yours", "yourself", "yourselves"
    ));

    public Document(String title, String content) {
        this.title = title;
        this.content = content;
	    wordCount = new HashMap<String, Integer>();
        tokens = tokenize(content);
	    int count = 0;
        for(String token : tokens){
            if (token.isEmpty()) continue;
            count++;
            InvertedIndex.addWord(token, count, this);
        }
        sentences = splitSentences(content);
        tfidf = new HashMap<>();
        totalDocuments++;
        for (String token : tokens) {
            if(tfidf.containsKey(token)) continue;
            if(stopWords.contains(token)) {
                tfidf.put(token, 0.0);
                continue;
            }
            int timesInDoc = wordCount.getOrDefault(token, 0);
            int docsWithTerm = InvertedIndex.search(token).size();
            double tf = timesInDoc / (double) tokens.length;
            double idf = (double) Math.log(1.0 + totalDocuments / (1.0 + docsWithTerm));
            tfidf.put(token, tf * idf);
        }
    }

    private String[] tokenize(String text) {
        String[] tokens = text.toLowerCase().split("[^a-z0-9']+");
	    for(String token : tokens){
            if(token.isEmpty()) continue;
            wordCount.put(token, wordCount.getOrDefault(token, 0) + 1);
	    }
	    return tokens;
    }

    private ArrayList<Sentence> splitSentences(String text) {
        ArrayList<Sentence> result = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        int sentenceIndex = 0;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            sb.append(c);

            if (c == '.' || c == '?' || c == '!') {
                int j = i + 1;
                while (j < text.length() && (text.charAt(j) == ' ' || text.charAt(j) == '\n')) {
                    j++;
                }

                if (j < text.length() && Character.isUpperCase(text.charAt(j))) {
                    String sentence = sb.toString().trim();
                    if (!sentence.isEmpty()) {
                        if (isAbbreviation(sentence)) {
                            sb.append(' ');
                            continue;
                        }
                        result.add(new Sentence(sentence, sentenceIndex++));
                    }
                    sb.setLength(0);
                    i = j - 1; // skip whitespace
                }
            } else if (i < text.length() - 1 && c == '\n' && text.charAt(i+1) == '\n') {
		String sentence = sb.toString().trim();
		if (!sentence.isEmpty()) {
		    if (isAbbreviation(sentence)) {
			sb.append(' ');
        	    } else {
       			result.add(new Sentence(sentence, sentenceIndex++));
            		sb.setLength(0);
        	    }
		}
            } else if (title.toLowerCase().contains("vocab") && c == '\n') {
		String sentence = sb.toString().trim();
                if (!sentence.isEmpty()) {
                    if (isAbbreviation(sentence)) { 
                        sb.append(' ');
                    } else {
                        result.add(new Sentence(sentence, sentenceIndex++));
                        sb.setLength(0);
                    }
                }
	    }
        }

        if (sb.length() > 0) {
            String sentence = sb.toString().trim();
            if (!sentence.isEmpty()) {
                result.add(new Sentence(sentence, sentenceIndex++));
            }
        }

        return result;
    }

    private boolean isAbbreviation(String str) {
	if (str.contains(" "))
	    str = str.substring(str.lastIndexOf(" "));
	if(str.length() == 1 && Character.isUpperCase(str.charAt(0))) return true;
	String[] abbreviations = {"Mr", "Mrs", "Ms", "Dr", "Prof", "Sr", "Jr", "St", "Lt", "Col", "Gen", "Rev", "Capt", "vs", "Maj", "Sgt", "Ex", "U", "S", "i", "e", "g", "etc", "a", "m", "p",  "Ph", "ed"};
	for(String abbreviation : abbreviations) {
	    if(str.substring(str.lastIndexOf(" ")+1).equals(abbreviation)) return true;
	}
	return false;
    }

    public String getSummary() {
        return buildStringS(summarize());
    }

    public String getQuiz() {
        return buildStringQ(getQuestions(summarize()));
    }

    public String getQuestionsAsJson() {
        System.out.println("getting questions");
        ObjectMapper mapper = new ObjectMapper();
        try {
            ArrayList<Question> temp = getQuestions(summarize());
            System.out.println(temp);
            return mapper.writeValueAsString(temp);
        } catch (JsonProcessingException e) {
            System.out.println(e);
            return null;
        }
        
    }

    private ArrayList<Question> getQuestions(ArrayList<Sentence> sentences) {
        ArrayList<Question> questions = new ArrayList<>();
        for(Sentence s : sentences) {
            HashMap<String, Double> map = s.getTfidfMap();
            String keyWord = Collections.max(map.entrySet(), Map.Entry.comparingByValue()).getKey();
            StringBuilder text = new StringBuilder();
            for(String str : s.getText().toLowerCase().split("[^a-z0-9']+")) {
                text.append(str.equals(keyWord) ? "____" : str);
                text.append(" ");
            }
            Question question = new Question(text.toString(), keyWord);
            questions.add(question);
        }
        return questions;
    }

    private ArrayList<Sentence> summarize() {
	System.out.println("doc size " + sentences.size() + " sentences");

	ArrayList<Sentence> proseFiltered = proseFilter();
	System.out.println("prose filtering removed " + (sentences.size() - proseFiltered.size()) + " sentences");
        System.out.println("sentences left " + proseFiltered.size());

	ArrayList<Sentence> lengthFiltered = lengthFilter(proseFiltered);
	System.out.println("length filtering removed " + (sentences.size() - lengthFiltered.size()) + " sentences");
	System.out.println("sentences left " + lengthFiltered.size());

	Queue<Sentence> topSentences = getTopSentences(lengthFiltered);
	int topSentencesSize = topSentences.size();
	System.out.println("sentences left after ordering " + topSentencesSize);
		
	ArrayList<Sentence> cosignFiltered = cosignFilter(topSentences);
	System.out.println("cosign filtering removed " + (topSentencesSize - cosignFiltered.size()) + " sentences");
	System.out.println("sentences left " + cosignFiltered.size());

	ArrayList<Sentence> keyWordFiltered = keyWordFilter(cosignFiltered);
	System.out.println("keyWord filtering removed " + (cosignFiltered.size() - keyWordFiltered.size()) + " sentences");
	System.out.println("sentences left " + keyWordFiltered.size());

	return keyWordFiltered;
    }

    private ArrayList<Sentence> proseFilter() {
	ArrayList<Sentence> out = new ArrayList<Sentence>();
	for (Sentence s : sentences) {
	    boolean prose = true;
	    String text = s.getText();
	    if(text.contains("http")) { prose = false; } 
	    else if(text.contains("[↩]")) { prose = false; } 
	    else if(text.contains("wiki")) { prose = false; }
	    if(prose) out.add(s);
	}
	return out;
    }

    private ArrayList<Sentence> lengthFilter(ArrayList<Sentence> sentences){
	ArrayList<Sentence> out = new ArrayList<Sentence>();
	for (Sentence sentence : sentences) {
            String[] tokens = sentence.getText().toLowerCase().split("[^a-z0-9']+");
            if(tokens.length > 6)
		out.add(sentence);
	}
	return out;
    }

    private Queue<Sentence> getTopSentences(ArrayList<Sentence> sentences) {
	for (Sentence sentence : sentences) {
            ArrayList<Double> tfidfVector = new ArrayList<>();
            for (String str : sentence.getText().toLowerCase().split("[^a-z0-9']+")) {
                if(!str.isEmpty())
                    tfidfVector.add(tfidf.get(str));
            }
            sentence.setTfidfVector(tfidfVector);
        }

        HashMap<Sentence, Double> sentenceTfidf = new HashMap<>();
        for (Sentence sentence : sentences) {
            double tempTfidf = 0;
            for (Double tfidf : sentence.getTfidfVector()) {
                tempTfidf += tfidf;
            }
            sentenceTfidf.put(sentence, tempTfidf);
        }

        PriorityQueue<Sentence> pq = new PriorityQueue<>(Comparator.comparingDouble(sentenceTfidf::get).reversed());
        for (Sentence sentence : sentences) {
            pq.add(sentence);
        }

        return pq;
    }

    private ArrayList<Sentence> cosignFilter(Queue<Sentence> topSentences) {
	ArrayList<Sentence> filtered = new ArrayList<>();
        while (!topSentences.isEmpty()) {
            Sentence s = topSentences.poll();
            boolean similar = false;
            for (Sentence f : filtered) {
                if (cosineSimilarity(s, f) > 0.95) {
                    similar = true;
                    break;
                }
            }
            if (!similar) filtered.add(s);
        }
        return filtered;
    }

    private double cosineSimilarity(Sentence a, Sentence b) {
	HashMap<String, Double> va = a.getTfidfMap();
	HashMap<String, Double> vb = b.getTfidfMap();
	Set<String> vocab = new LinkedHashSet<>();
	vocab.addAll(va.keySet());
	vocab.addAll(vb.keySet());

	Double dot = 0.0, na = 0.0, nb = 0.0;
	for (String w : vocab) {
	    Double x = va.getOrDefault(w, 0.0);
	    Double y = vb.getOrDefault(w, 0.0);
	    dot += x * y;
	    na  += x * x;
	    nb  += y * y;
	}
	double result = (na == 0 || nb == 0) ? 0.0 : dot / (Math.sqrt(na) * Math.sqrt(nb));
//	System.out.println(result);	
	return result;
    }

    private ArrayList<Sentence> keyWordFilter(ArrayList<Sentence> filtered) {
        ArrayList<Sentence> keyWordFiltered = new ArrayList<>();
        HashSet<String> keyWords = new HashSet<>();
        for(Sentence s : filtered) {
            HashMap<String, Double> map = s.getTfidfMap();
            String keyWord = Collections.max(map.entrySet(), Map.Entry.comparingByValue()).getKey();
            if(!keyWords.contains(keyWord)) {
                keyWords.add(keyWord);
		// System.out.println(keyWord);
                keyWordFiltered.add(s);
            }
        }
        return keyWordFiltered;
    }

    private String buildStringS(ArrayList<Sentence> list){
        ArrayList<String> list2 = new ArrayList<>();
        for(Sentence s : list){
            list2.add(s.getText());
        }
        return bs(list2);
    }

    private String buildStringQ(ArrayList<Question> list){
        ArrayList<String> list2 = new ArrayList<>();
        for(Question q : list){
            list2.add(q.getText());
        }
        return bs(list2);
    }

    private String bs(ArrayList<String> list) {
	StringBuilder out = new StringBuilder();
    int num = Math.min(list.size(), Math.max(sentences.size()/20, 5));
    System.out.println("printing " + num + " sentences");
	out.append("\n");
        for(int i = 0; i < num; i++) {
            out.append(list.get(i)).append("\n\n");
        }
        return out.toString();
    }

    public String toString() {
	return title;
    }

    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String[] getTokens() { return tokens; }
    public ArrayList<Sentence> getSentences() { return sentences; }
}


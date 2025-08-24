import java.util.*;

public class Document {
    private String title;
    private String content;
    private String[] tokens;
    private ArrayList<Sentence> sentences;
    private HashMap<String, Integer> wordCount;
    private static int totalDocuments = 0;

    public Document(String title, String content) {
        this.title = title;
        this.content = content;
	this.wordCount = new HashMap<String, Integer>();
        this.tokens = tokenize(content);
	int count = 0;
	for(String token : tokens){
	    if (token.isEmpty()) continue;
	    count++;
	    InvertedIndex.addWord(token, count, this);
	}
	this.sentences = splitSentences(content);
	totalDocuments++;
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
	String[] parts = text.split("(?<=[.?!]+)[\n\s\"]+(?=[A-Z])");
	int sentenceIndex = 0;
	for (int i = 0; i < parts.length; i++) {
	    String part = parts[i].trim();
	    if (isAbbreviation(part)) {
		if (i + 1 < parts.length) {
		    parts[i+1] = part + " " + parts[i+1];
                }
            } else if (!part.isEmpty()) {
                result.add(new Sentence(part, sentenceIndex++));
            }
        }
	return result;
    }

    private boolean isAbbreviation(String str) {
	if(str.length() == 1 && Character.isUpperCase(str.charAt(0))) return true;
	String[] abbreviations = {"Mr", "Mrs", "Ms", "Dr", "Prof", "Sr", "Jr", "St", "Lt", "Col", "Gen", "Rev", "Capt", "Maj", "Sgt", "U", "S", "i", "e", "g", "etc", "a", "m", "p",  "Ph"};
	for(String abbreviation : abbreviations) {
	    if(str.equals(abbreviation)) return true;
	}
	return false;
    }

    public String summarize() {
        for (Sentence sentence : sentences) {
            ArrayList<Double> tfidfVector = new ArrayList<>();
            for (String str : sentence.getText().split("[^a-z0-9']+")) {
                int timesInDoc = wordCount.getOrDefault(str, 0);
                int docsWithTerm = InvertedIndex.search(str).size();
                double tf = timesInDoc / (double) tokens.length;
                double idf = Math.log(totalDocuments / (1.0 + docsWithTerm));
                tfidfVector.add(tf * idf);
            }
            sentence.setTfidfVector(tfidfVector);
        }

        HashMap<Sentence, Double> sentenceTfidf = new HashMap<>();
        for (Sentence sentence : sentences) {
            double tempTfidf = 0;
            for (Double tfidf : sentence.getTfidfVector()) {
                tempTfidf += tfidf;
            }
            tempTfidf /= sentence.getTfidfVector().size();
            sentenceTfidf.put(sentence, tempTfidf);
        }

        PriorityQueue<Sentence> pq = new PriorityQueue<>(Comparator.comparingDouble(sentenceTfidf::get).reversed());
        for (Sentence sentence : sentences) {
            pq.add(sentence);
        }

        ArrayList<Sentence> filtered = new ArrayList<>();
        while (!pq.isEmpty()) {
            Sentence s = pq.poll();
            boolean similar = false;
            for (Sentence f : filtered) {
                if (cosineSimilarity(s, f) > 0.7) {
                    similar = true;
                    break;
                }
            }
            if (!similar) filtered.add(s);
        }

        while (filtered.size() > sentences.size()/10) {
            filtered.remove(filtered.size()-1);
        }

        return filtered.toString();
    }

    private double cosineSimilarity(Sentence a, Sentence b) {
	HashMap<String, Double> va = a.getTfidfMap();   // word -> weight
	HashMap<String, Double> vb = b.getTfidfMap();
	Set<String> vocab = new LinkedHashSet<>();
	vocab.addAll(va.keySet());
	vocab.addAll(vb.keySet());

	double dot = 0, na = 0, nb = 0;
	for (String w : vocab) {
	    double x = va.getOrDefault(w, 0.0);
	    double y = vb.getOrDefault(w, 0.0);
	    dot += x * y;
	    na  += x * x;
	    nb  += y * y;
	}
	return (na == 0 || nb == 0) ? 0.0 : dot / (Math.sqrt(na) * Math.sqrt(nb));
    }


    public String toString() {
	return title;
    }

    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String[] getTokens() { return tokens; }
    public ArrayList<Sentence> getSentences() { return sentences; }
}


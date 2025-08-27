import java.util.*;

public class Document {
    private String title;
    private String content;
    private String[] tokens;
    private ArrayList<Sentence> sentences;
    private HashMap<String, Integer> wordCount;
    private static int totalDocuments = 0;
    private HashMap<String, Double> tfidf;
    private static final HashSet<String> stopWords = new HashSet<>(Arrays.asList(
    "a", "an", "the", "and", "or", "but", "if", "while", "as", "because", "since", "so", "although", "though",
    "on", "in", "at", "by", "for", "with", "about", "against", "between", "into", "through", "during", "before",
    "after", "above", "below", "to", "from", "up", "down", "out", "off", "over", "under", "again", "further",
    "then", "once", "here", "there", "when", "where", "why", "how", "all", "any", "both", "each", "few", "more",
    "most", "other", "some", "such", "no", "nor", "not", "only", "own", "same", "so", "than", "too", "very",
    "can", "will", "just", "don", "should", "now", "is", "are", "was", "were", "be", "been", "being", "have",
    "has", "had", "do", "does", "did", "am", "may", "might", "must", "shall", "would", "could", "should",
    "i", "you", "he", "she", "it", "we", "they", "me", "him", "her", "us", "them", "my", "your", "his", "her",
    "its", "our", "their", "mine", "yours", "hers", "ours", "theirs"));

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
            int timesInDoc = wordCount.get(token);
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
	String[] parts = text.split("(?<=[.?!\n]+)[\n\s](?=[A-Z])|[\n](?=[A-Z])");
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
	String[] abbreviations = {"Mr", "Mrs", "Ms", "Dr", "Prof", "Sr", "Jr", "St", "Lt", "Col", "Gen", "Rev", "Capt", "vs", "Maj", "Sgt", "Ex", "U", "S", "i", "e", "g", "etc", "a", "m", "p",  "Ph"};
	for(String abbreviation : abbreviations) {
	    if(str.equals(abbreviation)) return true;
	}
	return false;
    }

    public String summarize() {
        for (Sentence sentence : sentences) {
            ArrayList<Double> tfidfVector = new ArrayList<>();
            for (String str : sentence.getText().toLowerCase().split("[^a-z0-9']+")) {
		if(!stopWords.contains(str
))
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
            tempTfidf /= (sentence.getTfidfVector().size());
	    tempTfidf *= sentence.getTfidfVector().size();
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

        while (filtered.size() > sentences.size()/20) {
            filtered.remove(filtered.size()-1);
        }

	StringBuilder out = new StringBuilder();
	for (Sentence s : filtered) {
            out.append(s.toString()).append("\n\n");
    	}

        return out.toString();
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


    public String toString() {
	return title;
    }

    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String[] getTokens() { return tokens; }
    public ArrayList<Sentence> getSentences() { return sentences; }
}


import java.util.ArrayList;
import java.util.HashMap;

public class Sentence {
    String text;
    int position;
    ArrayList<Double> tfidfVector;
    
    public Sentence(String text, int position) {
        this.text = text;
        this.position = position;
    }

    public void setTfidfVector(ArrayList<Double> vector) {
        this.tfidfVector = vector;
    }

    public HashMap<String, Double> getTfidfMap() {
	HashMap<String, Double> tfidfMap = new HashMap<>();
	String[] words = text.split("[^a-z0-9']+");
        for (int i = 0; i < words.length && i < tfidfVector.size(); i++) {
            String word = words[i].toLowerCase();
            if (!word.isEmpty()) {
                tfidfMap.put(word, tfidfVector.get(i));
            }
        }
	return tfidfMap;
    }
    public String getText() { return text; }
    public ArrayList<Double> getTfidfVector() { return tfidfVector; }
    public int getPosition() { return position; }
    
    @Override
    public String toString() { return text; }
}

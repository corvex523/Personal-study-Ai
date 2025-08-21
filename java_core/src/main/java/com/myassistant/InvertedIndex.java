import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InvertedIndex {
    private static Map<String, List<DocumentFrequency>> index = new HashMap<>();

    public static void addWord(String word, Document doc) {
        List<DocumentFrequency> list = index.getOrDefault(word, new ArrayList<>());

        boolean found = false;
        for (DocumentFrequency df : list) {
            if (df.getDocument().equals(doc)) {
                df.increment();
                found = true;
                break;
            }
        }

        if (!found) {
            list.add(new DocumentFrequency(doc));
        }

        index.put(word, list);
    }

    public static List<DocumentFrequency> getDocuments(String search) {
        return index.getOrDefault(search, new ArrayList<>());
    }
}


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InvertedIndex {
    private static Map<String, List<DocumentIndex>> index = new HashMap<>();

    public static void addWord(String word, Integer integer, Document doc) {
        List<DocumentIndex> list = index.getOrDefault(word, new ArrayList<>());

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

    public static List<DocumentIndex> getDocuments(String search) {
        return index.getOrDefault(search, new ArrayList<>());
    }
}


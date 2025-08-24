import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class App {
    public static void main(String[] args) {
        try {
	    ArrayList<Document> docs = new ArrayList<>();
	    long startTime = System.currentTimeMillis();
	    
            List<Path> files = FileLoader.getAllTextFiles("/Users/johnmiller/Personal-study-Ai/java_core/data/");
	    
            for (Path file : files) {
                String content = FileLoader.readFile(file);
		String fileName = file.toString();
		int start = fileName.lastIndexOf("/") + 1;
		int end = fileName.lastIndexOf(".");
		String title = fileName.substring(start, end);
		Document doc = new Document(title, content);
		docs.add(doc);
            }
	    System.out.println("sentence indexing " + (System.currentTimeMillis()-startTime) + " ms");
	    
	    String search = "a new";
            System.out.print(search + " ");
            System.out.println(InvertedIndex.search(search));
	    
	    for(Document doc : docs)
		if(doc.getTitle().equals("APBiologyChemistryOfLife"))
		    System.out.println(doc.summarize());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

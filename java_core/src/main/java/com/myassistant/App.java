import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class App {
    public static void main(String[] args) {
        try {
            List<Path> files = FileLoader.getAllTextFiles("/Users/johnmiller/Personal-study-Ai/java_core/data/");

            for (Path file : files) {
                String content = FileLoader.readFile(file);
		String fileName = file.toString();
		int start = fileName.lastIndexOf("/") + 1;
		int end = fileName.lastIndexOf(".");
		String title = fileName.substring(start, end);
		Document doc = new Document(title, content);
            }
	    
	    String search = "ab";
	    System.out.print(search + " ");
	    System.out.println(InvertedIndex.getDocuments(search));
	    search = "a";
            System.out.print(search + " ");
            System.out.println(InvertedIndex.getDocuments(search));
            

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

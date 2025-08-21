public class Document {
    private String title;
    private String content;
    private String[] tokens;

    public Document(String title, String content) {
        this.title = title;
        this.content = content;
        this.tokens = tokenize(content);
	int count = 0;
	for(String token : tokens){
        if (token.isEmpty()) continue;
	    count++;
	    InvertedIndex.addWord(token, count, this);
	}
    }

    private String[] tokenize(String text) {
        // Lowercase + split on non-letter characters
        return text.toLowerCase().split("[^a-z0-9']+");
    }

    public String toString() {
	return title;
    }

    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String[] getTokens() { return tokens; }
}


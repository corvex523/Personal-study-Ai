public class DocumentFrequency {
    private final Document document;
    private int frequency;

    public DocumentFrequency(Document document) {
        this.document = document;
        this.frequency = 1;
    }

    public void increment() {
        frequency++;
    }

    @Override
    public String toString() {
    String title = document.toString();
	if(frequency == 1)
	    return "orrurs 1 time in " + title;
	return "occurs " + frequency + " times in " + title;
    }

    public Document getDocument() { return document; }
    public int getFrequency() { return frequency; }
}


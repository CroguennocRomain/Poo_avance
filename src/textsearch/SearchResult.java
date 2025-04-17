package textsearch;

public class SearchResult {
    private final String filePath;
    private final int lineNumber;
    private final String lineContent;

    public SearchResult(String filePath, int lineNumber, String lineContent) {
        this.filePath = filePath;
        this.lineNumber = lineNumber;
        this.lineContent = lineContent;
    }

    public String getFilePath() {
        return filePath;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public String getLineContent() {
        return lineContent;
    }
}

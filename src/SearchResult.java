public class SearchResult {
    private final String filePath;
    private final int lineNumber;
    private final String lineContent;
    private final boolean isApproximate;

    public SearchResult(String filePath, int lineNumber, String lineContent, boolean isApproximate) {
        this.filePath = filePath;
        this.lineNumber = lineNumber;
        this.lineContent = lineContent;
        this.isApproximate = isApproximate;
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

    public boolean isApproximate() {
        return isApproximate;
    }
}

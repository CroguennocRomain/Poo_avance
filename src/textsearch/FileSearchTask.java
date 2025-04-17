package textsearch;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class FileSearchTask implements Callable<List<SearchResult>> {
    private final Path file;
    private final String term;

    public FileSearchTask(Path file, String term) {
        this.file = file;
        this.term = term;
    }

    @Override
    public List<SearchResult> call() {
        List<SearchResult> matches = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.contains(term)) {
                    matches.add(new SearchResult(file.toString(), lineNumber, line));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return matches;
    }
}
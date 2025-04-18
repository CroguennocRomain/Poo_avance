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
    private final int tolerance;

    public FileSearchTask(Path file, String term) {
        this(file, term, 1); // tolérance par défaut : 1
    }

    public FileSearchTask(Path file, String term, int tolerance) {
        this.file = file;
        this.term = term;
        this.tolerance = tolerance;
    }

    @Override
    public List<SearchResult> call() {
        List<SearchResult> matches = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                String[] words = line.split("\\W+"); // découpe en mots

                for (String word : words) {
                    int distance = levenshteinDistance(word.toLowerCase(), term.toLowerCase());
                    if (distance == 0) {
                        matches.add(new SearchResult(file.toString(), lineNumber, line, false)); // exact
                        break;
                    } else if (distance <= tolerance) {
                        matches.add(new SearchResult(file.toString(), lineNumber, line, true)); // approx.
                        break;
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return matches;
    }

    private int levenshteinDistance(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];

        for (int i = 0; i <= s1.length(); i++) {
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else {
                    int cost = s1.charAt(i - 1) == s2.charAt(j - 1) ? 0 : 1;
                    dp[i][j] = Math.min(
                            Math.min(dp[i - 1][j] + 1,
                                    dp[i][j - 1] + 1),
                            dp[i - 1][j - 1] + cost);
                }
            }
        }

        return dp[s1.length()][s2.length()];
    }
}

package textsearch;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        args = new String[] {"TestsFiles", "bonjour", ".txt"};
        if (args.length < 2) {
            System.out.println("Usage: java Main <directory> <search_term> [extension]");
            return;
        }

        String directoryPath = args[0];
        String searchTerm = args[1];
        String extensionFilter = args.length > 2 ? args[2] : null;

        SearchManager searchManager = new SearchManager();
        List<SearchResult> results = searchManager.searchInDirectory(directoryPath, searchTerm, extensionFilter);

        if (results.isEmpty()) {
            System.out.println("Aucun résultat trouvé.");
            return;
        }

        // Organiser les résultats par fichier
        Map<String, List<SearchResult>> resultsByFile = new HashMap<>();
        for (SearchResult result : results) {
            resultsByFile.computeIfAbsent(result.getFilePath(), k -> new ArrayList<>()).add(result);
        }

        System.out.printf("Terme \"%s\" trouvé %d fois dans %d fichier(s) :\n\n",
                searchTerm, results.size(), resultsByFile.size());

        for (String filePath : resultsByFile.keySet()) {
            List<SearchResult> fileResults = resultsByFile.get(filePath);
            System.out.printf("- %s : %d occurrence(s)\n", filePath, fileResults.size());
            for (SearchResult res : fileResults) {
                System.out.printf("    → ligne %d : %s\n", res.getLineNumber(), res.getLineContent());
            }
            System.out.println();
        }
    }
}

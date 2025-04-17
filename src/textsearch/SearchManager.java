package textsearch;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class SearchManager {
    private static final int THREAD_COUNT = Runtime.getRuntime().availableProcessors();

    public List<SearchResult> searchInDirectory(String dir, String term, String extFilter) {
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        List<Future<List<SearchResult>>> futures = new ArrayList<>();
        List<Path> files = FileScanner.scanDirectory(dir, extFilter);

        for (Path file : files) {
            FileSearchTask task = new FileSearchTask(file, term);
            futures.add(executor.submit(task));
        }

        List<SearchResult> allResults = new ArrayList<>();
        for (Future<List<SearchResult>> future : futures) {
            try {
                allResults.addAll(future.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        executor.shutdown();
        return allResults;
    }
}

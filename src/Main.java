import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {

    public static void main(String[] args) {
        // Vérification des arguments
        if (args.length < 2) {
            System.out.println("Usage: java Main <dossier> <mot-clé>");
            System.exit(1);
        }

        String directoryPath = args[0];
        String keyword = args[1];

        File folder = new File(directoryPath);
        if (!folder.exists() || !folder.isDirectory()) {
            System.err.println("Le chemin spécifié n'est pas un dossier valide.");
            System.exit(1);
        }

        // Liste des fichiers à traiter
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".txt"));
        if (files == null || files.length == 0) {
            System.out.println("Aucun fichier texte trouvé dans le dossier.");
            return;
        }

        // Création du pool de threads
        int numThreads = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        try {
            // Lancer une tâche par fichier
            for (File file : files) {
                SearchTask task = new SearchTask(file, keyword);
                Future<List<SearchResult>> future = executor.submit(task);

                // Traitement des résultats (on pourrait les accumuler pour affichage global)
                List<SearchResult> results = future.get();
                for (SearchResult result : results) {
                    System.out.printf("[%s] Ligne %d : %s%n",
                            result.getFileName(),
                            result.getLineNumber(),
                            result.getLineContent());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }
    }
}

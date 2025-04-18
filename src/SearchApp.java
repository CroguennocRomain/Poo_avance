import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;

public class SearchApp extends JFrame {
    private JTextField keywordField;
    private JTextField folderField;
    private JTextArea resultArea;

    public SearchApp() {
        setTitle("Recherche de texte multithread");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 500);
        setLocationRelativeTo(null);

        // UI
        keywordField = new JTextField(20);
        folderField = new JTextField(30);
        JButton browseButton = new JButton("Parcourir...");
        JButton searchButton = new JButton("Rechercher");
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);

        // Layout
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));

        JPanel line1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        line1.add(new JLabel("Mot à chercher :"));
        line1.add(keywordField);

        JPanel line2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        line2.add(new JLabel("Dossier :"));
        line2.add(folderField);
        line2.add(browseButton);

        JPanel line3 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        line3.add(searchButton);

        inputPanel.add(line1);
        inputPanel.add(line2);
        inputPanel.add(line3);


        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Bouton Parcourir...
        browseButton.addActionListener((ActionEvent e) -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int res = chooser.showOpenDialog(this);
            if (res == JFileChooser.APPROVE_OPTION) {
                File selectedDir = chooser.getSelectedFile();
                folderField.setText(selectedDir.getAbsolutePath());
            }
        });

        // Bouton Rechercher
        searchButton.addActionListener((ActionEvent e) -> {
            String keyword = keywordField.getText().trim();
            String folderPath = folderField.getText().trim();

            if (keyword.isEmpty() || folderPath.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Veuillez entrer un mot-clé et un dossier.");
                return;
            }

            resultArea.setText("Recherche en cours...\n");

            // Thread de recherche (évite de bloquer l’interface)
            new Thread(() -> {
                SearchManager manager = new SearchManager();
                List<SearchResult> results = manager.searchInDirectory(folderPath, keyword, ".txt");

                SwingUtilities.invokeLater(() -> {
                    if (results.isEmpty()) {
                        resultArea.setText("Aucun résultat trouvé.");
                    } else {
                        StringBuilder sb = new StringBuilder();
                        sb.append("Résultats pour le mot \"").append(keyword).append("\" dans ").append(results.size()).append(" ligne(s) :\n\n");
                        for (SearchResult r : results) {
                            sb.append(String.format("%s (ligne %d) [%s] : %s\n",
                                    r.getFilePath(),
                                    r.getLineNumber(),
                                    r.isApproximate() ? "≈" : "✔",
                                    r.getLineContent()));
                        }
                        resultArea.setText(sb.toString());
                    }
                });
            }).start();
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SearchApp().setVisible(true));
    }
}

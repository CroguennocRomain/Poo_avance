import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;

public class SearchApp extends JFrame {
    private JTextField keywordField;
    private JTextField folderField;
    private JEditorPane resultArea;

    public SearchApp() {
        setTitle("🔍 Recherche de texte multithread");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Champs de saisie
        keywordField = new JTextField(20);
        folderField = new JTextField(30);
        JButton browseButton = new JButton("Parcourir...");
        JButton searchButton = new JButton("Rechercher");

        // Zone de résultats
        resultArea = new JEditorPane();
        resultArea.setEditable(false);
        resultArea.setContentType("text/html");
        JScrollPane scrollPane = new JScrollPane(resultArea);

        // Panel d'entrée avec mise en forme
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel line1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        line1.add(new JLabel("📝 Mot à chercher :"));
        line1.add(keywordField);

        JPanel line2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        line2.add(new JLabel("📁 Dossier :"));
        line2.add(folderField);
        line2.add(browseButton);

        JPanel line3 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        line3.add(searchButton);

        inputPanel.add(line1);
        inputPanel.add(line2);
        inputPanel.add(line3);

        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Action : Bouton Parcourir
        browseButton.addActionListener((ActionEvent e) -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int res = chooser.showOpenDialog(this);
            if (res == JFileChooser.APPROVE_OPTION) {
                File selectedDir = chooser.getSelectedFile();
                folderField.setText(selectedDir.getAbsolutePath());
            }
        });

        // Action : Bouton Rechercher
        searchButton.addActionListener((ActionEvent e) -> {
            String keyword = keywordField.getText().trim();
            String folderPath = folderField.getText().trim();

            if (keyword.isEmpty() || folderPath.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Veuillez entrer un mot-clé et un dossier.");
                return;
            }

            resultArea.setText("<html><body><p style='font-family:sans-serif;'>Recherche en cours...</p></body></html>");

            new Thread(() -> {
                SearchManager manager = new SearchManager();
                List<SearchResult> results = manager.searchInDirectory(folderPath, keyword, ".txt");

                SwingUtilities.invokeLater(() -> {
                    StringBuilder sb = new StringBuilder();
                    sb.append("<html><body style='font-family:sans-serif; padding:10px;'>");
                    sb.append("<h2>🔎 Résultats pour : <span style='color:#007acc'>\"").append(keyword).append("\"</span></h2>");
                    if (results.isEmpty()) {
                        sb.append("<p style='color:gray;'>Aucun résultat trouvé.</p>");
                    } else {
                        sb.append("<ul>");
                        for (SearchResult r : results) {
                            String color = r.isApproximate() ? "#e67e22" : "#2ecc71";
                            String symbol = r.isApproximate() ? "≈ (approché)" : "✔ (exact)";
                            sb.append("<li><b>Fichier :</b> ").append(r.getFilePath())
                                    .append(" | <b>Ligne :</b> ").append(r.getLineNumber())
                                    .append(" | <span style='color:").append(color).append("'>").append(symbol).append("</span><br>")
                                    .append("<pre style='background:#f9f9f9;padding:5px;border:1px solid #ddd;'>")
                                    .append(r.getLineContent()).append("</pre></li><br>");
                        }
                        sb.append("</ul>");
                    }
                    sb.append("</body></html>");
                    resultArea.setText(sb.toString());
                    resultArea.setCaretPosition(0); // remonter en haut
                });
            }).start();
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SearchApp().setVisible(true));
    }
}

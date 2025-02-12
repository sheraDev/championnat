import javax.swing.*;
import java.awt.*;

/**
 * Main class for the Football Championship Management application.
 * Sets up the main window, navigation menu, and panels.
 */
public class Main extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;

    /**
     * Constructs the main application window.
     */
    public Main() {
        // Configuration de la fenêtre principale
        setTitle("Gestion du Championnat de Football");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Création du menu
        JMenuBar menuBar = new JMenuBar();
        JMenu menuGestion = new JMenu("Gestion");

        // Menu Items existants
        JMenuItem menuItemEquipes = new JMenuItem("Gérer les Équipes");
        JMenuItem menuItemMatchs = new JMenuItem("Gérer les Matchs");

        menuGestion.add(menuItemEquipes);
        menuGestion.add(menuItemMatchs);

        menuBar.add(menuGestion);
        setJMenuBar(menuBar);

        // Panel principal avec un CardLayout pour changer les vues
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Ajouter les différentes sections
        mainPanel.add(new AccueilPanel(), "Accueil");
        mainPanel.add(new EquipesPanel(), "Équipes");
        mainPanel.add(new MatchPanel(), "Matchs");

        add(mainPanel);

        // Gestion des événements du menu
        menuItemEquipes.addActionListener(e -> cardLayout.show(mainPanel, "Équipes"));
        menuItemMatchs.addActionListener(e -> cardLayout.show(mainPanel, "Matchs"));
    }

    /**
     * Main entry point of the application.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main app = new Main();
            app.setVisible(true);
        });
    }
}

/**
 * AccueilPanel displays the welcome screen of the application.
 */
class AccueilPanel extends JPanel {
    /**
     * Constructs the AccueilPanel.
     */
    public AccueilPanel() {
        setLayout(new BorderLayout());
        JLabel label = new JLabel("Bienvenue dans l'application de gestion du championnat", JLabel.CENTER);
        add(label, BorderLayout.CENTER);
    }
}

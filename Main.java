import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;

    public Main() {
        // Configuration de la fenêtre principale
        setTitle("Gestion du Championnat de Football");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Création du menu
        JMenuBar menuBar = new JMenuBar();
        JMenu menuGestion = new JMenu("Gestion");
        JMenuItem menuItemEquipes = new JMenuItem("Gérer les Équipes");
        JMenuItem menuItemMatchs = new JMenuItem("Gérer les Matchs");
        JMenuItem menuItemClassement = new JMenuItem("Voir Classement");

        menuGestion.add(menuItemEquipes);
        menuGestion.add(menuItemMatchs);
        menuGestion.add(menuItemClassement);
        menuBar.add(menuGestion);
        setJMenuBar(menuBar);

        // Panel principal avec un CardLayout pour changer les vues
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Ajouter les différentes sections
        mainPanel.add(new AccueilPanel(), "Accueil");
        mainPanel.add(new EquipesPanel(), "Équipes");
        mainPanel.add(new MatchsPanel(), "Matchs");
        mainPanel.add(new ClassementPanel(), "Classement");

        add(mainPanel);

        // Gestion des événements du menu
        menuItemEquipes.addActionListener(e -> cardLayout.show(mainPanel, "Équipes"));
        menuItemMatchs.addActionListener(e -> cardLayout.show(mainPanel, "Matchs"));
        menuItemClassement.addActionListener(e -> cardLayout.show(mainPanel, "Classement"));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main app = new Main();
            app.setVisible(true);
        });
    }
}

// === PANELS POUR CHAQUE SECTION ===

// Panel Accueil
class AccueilPanel extends JPanel {
    public AccueilPanel() {
        setLayout(new BorderLayout());
        JLabel label = new JLabel("Bienvenue dans l'application de gestion du championnat", JLabel.CENTER);
        add(label, BorderLayout.CENTER);
    }
}

// Panel Gestion des Équipes (à compléter avec des JTable, boutons...)
class EquipesPanel extends JPanel {
    public EquipesPanel() {
        setLayout(new BorderLayout());
        JLabel label = new JLabel("Gestion des Équipes", JLabel.CENTER);
        add(label, BorderLayout.CENTER);
    }
}

// Panel Gestion des Matchs (à compléter)
class MatchsPanel extends JPanel {
    public MatchsPanel() {
        setLayout(new BorderLayout());
        JLabel label = new JLabel("Gestion des Matchs", JLabel.CENTER);
        add(label, BorderLayout.CENTER);
    }
}

// Panel Classement (à compléter)
class ClassementPanel extends JPanel {
    public ClassementPanel() {
        setLayout(new BorderLayout());
        JLabel label = new JLabel("Classement des Équipes", JLabel.CENTER);
        add(label, BorderLayout.CENTER);
    }
}

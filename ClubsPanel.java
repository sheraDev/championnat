import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class ClubsPanel extends JPanel {
    // Composants d'affichage : un JTable pour visualiser les clubs
    private JTable clubsTable;
    private ClubsTableModel tableModel;

    // Composants du formulaire d'ajout
    private JTextField nomField;
    private JTextField villeField;
    private JButton addButton;
    private JButton deleteButton;

    // Accès aux données via le DAO
    private ClubDAO clubDAO;

    // Pour mémoriser le club sélectionné (pour la suppression)
    private String selectedClubNom = null;

    public ClubsPanel() {
        // Instanciation du DAO
        clubDAO = new ClubDAO();

        setLayout(new BorderLayout(10, 10));

        // ---------- Partie affichage (tableau des clubs) ----------
        tableModel = new ClubsTableModel();
        clubsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(clubsTable);
        scrollPane.setPreferredSize(new Dimension(600, 200));
        add(scrollPane, BorderLayout.NORTH);

        // Écouteur sur la sélection du tableau pour mémoriser le club sélectionné
        clubsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = clubsTable.getSelectedRow();
                if (selectedRow != -1) {
                    Club selectedClub = tableModel.getClubAt(selectedRow);
                    selectedClubNom = selectedClub.getNom();
                    // Remplissage du formulaire (facultatif) avec les données du club sélectionné
                    nomField.setText(selectedClub.getNom());
                    villeField.setText(selectedClub.getVille());
                }
            }
        });

        // ---------- Partie formulaire d'ajout ----------
        JPanel formPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        formPanel.add(new JLabel("Nom du club :"));
        nomField = new JTextField();
        formPanel.add(nomField);
        formPanel.add(new JLabel("Ville :"));
        villeField = new JTextField();
        formPanel.add(villeField);

        // ---------- Boutons d'actions ----------
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        addButton = new JButton("Ajouter Club");
        deleteButton = new JButton("Supprimer Club");
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);

        // Conteneur pour le formulaire et les boutons
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(formPanel, BorderLayout.CENTER);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(southPanel, BorderLayout.SOUTH);

        // Chargement initial de la liste des clubs
        loadClubs();

        // Action sur le bouton "Ajouter Club"
        addButton.addActionListener(e -> ajouterClub());
        // Action sur le bouton "Supprimer Club"
        deleteButton.addActionListener(e -> supprimerClub());
    }

    /**
     * Recharge la liste des clubs depuis la BDD et met à jour le tableau.
     */
    private void loadClubs() {
        List<Club> clubs = clubDAO.getClubs();
        tableModel.setClubs(clubs);
    }

    /**
     * Récupère les informations du formulaire et ajoute un club dans la BDD.
     */
    private void ajouterClub() {
        String nom = nomField.getText().trim();
        String ville = villeField.getText().trim();

        if (nom.isEmpty() || ville.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs.");
            return;
        }

        int result = clubDAO.ajouterClub(nom, ville);
        if (result > 0) {
            JOptionPane.showMessageDialog(this, "Club ajouté avec succès !");
            loadClubs();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout du club.");
        }
    }

    /**
     * Supprime le club sélectionné de la BDD après confirmation.
     */
    private void supprimerClub() {
        if (selectedClubNom == null) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un club à supprimer.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Voulez-vous vraiment supprimer le club \"" + selectedClubNom + "\" ?",
                "Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int result = clubDAO.supprimerClub(selectedClubNom);
            if (result > 0) {
                JOptionPane.showMessageDialog(this, "Club supprimé avec succès !");
                loadClubs();
                clearForm();
                selectedClubNom = null;
            } else {
                JOptionPane.showMessageDialog(this, "Erreur lors de la suppression du club.");
            }
        }
    }

    /**
     * Réinitialise les champs du formulaire.
     */
    private void clearForm() {
        nomField.setText("");
        villeField.setText("");
    }

    /**
     * Modèle de table personnalisé pour afficher la liste des clubs.
     */
    class ClubsTableModel extends AbstractTableModel {
        private List<Club> clubs = new ArrayList<>();
        private final String[] columnNames = {"ID", "Nom", "Ville"};

        public void setClubs(List<Club> clubs) {
            this.clubs = clubs;
            fireTableDataChanged();
        }

        public Club getClubAt(int rowIndex) {
            return clubs.get(rowIndex);
        }

        @Override
        public int getRowCount() {
            return clubs.size();
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Club club = clubs.get(rowIndex);
            switch (columnIndex) {
                case 0: return club.getId();
                case 1: return club.getNom();
                case 2: return club.getVille();
                default: return "";
            }
        }

        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }
    }

    // Méthode main pour tester le panel indépendamment
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Gestion des Clubs");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(600, 400);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new ClubsPanel());
            frame.setVisible(true);
        });
    }
}

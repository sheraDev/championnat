import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The ClubsPanel class is a JPanel that provides a user interface for managing clubs.
 * It displays a table of clubs and includes a form to add and remove clubs from the database.
 */
public class ClubsPanel extends JPanel {
    // Composants d'affichage 
    private JTable clubsTable;
    private ClubsTableModel tableModel;

    // Composants du formulaire d'ajout
    private JTextField nomField;
    private JTextField villeField;
    private JButton addButton;
    private JButton deleteButton;

    // Accès aux données via le DAO
    private ClubDAO clubDAO;

    private String selectedClubNom = null;

    /**
     * Constructs a new ClubsPanel and initializes the user interface components,
     * including the clubs table and the form for adding clubs.
     */
    public ClubsPanel() {
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

        addButton.addActionListener(e -> ajouterClub());
        deleteButton.addActionListener(e -> supprimerClub());
    }

    /**
     * Reloads the list of clubs from the database and updates the table.
     */
    private void loadClubs() {
        List<Club> clubs = clubDAO.getClubs();
        tableModel.setClubs(clubs);
    }

    /**
     * Retrieves the club information from the form and adds the club to the database.
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
     * Deletes the selected club from the database after user confirmation.
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
     * Clears the input fields of the form.
     */
    private void clearForm() {
        nomField.setText("");
        villeField.setText("");
    }

    /**
     * A custom table model for displaying the list of clubs in a JTable.
     */
    class ClubsTableModel extends AbstractTableModel {
        private List<Club> clubs = new ArrayList<>();
        private final String[] columnNames = {"ID", "Nom", "Ville"};

        /**
         * Sets the list of clubs to be displayed in the table and refreshes the view.
         *
         * @param clubs the list of clubs
         */
        public void setClubs(List<Club> clubs) {
            this.clubs = clubs;
            fireTableDataChanged();
        }

        /**
         * Returns the Club object at the specified row index.
         *
         * @param rowIndex the row index
         * @return the Club at the specified row
         */
        public Club getClubAt(int rowIndex) {
            return clubs.get(rowIndex);
        }

        /**
         * Returns the number of rows in the table model.
         *
         * @return the number of clubs
         */
        @Override
        public int getRowCount() {
            return clubs.size();
        }

        /**
         * Returns the number of columns in the table model.
         *
         * @return the number of columns
         */
        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        /**
         * Returns the value for the cell at the specified row and column.
         *
         * @param rowIndex the row index
         * @param columnIndex the column index
         * @return the value at the specified cell
         */
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

        /**
         * Returns the name of the column at the specified index.
         *
         * @param column the column index
         * @return the name of the column
         */
        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }
    }

    /**
     * The main method to run the ClubsPanel application.
     *
     * @param args command-line arguments (not used)
     */
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

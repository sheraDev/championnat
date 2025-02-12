import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * The EquipesPanel class provides a graphical user interface for managing teams.
 * It displays a table of teams and includes a form to add, modify, or delete teams from the database.
 * This panel interacts with EquipeDAO and ClubDAO for data access.
 */
public class EquipesPanel extends JPanel {

    // Display components (table)
    private JTable equipeTable;
    private EquipeTableModel tableModel;

    // Form components
    private JTextField nomField;
    private JTextField divisionField;
    private JTextField niveauField;  
    private JComboBox<String> clubCombo; 
    private JComboBox<String> sexeCombo;

    // Operation buttons
    private JButton addButton;
    private JButton modifyButton;
    private JButton deleteButton;

    // Data access objects
    private EquipeDAO equipeDAO;
    private ClubDAO clubDAO;

    // Stores the original name of the selected team
    private String selectedEquipeNom = null;

    /**
     * Constructs a new EquipesPanel, initializing the user interface components,
     * loading initial data for teams and clubs, and setting up event listeners.
     */
    public EquipesPanel() {
        // Instantiate the existing DAOs
        equipeDAO = new EquipeDAO();
        clubDAO = new ClubDAO();

        setLayout(new BorderLayout(10, 10));

        // ---------- Display section (teams table) ----------
        tableModel = new EquipeTableModel();
        equipeTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(equipeTable);
        tableScrollPane.setPreferredSize(new Dimension(800, 200));
        add(tableScrollPane, BorderLayout.NORTH);

        // Listener for table selection to populate the form
        equipeTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = equipeTable.getSelectedRow();
                if (selectedRow != -1) {
                    Equipe selectedEquipe = tableModel.getEquipeAt(selectedRow);
                    selectedEquipeNom = selectedEquipe.getNom();
                    nomField.setText(selectedEquipe.getNom());
                    divisionField.setText(selectedEquipe.getDivision());
                    niveauField.setText(selectedEquipe.getNiveau());
                    for (int i = 0; i < clubCombo.getItemCount(); i++) {
                        String item = clubCombo.getItemAt(i);
                        if (item.startsWith(selectedEquipe.getClub() + " -")) {
                            clubCombo.setSelectedIndex(i);
                            break;
                        }
                    }
                    sexeCombo.setSelectedItem(selectedEquipe.getSexe());
                }
            }
        });

        // ---------- Form section for adding/modifying teams ----------
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        
        formPanel.add(new JLabel("Nom de l'équipe :"));
        nomField = new JTextField();
        formPanel.add(nomField);

        formPanel.add(new JLabel("Club :"));
        clubCombo = new JComboBox<>();
        
        // Initial filling of the combo box with the list of clubs
        List<String> clubs = clubDAO.getListeClubs();
        for (String club : clubs) {
            clubCombo.addItem(club);
        }
        formPanel.add(clubCombo);

        formPanel.add(new JLabel("Division :"));
        divisionField = new JTextField();
        formPanel.add(divisionField);

        formPanel.add(new JLabel("Niveau :"));  
        niveauField = new JTextField();
        formPanel.add(niveauField);

        formPanel.add(new JLabel("Sexe :"));
        String[] sexes = {"M", "F"};
        sexeCombo = new JComboBox<>(sexes);
        formPanel.add(sexeCombo);

        // ---------- Action buttons ----------
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        addButton = new JButton("Ajouter Équipe");
        modifyButton = new JButton("Modifier Équipe");
        deleteButton = new JButton("Supprimer Équipe");
        JButton addClubPopupButton = new JButton("Ajouter un Club");

        buttonPanel.add(addButton);
        buttonPanel.add(modifyButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(addClubPopupButton);

        // Container for the form and buttons
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(formPanel, BorderLayout.CENTER);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(southPanel, BorderLayout.SOUTH);

        // Initial loading of teams from the database
        loadEquipes();

        // Button actions
        addButton.addActionListener(e -> ajouterEquipe());
        modifyButton.addActionListener(e -> modifierEquipe());
        deleteButton.addActionListener(e -> supprimerEquipe());
        addClubPopupButton.addActionListener(e -> {
            // Modal window for club management
            JDialog clubDialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Gestion des Clubs", Dialog.ModalityType.APPLICATION_MODAL);
            clubDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            clubDialog.setContentPane(new ClubsPanel());
            clubDialog.pack();
            clubDialog.setLocationRelativeTo(this);
            clubDialog.setVisible(true);
            refreshClubCombo();
        });
    }

    /**
     * Refreshes the list of clubs in the combo box.
     */
    private void refreshClubCombo() {
        List<String> clubs = clubDAO.getListeClubs();
        clubCombo.removeAllItems();
        for (String club : clubs) {
            clubCombo.addItem(club);
        }
    }

    /**
     * Loads the list of teams from the database and updates the table.
     */
    private void loadEquipes() {
        List<Equipe> equipes = equipeDAO.getListeEquipes();
        tableModel.setEquipes(equipes);
    }

    /**
     * Retrieves team information from the form and adds a new team to the database.
     */
    private void ajouterEquipe() {
        String nom = nomField.getText().trim();
        String division = divisionField.getText().trim();
        String niveau = niveauField.getText().trim();  
        String sexe = (String) sexeCombo.getSelectedItem();
        String clubDisplay = (String) clubCombo.getSelectedItem();
        String clubName = clubDisplay.split(" - ")[0].trim();

        if (nom.isEmpty() || division.isEmpty() || clubName.isEmpty() || niveau.isEmpty()) {  // Also checking the level
            JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs.");
            return;
        }

        int clubId = clubDAO.getClubId(clubName);
        if (clubId == -1) {
            JOptionPane.showMessageDialog(this, "Club non trouvé. Vérifiez la sélection.");
            return;
        }

        int result = equipeDAO.ajouterEquipe(nom, clubId, division, sexe, niveau);
        if (result > 0) {
            JOptionPane.showMessageDialog(this, "Équipe ajoutée avec succès !");
            loadEquipes();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout de l'équipe.");
        }
    }

    /**
     * Updates the selected team in the database with the data from the form.
     */
    private void modifierEquipe() {
        if (selectedEquipeNom == null) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une équipe à modifier.");
            return;
        }
        String nouveauNom = nomField.getText().trim();
        String division = divisionField.getText().trim();
        String niveau = niveauField.getText().trim(); 
        String sexe = (String) sexeCombo.getSelectedItem();
        String clubDisplay = (String) clubCombo.getSelectedItem();
        String clubName = clubDisplay.split(" - ")[0].trim();

        if (nouveauNom.isEmpty() || division.isEmpty() || clubName.isEmpty() || niveau.isEmpty()) {  
            JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs.");
            return;
        }

        int clubId = clubDAO.getClubId(clubName);
        if (clubId == -1) {
            JOptionPane.showMessageDialog(this, "Club non trouvé. Vérifiez la sélection.");
            return;
        }

        int result = equipeDAO.modifierEquipe(selectedEquipeNom, nouveauNom, clubId, division, sexe, niveau);
        if (result > 0) {
            JOptionPane.showMessageDialog(this, "Équipe modifiée avec succès !");
            loadEquipes();
            clearForm();
            selectedEquipeNom = null;
        } else {
            JOptionPane.showMessageDialog(this, "Erreur lors de la modification de l'équipe.");
        }
    }

    /**
     * Deletes the selected team from the database after user confirmation.
     */
    private void supprimerEquipe() {
        if (selectedEquipeNom == null) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une équipe à supprimer.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Voulez-vous vraiment supprimer l'équipe \"" + selectedEquipeNom + "\" ?",
                "Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int result = equipeDAO.supprimerEquipe(selectedEquipeNom);
            if (result > 0) {
                JOptionPane.showMessageDialog(this, "Équipe supprimée avec succès !");
                loadEquipes();
                clearForm();
                selectedEquipeNom = null;
            } else {
                JOptionPane.showMessageDialog(this, "Erreur lors de la suppression de l'équipe.");
            }
        }
    }

    /**
     * Clears the input fields in the form.
     */
    private void clearForm() {
        nomField.setText("");
        divisionField.setText("");
        niveauField.setText("");  
        clubCombo.setSelectedIndex(0);
        sexeCombo.setSelectedIndex(0);
    }

    /**
     * Custom table model for displaying the list of teams.
     */
    class EquipeTableModel extends AbstractTableModel {
        private List<Equipe> equipes = new ArrayList<>();
        private final String[] columnNames = {"Nom", "Division", "Sexe", "Club", "Niveau"};

        /**
         * Sets the list of teams to display in the table and refreshes the view.
         *
         * @param equipes the list of teams
         */
        public void setEquipes(List<Equipe> equipes) {
            this.equipes = equipes;
            fireTableDataChanged();
        }

        /**
         * Returns the Equipe object at the specified row index.
         *
         * @param rowIndex the row index in the table
         * @return the team at the specified row
         */
        public Equipe getEquipeAt(int rowIndex) {
            return equipes.get(rowIndex);
        }

        /**
         * Returns the number of rows in the table.
         *
         * @return the number of teams
         */
        @Override
        public int getRowCount() {
            return equipes.size();
        }

        /**
         * Returns the number of columns in the table.
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
         * @return the value of the cell
         */
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Equipe eq = equipes.get(rowIndex);
            switch (columnIndex) {
                case 0: return eq.getNom();
                case 1: return eq.getDivision();
                case 2: return eq.getSexe();
                case 3: return eq.getClub();
                case 4: return eq.getNiveau();
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
     * Main method to test the EquipesPanel independently.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Gestion des Équipes");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new EquipesPanel());
            frame.setVisible(true);
        });
    }
}

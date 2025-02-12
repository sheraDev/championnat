import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * The MatchPanel class provides a user interface for managing matches.
 * It displays a table of matches and offers a form to add, modify, or delete matches.
 * The panel interacts with various DAOs (MatchDAO, EquipeDAO, ChampionnatDAO, StadeDAO, ArbitreDAO)
 * to retrieve and update data.
 */
public class MatchPanel extends JPanel {
    // Table and model for displaying matches
    private JTable matchTable;
    private MatchTableModel tableModel;

    // Form components
    private JComboBox<String> equipeDomicileCombo;
    private JComboBox<String> equipeExterieurCombo;
    private JComboBox<String> championnatCombo; 
    private JComboBox<String> stadeCombo;        
    private JComboBox<String> arbitreCombo;
    private JTextField dateMatchField;
    private JComboBox<String> statutCombo;       
    private JTextField scoreDomicileField;       
    private JTextField scoreExterieurField;      

    // Action buttons
    private JButton addButton;
    private JButton modifyButton;
    private JButton deleteButton;

    // Data Access Objects (DAOs)
    private MatchDAO matchDAO;
    private EquipeDAO equipeDAO;
    private ChampionnatDAO championnatDAO;
    private StadeDAO stadeDAO;
    private ArbitreDAO arbitreDAO;

    // To store the ID of the selected match
    private Integer selectedMatchId = null;

    /**
     * Constructs a new MatchPanel, initializes the UI components, loads initial data,
     * and sets up event listeners.
     */
    public MatchPanel() {
        // Instantiate DAOs
        matchDAO = new MatchDAO();
        equipeDAO = new EquipeDAO();
        championnatDAO = new ChampionnatDAO();
        stadeDAO = new StadeDAO();
        arbitreDAO = new ArbitreDAO();
        setLayout(new BorderLayout(10, 10));

        // ---------- Display Section (Matches Table) ----------
        tableModel = new MatchTableModel();
        matchTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(matchTable);
        tableScrollPane.setPreferredSize(new Dimension(900, 200));
        add(tableScrollPane, BorderLayout.NORTH);

        // Listener for table selection to populate the form fields
        matchTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = matchTable.getSelectedRow();
                if (selectedRow != -1) {
                    Match selectedMatch = tableModel.getMatchAt(selectedRow);
                    selectedMatchId = selectedMatch.getId();
                    // Populate form fields with the selected match's data
                    equipeDomicileCombo.setSelectedItem(selectedMatch.getEquipeDomicile());
                    equipeExterieurCombo.setSelectedItem(selectedMatch.getEquipeExterieur());
                    championnatCombo.setSelectedItem(selectedMatch.getChampionnat());
                    stadeCombo.setSelectedItem(selectedMatch.getStade());
                    arbitreCombo.setSelectedItem(selectedMatch.getArbitre());
                    dateMatchField.setText(selectedMatch.getDateMatch());
                    statutCombo.setSelectedItem(selectedMatch.getStatut());
                    scoreDomicileField.setText(String.valueOf(selectedMatch.getScoreDomicile()));
                    scoreExterieurField.setText(String.valueOf(selectedMatch.getScoreExterieur()));
                }
            }
        });

        // ---------- Form Section for Adding/Modifying Matches ----------
        // Using a grid layout with 9 rows and 2 columns (the "Statut" row is included)
        JPanel formPanel = new JPanel(new GridLayout(9, 2, 5, 5));
        
        formPanel.add(new JLabel("Équipe Domicile:"));
        equipeDomicileCombo = new JComboBox<>();
        formPanel.add(equipeDomicileCombo);

        formPanel.add(new JLabel("Équipe Extérieur:"));
        equipeExterieurCombo = new JComboBox<>();
        formPanel.add(equipeExterieurCombo);

        formPanel.add(new JLabel("Championnat:"));
        championnatCombo = new JComboBox<>();
        // Initial population of the championship combo box
        List<String> championnats = championnatDAO.getListeChampionnat();
        for (String champ : championnats) {
            championnatCombo.addItem(champ);
        }
        formPanel.add(championnatCombo);

        formPanel.add(new JLabel("Stade:"));
        stadeCombo = new JComboBox<>();
        // Initial population of the stadium combo box (format "Name - City")
        List<String> stades = stadeDAO.getListeStades();
        for (String stade : stades) {
            stadeCombo.addItem(stade);
        }
        formPanel.add(stadeCombo);

        formPanel.add(new JLabel("Arbitre:"));
        arbitreCombo = new JComboBox<>();
        // Initial population of the referee combo box
        List<String> arbitres = arbitreDAO.getListeArbitres();
        for (String arbitre : arbitres) {
            arbitreCombo.addItem(arbitre);
        }
        formPanel.add(arbitreCombo);

        formPanel.add(new JLabel("Date du Match (YYYY-MM-DD HH:MM:SS):"));
        dateMatchField = new JTextField();
        formPanel.add(dateMatchField);
        
        formPanel.add(new JLabel("Statut:"));
        statutCombo = new JComboBox<>();
        // Possible values for the match status
        statutCombo.addItem("prevu");
        statutCombo.addItem("annule");
        statutCombo.addItem("realise");
        formPanel.add(statutCombo);
        
        formPanel.add(new JLabel("Score Domicile:"));
        scoreDomicileField = new JTextField();
        formPanel.add(scoreDomicileField);
        
        formPanel.add(new JLabel("Score Extérieur:"));
        scoreExterieurField = new JTextField();
        formPanel.add(scoreExterieurField);

        // ---------- Action Buttons ----------
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        addButton = new JButton("Ajouter Match");
        modifyButton = new JButton("Modifier Match");
        deleteButton = new JButton("Supprimer Match");
        buttonPanel.add(addButton);
        buttonPanel.add(modifyButton);
        buttonPanel.add(deleteButton);

        // Button to add a referee
        JButton addArbitreButton = new JButton("Ajouter Arbitre");
        buttonPanel.add(addArbitreButton);
        addArbitreButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nomArbitre = JOptionPane.showInputDialog(
                        MatchPanel.this,
                        "Entrez le nom de l'arbitre :",
                        "Ajouter Arbitre",
                        JOptionPane.QUESTION_MESSAGE);
                if (nomArbitre != null && !nomArbitre.trim().isEmpty()) {
                    int result = arbitreDAO.ajouterArbitre(nomArbitre.trim());
                    if (result > 0) {
                        JOptionPane.showMessageDialog(MatchPanel.this, "Arbitre ajouté avec succès !");
                        // Update the referee combo box
                        arbitreCombo.removeAllItems();
                        List<String> newArbitres = arbitreDAO.getListeArbitres();
                        for (String arbitre : newArbitres) {
                            arbitreCombo.addItem(arbitre);
                        }
                    } else {
                        JOptionPane.showMessageDialog(MatchPanel.this, "Erreur lors de l'ajout de l'arbitre.");
                    }
                }
            }
        });

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(formPanel, BorderLayout.CENTER);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(southPanel, BorderLayout.SOUTH);

        // Load initial data
        loadMatches();
        refreshEquipeCombos();

        // Button actions
        addButton.addActionListener(e -> ajouterMatch());
        modifyButton.addActionListener(e -> modifierMatch());
        deleteButton.addActionListener(e -> supprimerMatch());
    }

    /**
     * Refreshes the team combo boxes with the list of teams from EquipeDAO.
     */
    private void refreshEquipeCombos() {
        List<Equipe> equipes = equipeDAO.getListeEquipes();
        equipeDomicileCombo.removeAllItems();
        equipeExterieurCombo.removeAllItems();
        for (Equipe eq : equipes) {
            equipeDomicileCombo.addItem(eq.getNom());
            equipeExterieurCombo.addItem(eq.getNom());
        }
    }

    /**
     * Loads the list of matches from the database and updates the table model.
     */
    private void loadMatches() {
        List<Match> matches = matchDAO.getListeMatchsObjects();
        tableModel.setMatches(matches);
    }

    /**
     * Retrieves the information from the form and adds a new match to the database.
     */
    private void ajouterMatch() {
        String eqDomicile = (String) equipeDomicileCombo.getSelectedItem();
        String eqExterieur = (String) equipeExterieurCombo.getSelectedItem();
        int idDomicile = equipeDAO.getEquipeId(eqDomicile);
        int idExterieur = equipeDAO.getEquipeId(eqExterieur);

        // Extract the championship name from the label (e.g., "Ligue 1 - Saison : 2024-2025")
        String selectedChampionnat = (String) championnatCombo.getSelectedItem();
        String championnatName = selectedChampionnat.split(" - ")[0].trim();
        int championnatId = championnatDAO.getChampionnatId(championnatName);

        // Retrieve the stadium ID from the combo (format "Name - City")
        String selectedStade = (String) stadeCombo.getSelectedItem();
        int stadeId = stadeDAO.getStadeIdByDisplay(selectedStade);

        String selectedArbitre = (String) arbitreCombo.getSelectedItem();
        int arbitreId = arbitreDAO.getArbitreIdByDisplay(selectedArbitre);

        String dateMatch = dateMatchField.getText().trim();
        if (dateMatch.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez entrer la date du match.");
            return;
        }
        
        // Retrieve and validate scores
        int scoreDomicile, scoreExterieur;
        try {
            scoreDomicile = Integer.parseInt(scoreDomicileField.getText().trim());
            scoreExterieur = Integer.parseInt(scoreExterieurField.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Veuillez entrer des valeurs numériques valides pour les scores.");
            return;
        }
        
        // Retrieve the status
        String statut = (String) statutCombo.getSelectedItem();

        // Call the DAO to add the match with the provided status and scores
        int result = matchDAO.ajouterMatch(idDomicile, idExterieur, championnatId, stadeId, arbitreId, dateMatch, scoreDomicile, scoreExterieur, statut);
        if (result > 0) {
            JOptionPane.showMessageDialog(this, "Match ajouté avec succès !");
            loadMatches();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout du match.");
        }
    }

    /**
     * Modifies the selected match using the information from the form.
     */
    private void modifierMatch() {
        if (selectedMatchId == null) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un match à modifier.");
            return;
        }
        String eqDomicile = (String) equipeDomicileCombo.getSelectedItem();
        String eqExterieur = (String) equipeExterieurCombo.getSelectedItem();
        int idDomicile = equipeDAO.getEquipeId(eqDomicile);
        int idExterieur = equipeDAO.getEquipeId(eqExterieur);

        String selectedChampionnat = (String) championnatCombo.getSelectedItem();
        String championnatName = selectedChampionnat.split(" - ")[0].trim();
        int championnatId = championnatDAO.getChampionnatId(championnatName);

        String selectedStade = (String) stadeCombo.getSelectedItem();
        int stadeId = stadeDAO.getStadeIdByDisplay(selectedStade);

        String selectedArbitre = (String) arbitreCombo.getSelectedItem();
        int arbitreId = arbitreDAO.getArbitreIdByDisplay(selectedArbitre);

        String dateMatch = dateMatchField.getText().trim();
        if (dateMatch.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez entrer la date du match.");
            return;
        }
        
        int scoreDomicile, scoreExterieur;
        try {
            scoreDomicile = Integer.parseInt(scoreDomicileField.getText().trim());
            scoreExterieur = Integer.parseInt(scoreExterieurField.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Veuillez entrer des valeurs numériques valides pour les scores.");
            return;
        }
        
        String statut = (String) statutCombo.getSelectedItem();

        int result = matchDAO.modifierMatch(selectedMatchId, idDomicile, idExterieur, championnatId, stadeId, arbitreId, dateMatch, scoreDomicile, scoreExterieur, statut);
        if (result > 0) {
            JOptionPane.showMessageDialog(this, "Match modifié avec succès !");
            loadMatches();
            clearForm();
            selectedMatchId = null;
        } else {
            JOptionPane.showMessageDialog(this, "Erreur lors de la modification du match.");
        }
    }

    /**
     * Deletes the selected match from the database after user confirmation.
     */
    private void supprimerMatch() {
        if (selectedMatchId == null) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un match à supprimer.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Voulez-vous vraiment supprimer le match sélectionné ?",
                "Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int result = matchDAO.supprimerMatch(selectedMatchId);
            if (result > 0) {
                JOptionPane.showMessageDialog(this, "Match supprimé avec succès !");
                loadMatches();
                clearForm();
                selectedMatchId = null;
            } else {
                JOptionPane.showMessageDialog(this, "Erreur lors de la suppression du match.");
            }
        }
    }

    /**
     * Clears the form fields.
     */
    private void clearForm() {
        equipeDomicileCombo.setSelectedIndex(0);
        equipeExterieurCombo.setSelectedIndex(0);
        championnatCombo.setSelectedIndex(0);
        stadeCombo.setSelectedIndex(0);
        arbitreCombo.setSelectedIndex(0);
        dateMatchField.setText("");
        statutCombo.setSelectedIndex(0);
        scoreDomicileField.setText("");
        scoreExterieurField.setText("");
    }

    // --- Custom Table Model for Matches ---
    class MatchTableModel extends AbstractTableModel {
        private List<Match> matches = new ArrayList<>();
        private final String[] columnNames = {"ID", "Domicile", "Extérieur", "Championnat", "Stade", "Arbitre", "Date", "Statut", "Score D", "Score E"};

        /**
         * Sets the list of matches to display in the table and refreshes the view.
         *
         * @param matches the list of matches
         */
        public void setMatches(List<Match> matches) {
            this.matches = matches;
            fireTableDataChanged();
        }

        /**
         * Returns the Match object at the specified row index.
         *
         * @param rowIndex the row index
         * @return the match at the specified row
         */
        public Match getMatchAt(int rowIndex) {
            return matches.get(rowIndex);
        }

        /**
         * Returns the number of rows in the table.
         *
         * @return the number of matches
         */
        @Override
        public int getRowCount() {
            return matches.size();
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
            Match m = matches.get(rowIndex);
            switch (columnIndex) {
                case 0: return m.getId();
                case 1: return m.getEquipeDomicile();
                case 2: return m.getEquipeExterieur();
                case 3: return m.getChampionnat();
                case 4: return m.getStade();
                case 5: return m.getArbitre();
                case 6: return m.getDateMatch();
                case 7: return m.getStatut();
                case 8: return m.getScoreDomicile();
                case 9: return m.getScoreExterieur();
                default: return "";
            }
        }

        /**
         * Returns the name of the column at the specified index.
         *
         * @param column the column index
         * @return the column name
         */
        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }
    }

    /**
     * Inner class representing a match.
     */
    public static class Match {
        private int id;
        private String equipeDomicile;
        private String equipeExterieur;
        private String championnat; 
        private String stade;
        private String arbitre;
        private String dateMatch;
        private String statut;     
        private int scoreDomicile;
        private int scoreExterieur;

        /**
         * Constructs a new Match with the specified details.
         *
         * @param id the match ID
         * @param equipeDomicile the home team name
         * @param equipeExterieur the away team name
         * @param championnat the championship name
         * @param stade the stadium name
         * @param arbitre the referee name
         * @param dateMatch the match date
         * @param statut the status of the match
         * @param scoreDomicile the home team score
         * @param scoreExterieur the away team score
         */
        public Match(int id, String equipeDomicile, String equipeExterieur, String championnat,
                     String stade, String arbitre, String dateMatch, String statut, int scoreDomicile, int scoreExterieur) {
            this.id = id;
            this.equipeDomicile = equipeDomicile;
            this.equipeExterieur = equipeExterieur;
            this.championnat = championnat;
            this.stade = stade;
            this.arbitre = arbitre;
            this.dateMatch = dateMatch;
            this.statut = statut;
            this.scoreDomicile = scoreDomicile;
            this.scoreExterieur = scoreExterieur;
        }

        /**
         * Returns the match ID.
         *
         * @return the ID of the match
         */
        public int getId() {
            return id;
        }

        /**
         * Returns the home team name.
         *
         * @return the home team
         */
        public String getEquipeDomicile() {
            return equipeDomicile;
        }

        /**
         * Returns the away team name.
         *
         * @return the away team
         */
        public String getEquipeExterieur() {
            return equipeExterieur;
        }

        /**
         * Returns the championship name.
         *
         * @return the championship
         */
        public String getChampionnat() {
            return championnat;
        }

        /**
         * Returns the stadium name.
         *
         * @return the stadium
         */
        public String getStade() {
            return stade;
        }

        /**
         * Returns the referee name.
         *
         * @return the referee
         */
        public String getArbitre() {
            return arbitre;
        }

        /**
         * Returns the match date.
         *
         * @return the match date
         */
        public String getDateMatch() {
            return dateMatch;
        }

        /**
         * Returns the match status.
         *
         * @return the status of the match
         */
        public String getStatut() {
            return statut;
        }

        /**
         * Returns the home team score.
         *
         * @return the score of the home team
         */
        public int getScoreDomicile() {
            return scoreDomicile;
        }

        /**
         * Returns the away team score.
         *
         * @return the score of the away team
         */
        public int getScoreExterieur() {
            return scoreExterieur;
        }
    }

    /**
     * Main method to run the MatchPanel independently.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Gestion des Matchs");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(900, 600);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new MatchPanel());
            frame.setVisible(true);
        });
    }
}

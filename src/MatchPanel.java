
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class MatchPanel extends JPanel {
    // Tableau et modèle pour afficher les matchs
    private JTable matchTable;
    private MatchTableModel tableModel;

    // Composants du formulaire
    private JComboBox<String> equipeDomicileCombo;
    private JComboBox<String> equipeExterieurCombo;
    private JComboBox<String> championnatCombo; 
    private JComboBox<String> stadeCombo;        
    private JComboBox<String> arbitreCombo;
    private JTextField dateMatchField;
    private JComboBox<String> statutCombo;       
    private JTextField scoreDomicileField;       
    private JTextField scoreExterieurField;      

    // Boutons d'actions
    private JButton addButton;
    private JButton modifyButton;
    private JButton deleteButton;

    // DAO
    private MatchDAO matchDAO;
    private EquipeDAO equipeDAO;
    private ChampionnatDAO championnatDAO;
    private StadeDAO stadeDAO;
    private ArbitreDAO arbitreDAO;

    // Pour mémoriser l'ID du match sélectionné
    private Integer selectedMatchId = null;

    public MatchPanel() {
        // Instanciation des DAO
        matchDAO = new MatchDAO();
        equipeDAO = new EquipeDAO();
        championnatDAO = new ChampionnatDAO();
        stadeDAO = new StadeDAO();
        arbitreDAO = new ArbitreDAO();
        setLayout(new BorderLayout(10, 10));

        // ---------- Partie affichage (tableau des matchs) ----------
        tableModel = new MatchTableModel();
        matchTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(matchTable);
        tableScrollPane.setPreferredSize(new Dimension(900, 200));
        add(tableScrollPane, BorderLayout.NORTH);

        // Écoute sur la sélection dans le tableau pour remplir le formulaire
        matchTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = matchTable.getSelectedRow();
                if (selectedRow != -1) {
                    Match selectedMatch = tableModel.getMatchAt(selectedRow);
                    selectedMatchId = selectedMatch.getId();
                    // Remplissage des champs du formulaire
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

        // ---------- Partie formulaire d'ajout/modification ----------
        // Utilisation d'une grille à 9 lignes (la ligne "Statut" est ajoutée)
        JPanel formPanel = new JPanel(new GridLayout(9, 2, 5, 5));
        
        formPanel.add(new JLabel("Équipe Domicile:"));
        equipeDomicileCombo = new JComboBox<>();
        formPanel.add(equipeDomicileCombo);

        formPanel.add(new JLabel("Équipe Extérieur:"));
        equipeExterieurCombo = new JComboBox<>();
        formPanel.add(equipeExterieurCombo);

        formPanel.add(new JLabel("Championnat:"));
        championnatCombo = new JComboBox<>();
        // Remplissage initial du combo pour championnats
        List<String> championnats = championnatDAO.getListeChampionnat();
        for (String champ : championnats) {
            championnatCombo.addItem(champ);
        }
        formPanel.add(championnatCombo);

        formPanel.add(new JLabel("Stade:"));
        stadeCombo = new JComboBox<>();
        // Remplissage initial du combo pour stades (format "Nom - Ville")
        List<String> stades = stadeDAO.getListeStades();
        for (String stade : stades) {
            stadeCombo.addItem(stade);
        }
        formPanel.add(stadeCombo);

        formPanel.add(new JLabel("Arbitre:"));
        arbitreCombo = new JComboBox<>();
        // Remplissage initial du combo pour arbitres
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
        // Valeurs possibles pour le statut
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

        // ---------- Boutons d'actions ----------
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        addButton = new JButton("Ajouter Match");
        modifyButton = new JButton("Modifier Match");
        deleteButton = new JButton("Supprimer Match");
        buttonPanel.add(addButton);
        buttonPanel.add(modifyButton);
        buttonPanel.add(deleteButton);

        // Bouton pour ajouter un arbitre
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
                        // Mise à jour du combo "arbitreCombo"
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

        // Chargement initial des données
        loadMatches();
        refreshEquipeCombos();

        // Actions sur les boutons
        addButton.addActionListener(e -> ajouterMatch());
        modifyButton.addActionListener(e -> modifierMatch());
        deleteButton.addActionListener(e -> supprimerMatch());
    }

    /**
     * Recharge les listes déroulantes des équipes depuis EquipeDAO.
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
     * Charge la liste des matchs depuis la base et met à jour le tableau.
     */
    private void loadMatches() {
        List<Match> matches = matchDAO.getListeMatchsObjects();
        tableModel.setMatches(matches);
    }

    /**
     * Récupère les informations du formulaire et ajoute un nouveau match dans la base.
     */
    private void ajouterMatch() {
        String eqDomicile = (String) equipeDomicileCombo.getSelectedItem();
        String eqExterieur = (String) equipeExterieurCombo.getSelectedItem();
        int idDomicile = equipeDAO.getEquipeId(eqDomicile);
        int idExterieur = equipeDAO.getEquipeId(eqExterieur);

        // Extraction du nom du championnat à partir du libellé (ex : "Ligue 1 - Saison : 2024-2025")
        String selectedChampionnat = (String) championnatCombo.getSelectedItem();
        String championnatName = selectedChampionnat.split(" - ")[0].trim();
        int championnatId = championnatDAO.getChampionnatId(championnatName);

        // Récupérer l'ID du stade à partir du combo (format "Nom - Ville")
        String selectedStade = (String) stadeCombo.getSelectedItem();
        int stadeId = stadeDAO.getStadeIdByDisplay(selectedStade);

        String selectedArbitre = (String) arbitreCombo.getSelectedItem();
        int arbitreId = arbitreDAO.getArbitreIdByDisplay(selectedArbitre);

        String dateMatch = dateMatchField.getText().trim();
        if (dateMatch.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez entrer la date du match.");
            return;
        }
        
        // Récupération et vérification des scores
        int scoreDomicile, scoreExterieur;
        try {
            scoreDomicile = Integer.parseInt(scoreDomicileField.getText().trim());
            scoreExterieur = Integer.parseInt(scoreExterieurField.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Veuillez entrer des valeurs numériques valides pour les scores.");
            return;
        }
        
        // Récupération du statut
        String statut = (String) statutCombo.getSelectedItem();

        // Appel au DAO mis à jour pour ajouter le match avec le statut et les scores
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
     * Modifie le match sélectionné avec les informations du formulaire.
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
     * Supprime le match sélectionné de la base.
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
     * Réinitialise les champs du formulaire.
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

    // --- Modèle de table personnalisé pour les matchs ---
    class MatchTableModel extends AbstractTableModel {
        private List<Match> matches = new ArrayList<>();
        private final String[] columnNames = {"ID", "Domicile", "Extérieur", "Championnat", "Stade", "Arbitre", "Date", "Statut", "Score D", "Score E"};

        public void setMatches(List<Match> matches) {
            this.matches = matches;
            fireTableDataChanged();
        }

        public Match getMatchAt(int rowIndex) {
            return matches.get(rowIndex);
        }

        @Override
        public int getRowCount() {
            return matches.size();
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

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

        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }
    }

    /**
     * Classe interne pour représenter un match.
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

        public int getId() {
            return id;
        }

        public String getEquipeDomicile() {
            return equipeDomicile;
        }

        public String getEquipeExterieur() {
            return equipeExterieur;
        }

        public String getChampionnat() {
            return championnat;
        }

        public String getStade() {
            return stade;
        }

        public String getArbitre() {
            return arbitre;
        }

        public String getDateMatch() {
            return dateMatch;
        }

        public String getStatut() {
            return statut;
        }

        public int getScoreDomicile() {
            return scoreDomicile;
        }

        public int getScoreExterieur() {
            return scoreExterieur;
        }
    }

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

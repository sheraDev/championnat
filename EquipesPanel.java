import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class EquipesPanel extends JPanel {
    // Composants d'affichage (tableau)
    private JTable equipeTable;
    private EquipeTableModel tableModel;

    // Composants du formulaire
    private JTextField nomField;
    private JTextField divisionField;
    private JTextField niveauField;  // Nouveau champ pour le niveau
    private JComboBox<String> clubCombo; // Liste déroulante pour les clubs
    private JComboBox<String> sexeCombo;

    // Boutons pour les opérations
    private JButton addButton;
    private JButton modifyButton;
    private JButton deleteButton;

    // Accès aux données via les DAO
    private EquipeDAO equipeDAO;
    private ClubDAO clubDAO;

    // Pour garder en mémoire le nom original de l'équipe sélectionnée
    private String selectedEquipeNom = null;

    public EquipesPanel() {
        // Instanciation des DAO existants
        equipeDAO = new EquipeDAO();
        clubDAO = new ClubDAO();

        setLayout(new BorderLayout(10, 10));

        // ---------- Partie affichage (tableau des équipes) ----------
        tableModel = new EquipeTableModel();
        equipeTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(equipeTable);
        tableScrollPane.setPreferredSize(new Dimension(800, 200));
        add(tableScrollPane, BorderLayout.NORTH);

        // Écouteur sur la sélection du tableau pour remplir le formulaire
        equipeTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = equipeTable.getSelectedRow();
                if (selectedRow != -1) {
                    Equipe selectedEquipe = tableModel.getEquipeAt(selectedRow);
                    // Stocke le nom original pour la modification
                    selectedEquipeNom = selectedEquipe.getNom();
                    nomField.setText(selectedEquipe.getNom());
                    divisionField.setText(selectedEquipe.getDivision());
                    niveauField.setText(selectedEquipe.getNiveau()); // Remplissage du niveau
                    // Sélectionne dans le combo le club correspondant
                    for (int i = 0; i < clubCombo.getItemCount(); i++) {
                        String item = clubCombo.getItemAt(i);
                        // On considère que l'item est au format "nom - ville"
                        if (item.startsWith(selectedEquipe.getClub() + " -")) {
                            clubCombo.setSelectedIndex(i);
                            break;
                        }
                    }
                    sexeCombo.setSelectedItem(selectedEquipe.getSexe());
                }
            }
        });

        // ---------- Partie formulaire d'ajout/modification ----------
        // On passe à une grille à 5 lignes : Nom, Club, Division, Niveau, Sexe
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        
        formPanel.add(new JLabel("Nom de l'équipe :"));
        nomField = new JTextField();
        formPanel.add(nomField);

        formPanel.add(new JLabel("Club :"));
        clubCombo = new JComboBox<>();
        // Remplissage initial du comboBox avec la liste des clubs
        List<String> clubs = clubDAO.getListeClubs();
        for (String club : clubs) {
            clubCombo.addItem(club);
        }
        formPanel.add(clubCombo);

        formPanel.add(new JLabel("Division :"));
        divisionField = new JTextField();
        formPanel.add(divisionField);

        formPanel.add(new JLabel("Niveau :"));  // Nouvelle ligne pour le niveau
        niveauField = new JTextField();
        formPanel.add(niveauField);

        formPanel.add(new JLabel("Sexe :"));
        String[] sexes = {"M", "F"};
        sexeCombo = new JComboBox<>(sexes);
        formPanel.add(sexeCombo);

        // ---------- Boutons d'actions ----------
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        addButton = new JButton("Ajouter Équipe");
        modifyButton = new JButton("Modifier Équipe");
        deleteButton = new JButton("Supprimer Équipe");
        // Nouveau bouton pour ouvrir la popup de gestion des clubs
        JButton addClubPopupButton = new JButton("Ajouter un Club");

        buttonPanel.add(addButton);
        buttonPanel.add(modifyButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(addClubPopupButton);

        // Conteneur pour le formulaire et les boutons
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(formPanel, BorderLayout.CENTER);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(southPanel, BorderLayout.SOUTH);

        // Chargement initial des équipes depuis la base
        loadEquipes();

        // Actions sur les boutons
        addButton.addActionListener(e -> ajouterEquipe());
        modifyButton.addActionListener(e -> modifierEquipe());
        deleteButton.addActionListener(e -> supprimerEquipe());
        addClubPopupButton.addActionListener(e -> {
            // Créer une fenêtre modale pour la gestion des clubs
            JDialog clubDialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Gestion des Clubs", Dialog.ModalityType.APPLICATION_MODAL);
            clubDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            clubDialog.setContentPane(new ClubsPanel());
            clubDialog.pack();
            clubDialog.setLocationRelativeTo(this);
            clubDialog.setVisible(true);
            // Après fermeture de la popup, on rafraîchit la liste des clubs dans le combo
            refreshClubCombo();
        });
    }

    /**
     * Méthode pour rafraîchir la liste des clubs dans le comboBox.
     */
    private void refreshClubCombo() {
        List<String> clubs = clubDAO.getListeClubs();
        clubCombo.removeAllItems();
        for (String club : clubs) {
            clubCombo.addItem(club);
        }
    }

    /**
     * Charge la liste des équipes depuis la base et met à jour le tableau.
     */
    private void loadEquipes() {
        List<Equipe> equipes = equipeDAO.getListeEquipes();
        tableModel.setEquipes(equipes);
    }

    /**
     * Récupère les informations du formulaire et ajoute une nouvelle équipe dans la base.
     */
    private void ajouterEquipe() {
        String nom = nomField.getText().trim();
        String division = divisionField.getText().trim();
        String niveau = niveauField.getText().trim();  // Récupération du niveau
        String sexe = (String) sexeCombo.getSelectedItem();
        String clubDisplay = (String) clubCombo.getSelectedItem();
        String clubName = clubDisplay.split(" - ")[0].trim();

        if (nom.isEmpty() || division.isEmpty() || clubName.isEmpty() || niveau.isEmpty()) {  // Vérification du niveau aussi
            JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs.");
            return;
        }

        int clubId = clubDAO.getClubId(clubName);
        if (clubId == -1) {
            JOptionPane.showMessageDialog(this, "Club non trouvé. Vérifiez la sélection.");
            return;
        }

        // Appel au DAO pour ajouter l'équipe (supposé prendre en compte le niveau)
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
     * Modifie l'équipe sélectionnée en utilisant les données du formulaire.
     */
    private void modifierEquipe() {
        if (selectedEquipeNom == null) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une équipe à modifier.");
            return;
        }
        String nouveauNom = nomField.getText().trim();
        String division = divisionField.getText().trim();
        String niveau = niveauField.getText().trim();  // Récupération du niveau
        String sexe = (String) sexeCombo.getSelectedItem();
        String clubDisplay = (String) clubCombo.getSelectedItem();
        String clubName = clubDisplay.split(" - ")[0].trim();

        if (nouveauNom.isEmpty() || division.isEmpty() || clubName.isEmpty() || niveau.isEmpty()) {  // Vérification du niveau
            JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs.");
            return;
        }

        int clubId = clubDAO.getClubId(clubName);
        if (clubId == -1) {
            JOptionPane.showMessageDialog(this, "Club non trouvé. Vérifiez la sélection.");
            return;
        }

        // Appel au DAO pour modifier l'équipe (supposé prendre en compte le niveau)
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
     * Supprime l'équipe sélectionnée de la base.
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
     * Réinitialise les champs du formulaire.
     */
    private void clearForm() {
        nomField.setText("");
        divisionField.setText("");
        niveauField.setText("");  // Réinitialisation du champ niveau
        clubCombo.setSelectedIndex(0);
        sexeCombo.setSelectedIndex(0);
    }

    /**
     * Modèle de table personnalisé pour afficher la liste des équipes.
     */
    class EquipeTableModel extends AbstractTableModel {
        private List<Equipe> equipes = new ArrayList<>();
        // Ajout de la colonne "Niveau"
        private final String[] columnNames = {"Nom", "Division", "Sexe", "Club", "Niveau"};

        public void setEquipes(List<Equipe> equipes) {
            this.equipes = equipes;
            fireTableDataChanged();
        }

        public Equipe getEquipeAt(int rowIndex) {
            return equipes.get(rowIndex);
        }

        @Override
        public int getRowCount() {
            return equipes.size();
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Equipe eq = equipes.get(rowIndex);
            switch (columnIndex) {
                case 0: return eq.getNom();
                case 1: return eq.getDivision();
                case 2: return eq.getSexe();
                case 3: return eq.getClub();
                case 4: return eq.getNiveau(); // Affichage du niveau
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
            JFrame frame = new JFrame("Gestion des Équipes");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new EquipesPanel());
            frame.setVisible(true);
        });
    }
}

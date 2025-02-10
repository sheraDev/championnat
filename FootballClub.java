
 */
package footballclub;

import javax.swing.*;
import java.awt.*;

/**
 *
 * @author DELL
 */
public class FootballClub {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(FootballClub::createAndShowGUI);
    }
    private static void createAndShowGUI() {
        // Main Frame
        JFrame frame = new JFrame("Gestion des Équipes");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 500);
        frame.setLayout(new BorderLayout());

        // Tabbed Panel
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.add("Equipe", createEquipePanel());
        tabbedPane.add("Home", new JPanel());  // Placeholder
        tabbedPane.add("Match", createMatchPanel()); // NEW

        frame.add(tabbedPane, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private static JPanel createEquipePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Left Section - List of Teams
        DefaultListModel<String> teamListModel = new DefaultListModel<>();
        JList<String> teamList = new JList<>(teamListModel);
        JScrollPane teamScrollPane = new JScrollPane(teamList);

        teamListModel.addElement("FC Mulhouse");
        teamListModel.addElement("Strasbourg Meinau FC");
        teamListModel.addElement("FC Colmar");
        teamListModel.addElement("FC Fréland");

        // Middle Section - List of Cities
        DefaultListModel<String> cityListModel = new DefaultListModel<>();
        JList<String> cityList = new JList<>(cityListModel);
        JScrollPane cityScrollPane = new JScrollPane(cityList);

        cityListModel.addElement("Mulhouse");
        cityListModel.addElement("Colmar");
        cityListModel.addElement("Haguenau");
        cityListModel.addElement("Strasbourg");

        JButton addCityButton = new JButton("Ajouter une ville");
        addCityButton.setPreferredSize(new Dimension(100, 40));
        // Right Section - List of Clubs
        DefaultListModel<String> clubListModel = new DefaultListModel<>();
        JList<String> clubList = new JList<>(clubListModel);
        JScrollPane clubScrollPane = new JScrollPane(clubList);

        clubListModel.addElement("FC Mulhouse");
        clubListModel.addElement("Strasbourg Meinau FC");
        clubListModel.addElement("FC Colmar");
        clubListModel.addElement("FC Fréland");

        JButton addClubButton = new JButton("Ajouter un club");
        addCityButton.setPreferredSize(new Dimension(100, 40));

        // Grid Layout for Lists
        JPanel listPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        listPanel.add(teamScrollPane);
        listPanel.add(cityScrollPane);
        listPanel.add(clubScrollPane);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        buttonPanel.add(addCityButton);
        buttonPanel.add(addClubButton);

        // Form Panel for Editing Team Details
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));

        JLabel nameLabel = new JLabel("Nom:");
        JTextField nameField = new JTextField("Mulhouse FC U17 D1");

        JLabel levelLabel = new JLabel("Niveau:");
        String[] levels = {"U17", "U19", "Seniors"};
        JComboBox<String> levelComboBox = new JComboBox<>(levels);

        JLabel districtLabel = new JLabel("District:");
        String[] districts = {"D1", "D2", "D3"};
        JComboBox<String> districtComboBox = new JComboBox<>(districts);

        JLabel genderLabel = new JLabel("Sexe:");
        JRadioButton maleButton = new JRadioButton("Equipe Masculine");
        JRadioButton femaleButton = new JRadioButton("Equipe Féminine");
        ButtonGroup genderGroup = new ButtonGroup();
        genderGroup.add(maleButton);
        genderGroup.add(femaleButton);

        JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        genderPanel.add(maleButton);
        genderPanel.add(femaleButton);

        formPanel.add(nameLabel);
        formPanel.add(nameField);
        formPanel.add(levelLabel);
        formPanel.add(levelComboBox);
        formPanel.add(districtLabel);
        formPanel.add(districtComboBox);
        formPanel.add(genderLabel);
        formPanel.add(genderPanel);

        // Buttons for Deleting & Saving
        JButton deleteButton = new JButton("Supprimer");
        JButton saveButton = new JButton("Enregistrer");

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.add(deleteButton);
        actionPanel.add(saveButton);

        // Combine Everything
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(formPanel, BorderLayout.CENTER);
        bottomPanel.add(actionPanel, BorderLayout.SOUTH);

        panel.add(listPanel, BorderLayout.NORTH);
        panel.add(buttonPanel, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private static JPanel createMatchPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // **Match List**
        DefaultListModel<String> matchListModel = new DefaultListModel<>();
        JList<String> matchList = new JList<>(matchListModel);
        JScrollPane matchScrollPane = new JScrollPane(matchList);

        matchListModel.addElement("Match 1");
        matchListModel.addElement("Match 2");
        matchListModel.addElement("FC Mulhouse U17 D1 - Strasbourg FC U17");

        JLabel matchTitle = new JLabel("Match à venir", SwingConstants.LEFT);

        // **Match Details Panel**
        JPanel detailsPanel = new JPanel(new GridLayout(4, 2, 10, 10));

        JLabel homeTeamLabel = new JLabel("Equipe Domicile");
        JTextField homeTeamField = new JTextField("FC Strasbourg U17");
        homeTeamField.setEditable(false);

        JLabel awayTeamLabel = new JLabel("Equipe Visiteur");
        JTextField awayTeamField = new JTextField("FC Mulhouse U17");
        awayTeamField.setEditable(false);

        JLabel venueLabel = new JLabel("Lieu");
        JTextField venueField = new JTextField("Stade de la Meinau");
        venueField.setEditable(false);

        JLabel dateLabel = new JLabel("Date et heure");
        JTextField dateField = new JTextField("12/01/2025 18h");
        dateField.setEditable(false);

        detailsPanel.add(homeTeamLabel);
        detailsPanel.add(homeTeamField);
        detailsPanel.add(awayTeamLabel);
        detailsPanel.add(awayTeamField);
        detailsPanel.add(venueLabel);
        detailsPanel.add(venueField);
        detailsPanel.add(dateLabel);
        detailsPanel.add(dateField);

        // **Score & Points Panel**
        JPanel scorePanel = new JPanel(new GridLayout(3, 2, 10, 10));

        JLabel scoreLabel = new JLabel("Score");
        JTextField scoreField = new JTextField("0 - 2");

        JLabel homePointsLabel = new JLabel("Points Domicile");
        JTextField homePointsField = new JTextField("0");

        JLabel awayPointsLabel = new JLabel("Points Visiteur");
        JTextField awayPointsField = new JTextField("3");

        scorePanel.add(scoreLabel);
        scorePanel.add(scoreField);
        scorePanel.add(homePointsLabel);
        scorePanel.add(homePointsField);
        scorePanel.add(awayPointsLabel);
        scorePanel.add(awayPointsField);

        // **Combine Everything**
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(matchTitle, BorderLayout.NORTH);
        topPanel.add(matchScrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(detailsPanel, BorderLayout.NORTH);
        bottomPanel.add(scorePanel, BorderLayout.SOUTH);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(bottomPanel, BorderLayout.CENTER);

        return panel;
    }
}

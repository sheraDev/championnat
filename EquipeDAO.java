import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame {
    public MainFrame() {
        setTitle("Gestion de Championnat de Football");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Création du JTabbedPane
        JTabbedPane tabbedPane = new JTabbedPane();

        // Onglet Équipe
        JPanel equipePanel = new JPanel();
        equipePanel.setLayout(new GridLayout(5, 2));
        
        JLabel nomLabel = new JLabel("Nom de l'équipe :");
        JTextField nomField = new JTextField();
        JLabel clubLabel = new JLabel("Club :");
        JTextField clubField = new JTextField();
        JLabel divisionLabel = new JLabel("Division :");
        JTextField divisionField = new JTextField();
        JLabel sexeLabel = new JLabel("Sexe :");
        String[] sexes = {"Masculin", "Féminin"};
        JComboBox<String> sexeCombo = new JComboBox<>(sexes);
        JButton addButton = new JButton("Ajouter Équipe");

        equipePanel.add(nomLabel);
        equipePanel.add(nomField);
        equipePanel.add(clubLabel);
        equipePanel.add(clubField);
        equipePanel.add(divisionLabel);
        equipePanel.add(divisionField);
        equipePanel.add(sexeLabel);
        equipePanel.add(sexeCombo);
        equipePanel.add(new JLabel()); // Espace vide
        equipePanel.add(addButton);

        // Action du bouton Ajouter
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nom = nomField.getText();
                String club = clubField.getText();
                String division = divisionField.getText();
                String sexe = (String) sexeCombo.getSelectedItem();
                
                JOptionPane.showMessageDialog(null, "Équipe ajoutée : " + nom + "\nClub : " + club + "\nDivision : " + division + "\nSexe : " + sexe);
            }
        });

        // Onglet Home
        JPanel homePanel = new JPanel();
        homePanel.add(new JLabel("Bienvenue dans l'application de gestion de championnat"));

        // Onglet Match (Placeholder pour l'instant)
        JPanel matchPanel = new JPanel();
        matchPanel.add(new JLabel("Gestion des matchs (à implémenter)"));

        // Ajouter les onglets
        tabbedPane.addTab("Équipe", equipePanel);
        tabbedPane.addTab("Home", homePanel);
        tabbedPane.addTab("Match", matchPanel);

        // Ajouter au frame
        add(tabbedPane);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StadeDAO {
    private static final String URL = "jdbc:mariadb://localhost:3306/championnat_football";
    private static final String LOGIN = "root";
    private static final String PASS = "root";

    public StadeDAO() {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Erreur : Impossible de charger le driver MariaDB");
            e.printStackTrace();
        }
    }

    // Ajouter un stade
    public int ajouterStade(String nom, String ville, int capacite) {
        String query = "INSERT INTO Stade (nom, ville, capacite) VALUES (?, ?, ?)";
        try (Connection con = DriverManager.getConnection(URL, LOGIN, PASS);
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, nom);
            ps.setString(2, ville);
            ps.setInt(3, capacite);
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Récupérer la liste des stades avec le format "Nom - Ville"
    public List<String> getListeStades() {
        List<String> stades = new ArrayList<>();
        String query = "SELECT * FROM Stade";
        try (Connection con = DriverManager.getConnection(URL, LOGIN, PASS);
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                stades.add(rs.getString("nom") + " - " + rs.getString("ville"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stades;
    }

    // Nouvelle méthode pour obtenir l'ID du stade à partir du libellé "Nom - Ville"
    public int getStadeIdByDisplay(String display) {
        int id = -1;
        // On s'attend à un format "Nom - Ville"
        String[] parts = display.split(" - ");
        if (parts.length < 2) return id;
        String nom = parts[0].trim();
        String ville = parts[1].trim();
        String query = "SELECT id FROM Stade WHERE nom = ? AND ville = ?";
        try (Connection con = DriverManager.getConnection(URL, LOGIN, PASS);
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, nom);
            ps.setString(2, ville);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    id = rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }

    // Mettre à jour un stade
    public int mettreAJourStade(int id, String nom, String ville, int capacite) {
        String query = "UPDATE Stade SET nom = ?, ville = ?, capacite = ? WHERE id = ?";
        try (Connection con = DriverManager.getConnection(URL, LOGIN, PASS);
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, nom);
            ps.setString(2, ville);
            ps.setInt(3, capacite);
            ps.setInt(4, id);
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Supprimer un stade
    public int supprimerStade(int id) {
        String query = "DELETE FROM Stade WHERE id = ?";
        try (Connection con = DriverManager.getConnection(URL, LOGIN, PASS);
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, id);
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Main de test
    public static void main(String[] args) {
        StadeDAO stadeDAO = new StadeDAO();

        // Ajouter un stade
        stadeDAO.ajouterStade("Stade de France", "Paris", 80000);

        // Afficher la liste des stades
        List<String> stades = stadeDAO.getListeStades();
        System.out.println("Liste des stades :");
        for (String stade : stades) {
            System.out.println(stade);
        }

        // Mettre à jour un stade
        stadeDAO.mettreAJourStade(1, "Parc des Princes", "Paris", 50000);

        // Afficher après mise à jour
        stades = stadeDAO.getListeStades();
        System.out.println("Liste après mise à jour :");
        for (String stade : stades) {
            System.out.println(stade);
        }

        // Supprimer un stade
        stadeDAO.supprimerStade(1);

        // Afficher après suppression
        stades = stadeDAO.getListeStades();
        System.out.println("Liste après suppression :");
        for (String stade : stades) {
            System.out.println(stade);
        }
    }
}

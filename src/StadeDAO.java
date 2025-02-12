import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for managing stadiums.
 */
public class StadeDAO {
    private static final String URL = DatabaseConfig.getProperty("db.url");
    private static final String LOGIN = DatabaseConfig.getProperty("db.user");
    private static final String PASS = DatabaseConfig.getProperty("db.password");

    /**
     * Constructs a new StadeDAO and loads the MariaDB JDBC driver.
     */
    public StadeDAO() {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Erreur : Impossible de charger le driver MariaDB");
            e.printStackTrace();
        }
    }

    /**
     * Adds a new stadium to the database.
     *
     * @param nom     the name of the stadium
     * @param ville   the city where the stadium is located
     * @param capacite the capacity of the stadium
     * @return the number of rows affected (typically 1 if successful)
     */
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

    /**
     * Retrieves a list of stadiums formatted as "Name - City".
     *
     * @return a list of strings representing the stadiums
     */
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

    /**
     * Retrieves the stadium ID based on its display label formatted as "Name - City".
     *
     * @param display the display string in the format "Name - City"
     * @return the stadium ID if found; -1 otherwise
     */
    public int getStadeIdByDisplay(String display) {
        int id = -1;
        // Expected format "Name - City"
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

    /**
     * Updates an existing stadium in the database.
     *
     * @param id      the stadium ID to update
     * @param nom     the new name of the stadium
     * @param ville   the new city of the stadium
     * @param capacite the new capacity of the stadium
     * @return the number of rows affected (typically 1 if successful)
     */
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

    /**
     * Deletes a stadium from the database.
     *
     * @param id the ID of the stadium to delete
     * @return the number of rows affected (typically 1 if successful)
     */
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

    /**
     * Main method for testing the StadeDAO functionality.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        StadeDAO stadeDAO = new StadeDAO();

        // Add a stadium
        stadeDAO.ajouterStade("Stade de France", "Paris", 80000);

        // Display the list of stadiums
        List<String> stades = stadeDAO.getListeStades();
        System.out.println("Liste des stades :");
        for (String stade : stades) {
            System.out.println(stade);
        }

        // Update a stadium
        stadeDAO.mettreAJourStade(1, "Parc des Princes", "Paris", 50000);

        // Display the list after update
        stades = stadeDAO.getListeStades();
        System.out.println("Liste après mise à jour :");
        for (String stade : stades) {
            System.out.println(stade);
        }

        // Delete a stadium
        stadeDAO.supprimerStade(1);

        // Display the list after deletion
        stades = stadeDAO.getListeStades();
        System.out.println("Liste après suppression :");
        for (String stade : stades) {
            System.out.println(stade);
        }
    }
}

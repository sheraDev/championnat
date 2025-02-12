import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for managing championships.
 */
public class ChampionnatDAO {
    private static final String URL = DatabaseConfig.getProperty("db.url");
    private static final String LOGIN = DatabaseConfig.getProperty("db.user");
    private static final String PASS = DatabaseConfig.getProperty("db.password");

    /**
     * Constructor that loads the MariaDB JDBC driver.
     */
    public ChampionnatDAO() {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Error: Unable to load MariaDB driver.");
            e.printStackTrace();
        }
    }

    /**
     * Adds a championship to the database.
     * 
     * @param nom    The name of the championship.
     * @param saison The season of the championship.
     * @return The number of rows affected (normally 1 if successful).
     */
    public int ajouterChampionnat(String nom, String saison) {
        int retour = 0;
        try (Connection con = DriverManager.getConnection(URL, LOGIN, PASS);
             PreparedStatement ps = con.prepareStatement("INSERT INTO Championnat (nom, saison) VALUES (?, ?)")) {

            ps.setString(1, nom);
            ps.setString(2, saison);
            retour = ps.executeUpdate();

            System.out.println("Championship added: " + nom + " - Season: " + saison);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return retour;
    }

    /**
     * Retrieves the list of championships from the database.
     * 
     * @return A list of strings containing the name and season of each championship.
     */
    public List<String> getListeChampionnat() {
        List<String> championnats = new ArrayList<>();
        try (Connection con = DriverManager.getConnection(URL, LOGIN, PASS);
             PreparedStatement ps = con.prepareStatement("SELECT * FROM Championnat");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                championnats.add(rs.getString("nom") + " - Season: " + rs.getString("saison"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return championnats;
    }

    /**
     * Deletes a championship from the database by its name.
     * 
     * @param nom The name of the championship to delete.
     * @return The number of rows affected (normally 1 if successful).
     */
    public int supprimerChampionnat(String nom) {
        int retour = 0;
        try (Connection con = DriverManager.getConnection(URL, LOGIN, PASS);
             PreparedStatement ps = con.prepareStatement("DELETE FROM Championnat WHERE nom = ?")) {

            ps.setString(1, nom);
            retour = ps.executeUpdate();

            System.out.println("Championship deleted: " + nom);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return retour;
    }

    /**
     * Retrieves the ID of a championship based on its name.
     * 
     * @param nom The name of the championship.
     * @return The ID of the championship if found, -1 otherwise.
     */
    public int getChampionnatId(String nom) {
        int id = -1;
        String sql = "SELECT id FROM Championnat WHERE nom = ?";
        try (Connection con = DriverManager.getConnection(URL, LOGIN, PASS);
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nom);
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
     * Main method to test the DAO.
     */
    public static void main(String[] args) {
        ChampionnatDAO championnatDAO = new ChampionnatDAO();

        // Adding championships
        championnatDAO.ajouterChampionnat("Ligue 1", "2023-2024");
        championnatDAO.ajouterChampionnat("Premier League", "2023-2024");

        // Displaying championships
        List<String> championnats = championnatDAO.getListeChampionnat();
        System.out.println("List of championships:");
        for (String championnat : championnats) {
            System.out.println(championnat);
        }

        // Deleting a championship
        championnatDAO.supprimerChampionnat("Premier League");

        // Displaying championships after deletion
        championnats = championnatDAO.getListeChampionnat();
        System.out.println("List after deletion:");
        for (String championnat : championnats) {
            System.out.println(championnat);
        }
    }
}

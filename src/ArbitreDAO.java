import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for the Arbitre entity.
 */
public class ArbitreDAO {
    private static final String URL = DatabaseConfig.getProperty("db.url");
    private static final String LOGIN = DatabaseConfig.getProperty("db.user");
    private static final String PASS = DatabaseConfig.getProperty("db.password");

    /**
     * Constructs an ArbitreDAO and attempts to load the MariaDB JDBC driver.
     */
    public ArbitreDAO() {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Erreur : Impossible de charger le driver MariaDB");
            e.printStackTrace();
        }
    }

    /**
     * Adds a referee to the Arbitre table.
     * <p>
     * The ID is auto-incremented in the database.
     * </p>
     *
     * @param nom the name of the referee.
     * @return the number of rows affected (normally 1 on success).
     */
    public int ajouterArbitre(String nom) {
        int retour = 0;
        String query = "INSERT INTO Arbitre (nom) VALUES (?)";
        try (Connection con = DriverManager.getConnection(URL, LOGIN, PASS);
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, nom);
            retour = ps.executeUpdate();
            System.out.println("Arbitre ajouté : " + nom);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return retour;
    }

    /**
     * Retrieves the list of referees registered in the database.
     *
     * @return a list of strings containing the name of each referee.
     */
    public List<String> getListeArbitres() {
        List<String> arbitres = new ArrayList<>();
        String query = "SELECT * FROM Arbitre";
        try (Connection con = DriverManager.getConnection(URL, LOGIN, PASS);
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                arbitres.add(rs.getString("nom"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return arbitres;
    }

    /**
     * Deletes a referee from the database based on their name.
     *
     * @param nom the name of the referee to delete.
     * @return the number of rows affected (normally 1 on success).
     */
    public int supprimerArbitre(String nom) {
        int retour = 0;
        String query = "DELETE FROM Arbitre WHERE nom = ?";
        try (Connection con = DriverManager.getConnection(URL, LOGIN, PASS);
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, nom);
            retour = ps.executeUpdate();
            System.out.println("Arbitre supprimé : " + nom);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return retour;
    }

    /**
     * Retrieves the ID of a referee based on a display string.
     * <p>
     * The display string can be in the format "Name - ..." or simply "Name".
     * </p>
     *
     * @param display the display string containing the referee's name (possibly followed by " - " and additional information).
     * @return the ID of the referee if found, or -1 otherwise.
     */
    public int getArbitreIdByDisplay(String display) {
        int id = -1;
        String nom;
        if (display.contains(" - ")) {
            String[] parts = display.split(" - ");
            nom = parts[0].trim();
        } else {
            nom = display.trim();
        }
        String query = "SELECT id FROM Arbitre WHERE nom = ?";
        try (Connection con = DriverManager.getConnection(URL, LOGIN, PASS);
             PreparedStatement ps = con.prepareStatement(query)) {
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
    
    public static void main(String[] args) {
    }
}

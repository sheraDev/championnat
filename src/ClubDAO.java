import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for managing clubs.
 */
public class ClubDAO {
    private static final String URL = DatabaseConfig.getProperty("db.url");
    private static final String LOGIN = DatabaseConfig.getProperty("db.user");
    private static final String PASS = DatabaseConfig.getProperty("db.password");

    public ClubDAO() {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Error: Unable to load the MariaDB driver");
            e.printStackTrace();
        }
    }

    /**
     * Creates a club.
     * 
     * @param nom   The name of the club.
     * @param ville The city where the club is based.
     * @return The number of affected rows (should be 1 if successful).
     */
    public int ajouterClub(String nom, String ville) {
        int retour = 0;
        try (Connection con = DriverManager.getConnection(URL, LOGIN, PASS);
             PreparedStatement ps = con.prepareStatement("INSERT INTO Club (nom, ville) VALUES (?, ?)")) {
            
            ps.setString(1, nom);
            ps.setString(2, ville);
            retour = ps.executeUpdate();
            
            System.out.println("Club added: " + nom + " - " + ville);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return retour;
    }

    /**
     * Retrieves the list of clubs.
     * 
     * @return A list of strings containing club names and their cities.
     */
    public List<String> getListeClubs() {
        List<String> clubs = new ArrayList<>();
        try (Connection con = DriverManager.getConnection(URL, LOGIN, PASS);
             PreparedStatement ps = con.prepareStatement("SELECT * FROM Club");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                clubs.add(rs.getString("nom") + " - " + rs.getString("ville"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clubs;
    }

    /**
     * Deletes a club by its name.
     * 
     * @param nom The name of the club to delete.
     * @return The number of affected rows (should be 1 if successful).
     */
    public int supprimerClub(String nom) {
        int retour = 0;
        try (Connection con = DriverManager.getConnection(URL, LOGIN, PASS);
             PreparedStatement ps = con.prepareStatement("DELETE FROM Club WHERE nom = ?")) {

            ps.setString(1, nom);
            retour = ps.executeUpdate();

            System.out.println("Club deleted: " + nom);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return retour;
    }

    /**
     * Retrieves the list of clubs as Club objects.
     * 
     * @return A list of Club objects.
     */
    public List<Club> getClubs() {
        List<Club> clubs = new ArrayList<>();
        try (Connection con = DriverManager.getConnection(URL, LOGIN, PASS);
             PreparedStatement ps = con.prepareStatement("SELECT * FROM Club");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String nom = rs.getString("nom");
                String ville = rs.getString("ville");
                clubs.add(new Club(id, nom, ville));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clubs;
    }

    /**
     * Retrieves the ID of a club by its name.
     * 
     * @param clubName The name of the club.
     * @return The ID of the club if found, -1 otherwise.
     */
    public int getClubId(String clubName) {
        int id = -1;
        try (Connection con = DriverManager.getConnection(URL, LOGIN, PASS);
             PreparedStatement ps = con.prepareStatement("SELECT id FROM Club WHERE nom = ?")) {
            ps.setString(1, clubName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                id = rs.getInt("id");
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }

    /**
     * Main method 
     */
    public static void main(String[] args) {
    }
}

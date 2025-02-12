import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for managing teams (Equipe) in the database.
 */
public class EquipeDAO {

    /**
     * Database connection URL.
     */
    private static final String URL = DatabaseConfig.getProperty("db.url");

    /**
     * Database login username.
     */
    private static final String LOGIN = DatabaseConfig.getProperty("db.user");

    /**
     * Database login password.
     */
    private static final String PASS = DatabaseConfig.getProperty("db.password");

    /**
     * Constructs an EquipeDAO and loads the MariaDB JDBC driver.
     */
    public EquipeDAO() {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            System.err.println("Error: Unable to load the MariaDB driver");
            ex.printStackTrace();
        }
    }

    /**
     * Adds a team to the Equipe table.
     *
     * @param nom      the name of the team (should be unique)
     * @param clubId   the club identifier (foreign key referring to the Club table)
     * @param division the division in which the team competes
     * @param sexe     the gender of the team ("M" for male or "F" for female)
     * @param niveau   the level of the team
     * @return the number of rows affected (normally 1 if successful)
     */
    public int ajouterEquipe(String nom, int clubId, String division, String sexe, String niveau) {
        int rowsAffected = 0;
        String sql = "INSERT INTO Equipe (nom, club_id, division, sexe, niveau) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL, LOGIN, PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nom);
            ps.setInt(2, clubId);
            ps.setString(3, division);
            ps.setString(4, sexe);
            ps.setString(5, niveau);
            rowsAffected = ps.executeUpdate();
            System.out.println("Team added: " + nom);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return rowsAffected;
    }

    /**
     * Modifies an existing team in the Equipe table.
     *
     * @param ancienNom the current name of the team to modify
     * @param nouveauNom the new name for the team
     * @param clubId    the club identifier
     * @param division  the division of the team
     * @param sexe      the gender of the team
     * @param niveau    the new level of the team
     * @return the number of rows affected (normally 1 if successful)
     */
    public int modifierEquipe(String ancienNom, String nouveauNom, int clubId, String division, String sexe, String niveau) {
        int rowsAffected = 0;
        String sql = "UPDATE Equipe SET nom = ?, club_id = ?, division = ?, sexe = ?, niveau = ? WHERE nom = ?";
        try (Connection conn = DriverManager.getConnection(URL, LOGIN, PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nouveauNom);
            ps.setInt(2, clubId);
            ps.setString(3, division);
            ps.setString(4, sexe);
            ps.setString(5, niveau);
            ps.setString(6, ancienNom);
            rowsAffected = ps.executeUpdate();
            System.out.println("Team modified: " + nouveauNom);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return rowsAffected;
    }

    /**
     * Retrieves the list of teams registered in the database.
     * Performs a join with the Club table to obtain the club name.
     *
     * @return a list of Equipe objects
     */
    public List<Equipe> getListeEquipes() {
        List<Equipe> equipes = new ArrayList<>();
        String sql = "SELECT e.nom, c.nom AS club, e.division, e.sexe, e.niveau " +
                     "FROM Equipe e JOIN Club c ON e.club_id = c.id";
        try (Connection conn = DriverManager.getConnection(URL, LOGIN, PASS);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String nom = rs.getString("nom");
                String club = rs.getString("club");
                String division = rs.getString("division");
                String sexe = rs.getString("sexe");
                String niveau = rs.getString("niveau");
                equipes.add(new Equipe(nom, club, division, sexe, niveau));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return equipes;
    }

    /**
     * Deletes a team from the database based on its name.
     *
     * @param nom the name of the team to delete
     * @return the number of rows affected (normally 1 if successful)
     */
    public int supprimerEquipe(String nom) {
        int rowsAffected = 0;
        String sql = "DELETE FROM Equipe WHERE nom = ?";
        try (Connection conn = DriverManager.getConnection(URL, LOGIN, PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nom);
            rowsAffected = ps.executeUpdate();
            System.out.println("Team deleted: " + nom);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return rowsAffected;
    }

    /**
     * Retrieves the identifier of a team based on its name.
     *
     * @param nom the name of the team
     * @return the identifier of the team if found, -1 otherwise
     */
    public int getEquipeId(String nom) {
        int id = -1;
        String sql = "SELECT id FROM Equipe WHERE nom = ?";
        try (Connection conn = DriverManager.getConnection(URL, LOGIN, PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {
         
            ps.setString(1, nom);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    id = rs.getInt("id");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return id;
    }
}

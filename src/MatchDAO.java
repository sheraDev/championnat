import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for managing matches.
 */
public class MatchDAO {
    private static final String URL = DatabaseConfig.getProperty("db.url");
    private static final String LOGIN = DatabaseConfig.getProperty("db.user");
    private static final String PASS = DatabaseConfig.getProperty("db.password");

    /**
     * Constructs a new MatchDAO and loads the MariaDB JDBC driver.
     */
    public MatchDAO() {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Error: Unable to load the MariaDB driver");
            e.printStackTrace();
        }
    }

    /**
     * Adds a match to the database.
     *
     * @param equipeDomicile  the ID of the home team
     * @param equipeExterieur the ID of the away team
     * @param championnatId  the championship ID
     * @param stadeId       the stadium ID
     * @param arbitreId     the referee ID
     * @param dateMatch     the date of the match
     * @param scoreDomicile the home team's score
     * @param scoreExterieur the away team's score
     * @param statut        the status of the match
     * @return the number of rows affected (typically 1 if successful)
     */
    public int ajouterMatch(int equipeDomicile, int equipeExterieur, int championnatId, int stadeId, int arbitreId,
                            String dateMatch, int scoreDomicile, int scoreExterieur, String statut) {
        int retour = 0;
        String sql = "INSERT INTO Matchs (equipe_domicile, equipe_exterieur, championnat_id, stade_id, arbitre_id, date_match, score_domicile, score_exterieur, statut) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DriverManager.getConnection(URL, LOGIN, PASS);
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, equipeDomicile);
            ps.setInt(2, equipeExterieur);
            ps.setInt(3, championnatId);
            ps.setInt(4, stadeId);
            ps.setInt(5, arbitreId);
            ps.setString(6, dateMatch);
            ps.setInt(7, scoreDomicile);
            ps.setInt(8, scoreExterieur);
            ps.setString(9, statut);
            retour = ps.executeUpdate();

            System.out.println("Match added: " + equipeDomicile + " vs " + equipeExterieur + " - " + dateMatch + " - Status: " + statut);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return retour;
    }

    /**
     * Retrieves the list of matches as formatted strings.
     *
     * @return a list of match descriptions
     */
    public List<String> getListeMatchs() {
        List<String> matchs = new ArrayList<>();
        String sql = "SELECT m.id, e1.nom AS equipe_domicile, e2.nom AS equipe_exterieur, c.nom AS championnat, s.nom AS stade, a.nom AS arbitre, m.date_match, m.score_domicile, m.score_exterieur, m.statut " +
                     "FROM Matchs m " +
                     "JOIN Equipe e1 ON m.equipe_domicile = e1.id " +
                     "JOIN Equipe e2 ON m.equipe_exterieur = e2.id " +
                     "JOIN Championnat c ON m.championnat_id = c.id " +
                     "JOIN Stade s ON m.stade_id = s.id " +
                     "JOIN Arbitre a ON m.arbitre_id = a.id " +
                     "ORDER BY m.date_match";
        try (Connection con = DriverManager.getConnection(URL, LOGIN, PASS);
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                matchs.add(rs.getString("equipe_domicile") + " vs " + rs.getString("equipe_exterieur") +
                           " | Championnat: " + rs.getString("championnat") +
                           " | Stade: " + rs.getString("stade") +
                           " | Arbitre: " + rs.getString("arbitre") +
                           " | Date: " + rs.getString("date_match") +
                           " | Status: " + rs.getString("statut") +
                           " | Score: " + rs.getInt("score_domicile") + "-" + rs.getInt("score_exterieur"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return matchs;
    }

    /**
     * Updates the score of a match.
     *
     * @param matchId       the match ID
     * @param scoreDomicile the home team's new score
     * @param scoreExterieur the away team's new score
     * @return the number of rows affected (typically 1 if successful)
     */
    public int mettreAJourScore(int matchId, int scoreDomicile, int scoreExterieur) {
        int retour = 0;
        String sql = "UPDATE Matchs SET score_domicile = ?, score_exterieur = ? WHERE id = ?";
        try (Connection con = DriverManager.getConnection(URL, LOGIN, PASS);
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, scoreDomicile);
            ps.setInt(2, scoreExterieur);
            ps.setInt(3, matchId);
            retour = ps.executeUpdate();

            System.out.println("Score updated for match ID " + matchId + " : " + scoreDomicile + "-" + scoreExterieur);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return retour;
    }

    /**
     * Deletes a match from the database.
     *
     * @param matchId the ID of the match to delete
     * @return the number of rows affected (typically 1 if successful)
     */
    public int supprimerMatch(int matchId) {
        int retour = 0;
        String sql = "DELETE FROM Matchs WHERE id = ?";
        try (Connection con = DriverManager.getConnection(URL, LOGIN, PASS);
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, matchId);
            retour = ps.executeUpdate();

            System.out.println("Match deleted ID : " + matchId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return retour;
    }

    /**
     * Retrieves the list of matches as objects.
     * The 'statut' field is retrieved and passed to the Match object.
     *
     * @return a list of Match objects
     */
    public List<MatchPanel.Match> getListeMatchsObjects() {
        List<MatchPanel.Match> matches = new ArrayList<>();
        String sql = "SELECT m.id, e1.nom AS equipe_domicile, e2.nom AS equipe_exterieur, c.nom AS championnat, s.nom AS stade, a.nom AS arbitre, m.date_match, m.score_domicile, m.score_exterieur, m.statut " +
                     "FROM Matchs m " +
                     "JOIN Equipe e1 ON m.equipe_domicile = e1.id " +
                     "JOIN Equipe e2 ON m.equipe_exterieur = e2.id " +
                     "JOIN Championnat c ON m.championnat_id = c.id " +
                     "JOIN Stade s ON m.stade_id = s.id " +
                     "JOIN Arbitre a ON m.arbitre_id = a.id " +
                     "ORDER BY m.date_match";
        try (Connection con = DriverManager.getConnection(URL, LOGIN, PASS);
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String equipeDomicile = rs.getString("equipe_domicile");
                String equipeExterieur = rs.getString("equipe_exterieur");
                String championnat = rs.getString("championnat");
                String stade = rs.getString("stade");
                String arbitre = rs.getString("arbitre");
                String dateMatch = rs.getString("date_match");
                int scoreDomicile = rs.getInt("score_domicile");
                int scoreExterieur = rs.getInt("score_exterieur");
                String statut = rs.getString("statut");

                matches.add(new MatchPanel.Match(id, equipeDomicile, equipeExterieur, championnat, stade, arbitre, dateMatch, statut, scoreDomicile, scoreExterieur));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return matches;
    }

    /**
     * Updates a match, including its status.
     *
     * @param matchId       the match ID
     * @param equipeDomicile the home team ID
     * @param equipeExterieur the away team ID
     * @param championnatId  the championship ID
     * @param stadeId       the stadium ID
     * @param arbitreId     the referee ID
     * @param dateMatch     the match date
     * @param scoreDomicile the home team's score
     * @param scoreExterieur the away team's score
     * @param statut        the status of the match
     * @return the number of rows affected (typically 1 if successful)
     */
    public int modifierMatch(int matchId, int equipeDomicile, int equipeExterieur, int championnatId, int stadeId,
                             int arbitreId, String dateMatch, int scoreDomicile, int scoreExterieur, String statut) {
        int retour = 0;
        String sql = "UPDATE Matchs SET equipe_domicile = ?, equipe_exterieur = ?, championnat_id = ?, stade_id = ?, arbitre_id = ?, date_match = ?, score_domicile = ?, score_exterieur = ?, statut = ? WHERE id = ?";
        try (Connection con = DriverManager.getConnection(URL, LOGIN, PASS);
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, equipeDomicile);
            ps.setInt(2, equipeExterieur);
            ps.setInt(3, championnatId);
            ps.setInt(4, stadeId);
            ps.setInt(5, arbitreId);
            ps.setString(6, dateMatch);
            ps.setInt(7, scoreDomicile);
            ps.setInt(8, scoreExterieur);
            ps.setString(9, statut);
            ps.setInt(10, matchId);
            retour = ps.executeUpdate();

            System.out.println("Match updated: ID " + matchId + " - Status: " + statut);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return retour;
    }

    /**
     * Main method for testing purposes.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
    }
}

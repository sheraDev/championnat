import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour la gestion des matchs.
 */
public class MatchDAO {
    private static final String URL = DatabaseConfig.getProperty("db.url");
    private static final String LOGIN = DatabaseConfig.getProperty("db.user");
    private static final String PASS = DatabaseConfig.getProperty("db.password");

    public MatchDAO() {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Erreur : Impossible de charger le driver MariaDB");
            e.printStackTrace();
        }
    }

    /**
     * Ajouter un match dans la base de données.
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

            System.out.println("Match ajouté : " + equipeDomicile + " vs " + equipeExterieur + " - " + dateMatch + " - Statut: " + statut);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return retour;
    }

    /**
     * Récupère la liste des matchs sous forme de chaînes.
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
                           " | Statut: " + rs.getString("statut") +
                           " | Score: " + rs.getInt("score_domicile") + "-" + rs.getInt("score_exterieur"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return matchs;
    }

    /**
     * Met à jour le score d'un match.
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

            System.out.println("Score mis à jour pour le match ID " + matchId + " : " + scoreDomicile + "-" + scoreExterieur);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return retour;
    }

    /**
     * Supprime un match.
     */
    public int supprimerMatch(int matchId) {
        int retour = 0;
        String sql = "DELETE FROM Matchs WHERE id = ?";
        try (Connection con = DriverManager.getConnection(URL, LOGIN, PASS);
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, matchId);
            retour = ps.executeUpdate();

            System.out.println("Match supprimé ID : " + matchId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return retour;
    }

    /**
     * Retourne la liste des matchs sous forme d'objets.
     * Le champ 'statut' est récupéré et transmis à l'objet Match.
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
     * Met à jour un match.
     * Le champ 'statut' est également mis à jour.
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

            System.out.println("Match modifié : ID " + matchId + " - Statut: " + statut);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return retour;
    }

    
    public static void main(String[] args) {
    }
}

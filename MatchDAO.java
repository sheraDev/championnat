import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour la gestion des matchs.
 */
public class MatchDAO {
    private static final String URL = "jdbc:mariadb://localhost:3306/championnat_football";
    private static final String LOGIN = "root";
    private static final String PASS = "root";

    public MatchDAO() {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Erreur : Impossible de charger le driver MariaDB");
            e.printStackTrace();
        }
    }

    /**
     * Ajouter un match dans la base de données
     */
    public int ajouterMatch(int equipeDomicile, int equipeExterieur, int championnatId, int stadeId, int arbitreId, String dateMatch) {
        int retour = 0;
        try (Connection con = DriverManager.getConnection(URL, LOGIN, PASS);
             PreparedStatement ps = con.prepareStatement("INSERT INTO Matchs (equipe_domicile, equipe_exterieur, championnat_id, stade_id, arbitre_id, date_match) VALUES (?, ?, ?, ?, ?, ?)")) {

            ps.setInt(1, equipeDomicile);
            ps.setInt(2, equipeExterieur);
            ps.setInt(3, championnatId);
            ps.setInt(4, stadeId);
            ps.setInt(5, arbitreId);
            ps.setString(6, dateMatch);
            retour = ps.executeUpdate();

            System.out.println("Match ajouté : " + equipeDomicile + " vs " + equipeExterieur + " - " + dateMatch);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return retour;
    }

    /**
     * Récupèrer la liste des matchs
     */
    public List<String> getListeMatchs() {
        List<String> matchs = new ArrayList<>();
        try (Connection con = DriverManager.getConnection(URL, LOGIN, PASS);
             PreparedStatement ps = con.prepareStatement(
                 "SELECT m.id, e1.nom AS equipe_domicile, e2.nom AS equipe_exterieur, s.nom AS stade, a.nom AS arbitre, m.date_match, m.score_domicile, m.score_exterieur " +
                 "FROM Matchs m " +
                 "JOIN Equipe e1 ON m.equipe_domicile = e1.id " +
                 "JOIN Equipe e2 ON m.equipe_exterieur = e2.id " +
                 "JOIN Stade s ON m.stade_id = s.id " +
                 "JOIN Arbitre a ON m.arbitre_id = a.id " +
                 "ORDER BY m.date_match");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                matchs.add(rs.getString("equipe_domicile") + " vs " + rs.getString("equipe_exterieur") +
                           " | Stade: " + rs.getString("stade") +
                           " | Arbitre: " + rs.getString("arbitre") +
                           " | Date: " + rs.getString("date_match") +
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
        try (Connection con = DriverManager.getConnection(URL, LOGIN, PASS);
             PreparedStatement ps = con.prepareStatement("UPDATE Matchs SET score_domicile = ?, score_exterieur = ? WHERE id = ?")) {

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
        try (Connection con = DriverManager.getConnection(URL, LOGIN, PASS);
             PreparedStatement ps = con.prepareStatement("DELETE FROM Matchs WHERE id = ?")) {

            ps.setInt(1, matchId);
            retour = ps.executeUpdate();

            System.out.println("Match supprimé ID : " + matchId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return retour;
    }

    /**
     * Test du DAO
     */
    public static void main(String[] args) {
        MatchDAO matchDAO = new MatchDAO();

        // Exemple d'ajout de match
        int matchId = matchDAO.ajouterMatch(1, 2, 1, 1, 1, "2024-03-10 15:00:00");

        // Affichage des matchs
        List<String> matchs = matchDAO.getListeMatchs();
        System.out.println("Liste des matchs :");
        for (String match : matchs) {
            System.out.println(match);
        }

        // Mise à jour du score d'un match
        if (matchId > 0) {
            matchDAO.mettreAJourScore(matchId, 2, 1);
        }

        // Suppression d'un match
        if (matchId > 0) {
            matchDAO.supprimerMatch(matchId);
        }

        // Affichage après suppression
        matchs = matchDAO.getListeMatchs();
        System.out.println("Liste après suppression :");
        for (String match : matchs) {
            System.out.println(match);
        }
    }
}

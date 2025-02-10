import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ResultatDAO {
    private static final String URL = "jdbc:mariadb://localhost:3306/championnat_football";
    private static final String LOGIN = "root";
    private static final String PASS = "root";

    public ResultatDAO() {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Erreur : Impossible de charger le driver MariaDB");
            e.printStackTrace();
        }
    }

    public int ajouterResultat(int matchId, String score, int pointsDomicile, int pointsExterieur) {
        String query = "INSERT INTO Resultat (match_id, score, PointEquipeDomicile, PointEquipeVisiteur) VALUES (?, ?, ?, ?)";
        try (Connection con = DriverManager.getConnection(URL, LOGIN, PASS);
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, matchId);
            ps.setString(2, score);
            ps.setInt(3, pointsDomicile);
            ps.setInt(4, pointsExterieur);
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<String> getListeResultats() {
        List<String> resultats = new ArrayList<>();
        String query = "SELECT * FROM Resultat";
        try (Connection con = DriverManager.getConnection(URL, LOGIN, PASS);
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                resultats.add("Match ID: " + rs.getInt("match_id") + " - Score: " + rs.getString("score") +
                        " - Points Domicile: " + rs.getInt("PointEquipeDomicile") +
                        " - Points Extérieur: " + rs.getInt("PointEquipeVisiteur"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultats;
    }

    public int supprimerResultat(int matchId) {
        String query = "DELETE FROM Resultat WHERE match_id = ?";
        try (Connection con = DriverManager.getConnection(URL, LOGIN, PASS);
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, matchId);
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void main(String[] args) {
        ResultatDAO resultatDAO = new ResultatDAO();

        // Ajouter un résultat
        resultatDAO.ajouterResultat(1, "2-1", 3, 0);

        // Afficher la liste des résultats
        List<String> resultats = resultatDAO.getListeResultats();
        System.out.println("Liste des résultats :");
        for (String resultat : resultats) {
            System.out.println(resultat);
        }

        // Supprimer un résultat
        resultatDAO.supprimerResultat(1);

        // Afficher après suppression
        resultats = resultatDAO.getListeResultats();
        System.out.println("Liste après suppression :");
        for (String resultat : resultats) {
            System.out.println(resultat);
        }
    }
}

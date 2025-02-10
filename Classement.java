import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour la gestion du classement.
 */
public class ClassementDAO {
    private static final String URL = "jdbc:mariadb://localhost:3306/championnat_football";
    private static final String LOGIN = "root";
    private static final String PASS = "ton_mot_de_passe";

    public ClassementDAO() {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Erreur : Impossible de charger le driver MariaDB");
            e.printStackTrace();
        }
    }

    /**
     * Ajoute une équipe au classement.
     */
    public int ajouterEquipeAuClassement(int equipeId, int championnatId) {
        int retour = 0;
        try (Connection con = DriverManager.getConnection(URL, LOGIN, PASS);
             PreparedStatement ps = con.prepareStatement(
                 "INSERT INTO Classement (equipe_id, championnat_id, points, victoires, defaites, matchs_nuls, buts_pour, buts_contre, difference_buts) " +
                 "VALUES (?, ?, 0, 0, 0, 0, 0, 0, 0)")) {

            ps.setInt(1, equipeId);
            ps.setInt(2, championnatId);
            retour = ps.executeUpdate();

            System.out.println("Équipe ajoutée au classement : ID " + equipeId + " - Championnat " + championnatId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return retour;
    }

    /**
     * Met à jour le classement après un match.
     */
    public int mettreAJourClassement(int equipeId, int championnatId, int butsPour, int butsContre) {
        int retour = 0;
        int points = (butsPour > butsContre) ? 3 : (butsPour == butsContre ? 1 : 0);
        int victoire = (butsPour > butsContre) ? 1 : 0;
        int defaite = (butsPour < butsContre) ? 1 : 0;
        int nul = (butsPour == butsContre) ? 1 : 0;
        int diff = butsPour - butsContre;

        try (Connection con = DriverManager.getConnection(URL, LOGIN, PASS);
             PreparedStatement ps = con.prepareStatement(
                 "UPDATE Classement SET points = points + ?, victoires = victoires + ?, defaites = defaites + ?, " +
                 "matchs_nuls = matchs_nuls + ?, buts_pour = buts_pour + ?, buts_contre = buts_contre + ?, " +
                 "difference_buts = difference_buts + ? WHERE equipe_id = ? AND championnat_id = ?")) {

            ps.setInt(1, points);
            ps.setInt(2, victoire);
            ps.setInt(3, defaite);
            ps.setInt(4, nul);
            ps.setInt(5, butsPour);
            ps.setInt(6, butsContre);
            ps.setInt(7, diff);
            ps.setInt(8, equipeId);
            ps.setInt(9, championnatId);
            retour = ps.executeUpdate();

            System.out.println("Classement mis à jour pour l'équipe ID " + equipeId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return retour;
    }

    /**
     * Récupère le classement d'un championnat.
     */
    public List<String> getClassement(int championnatId) {
        List<String> classement = new ArrayList<>();
        try (Connection con = DriverManager.getConnection(URL, LOGIN, PASS);
             PreparedStatement ps = con.prepareStatement(
                 "SELECT e.nom, c.points, c.victoires, c.defaites, c.matchs_nuls, c.buts_pour, c.buts_contre, c.difference_buts " +
                 "FROM Classement c JOIN Equipe e ON c.equipe_id = e.id WHERE c.championnat_id = ? " +
                 "ORDER BY c.points DESC, c.difference_buts DESC, c.buts_pour DESC")) {

            ps.setInt(1, championnatId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                classement.add(rs.getString("nom") + " | Points: " + rs.getInt("points") + " | Victoires: " +
                               rs.getInt("victoires") + " | Défaites: " + rs.getInt("defaites") + " | Nuls: " +
                               rs.getInt("matchs_nuls") + " | BP: " + rs.getInt("buts_pour") + " | BC: " +
                               rs.getInt("buts_contre") + " | Diff: " + rs.getInt("difference_buts"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return classement;
    }

    /**
     * Supprime une équipe du classement.
     */
    public int supprimerEquipeDuClassement(int equipeId, int championnatId) {
        int retour = 0;
        try (Connection con = DriverManager.getConnection(URL, LOGIN, PASS);
             PreparedStatement ps = con.prepareStatement("DELETE FROM Classement WHERE equipe_id = ? AND championnat_id = ?")) {

            ps.setInt(1, equipeId);
            ps.setInt(2, championnatId);
            retour = ps.executeUpdate();

            System.out.println("Équipe supprimée du classement : ID " + equipeId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return retour;
    }

    /**
     * Test du DAO
     */
    public static void main(String[] args) {
        ClassementDAO classementDAO = new ClassementDAO();

        // Ajouter des équipes au classement
        classementDAO.ajouterEquipeAuClassement(1, 1);
        classementDAO.ajouterEquipeAuClassement(2, 1);

        // Mettre à jour le classement après un match
        classementDAO.mettreAJourClassement(1, 1, 3, 1);
        classementDAO.mettreAJourClassement(2, 1, 1, 3);

        // Afficher le classement
        List<String> classement = classementDAO.getClassement(1);
        System.out.println("Classement du Championnat 1 :");
        for (String entry : classement) {
            System.out.println(entry);
        }

        // Supprimer une équipe du classement
        classementDAO.supprimerEquipeDuClassement(2, 1);

        // Affichage après suppression
        classement = classementDAO.getClassement(1);
        System.out.println("Classement après suppression :");
        for (String entry : classement) {
            System.out.println(entry);
        }
    }
}

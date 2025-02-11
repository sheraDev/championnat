import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour la gestion des championnats.
 */
public class ChampionnatDAO {
    private static final String URL = "jdbc:mariadb://localhost:3306/championnat_football";
    private static final String LOGIN = "root";
    private static final String PASS = "root";

    public ChampionnatDAO() {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Erreur : Impossible de charger le driver MariaDB");
            e.printStackTrace();
        }
    }

    /**
     * Ajoute un championnat dans la base de données.
     */
    public int ajouterChampionnat(String nom, String saison) {
        int retour = 0;
        try (Connection con = DriverManager.getConnection(URL, LOGIN, PASS);
             PreparedStatement ps = con.prepareStatement("INSERT INTO Championnat (nom, saison) VALUES (?, ?)")) {

            ps.setString(1, nom);
            ps.setString(2, saison);
            retour = ps.executeUpdate();

            System.out.println("Championnat ajouté : " + nom + " - Saison : " + saison);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return retour;
    }

    /**
     * Récupère la liste des championnats.
     */
    public List<String> getListeChampionnat() {
        List<String> championnats = new ArrayList<>();
        try (Connection con = DriverManager.getConnection(URL, LOGIN, PASS);
             PreparedStatement ps = con.prepareStatement("SELECT * FROM Championnat");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                championnats.add(rs.getString("nom") + " - Saison : " + rs.getString("saison"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return championnats;
    }

    /**
     * Supprime un championnat par son nom.
     */
    public int supprimerChampionnat(String nom) {
        int retour = 0;
        try (Connection con = DriverManager.getConnection(URL, LOGIN, PASS);
             PreparedStatement ps = con.prepareStatement("DELETE FROM Championnat WHERE nom = ?")) {

            ps.setString(1, nom);
            retour = ps.executeUpdate();

            System.out.println("Championnat supprimé : " + nom);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return retour;
    }

    /**
 * Récupère l'identifiant d'un championnat à partir de son nom.
 * @param nom Le nom du championnat.
 * @return L'identifiant du championnat s'il est trouvé, -1 sinon.
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
     * Test du DAO
     */
    public static void main(String[] args) {
        ChampionnatDAO championnatDAO = new ChampionnatDAO();

        // Ajout de championnats
        championnatDAO.ajouterChampionnat("Ligue 1", "2023-2024");
        championnatDAO.ajouterChampionnat("Premier League", "2023-2024");

        // Affichage des championnats
        List<String> championnats = championnatDAO.getListeChampionnat();
        System.out.println("Liste des championnats :");
        for (String championnat : championnats) {
            System.out.println(championnat);
        }

        // Suppression d'un championnat
        championnatDAO.supprimerChampionnat("Premier League");

        // Affichage après suppression
        championnats = championnatDAO.getListeChampionnat();
        System.out.println("Liste après suppression :");
        for (String championnat : championnats) {
            System.out.println(championnat);
        }
    }
}

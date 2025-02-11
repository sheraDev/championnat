import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour la gestion des clubs
 */
public class ClubDAO {
    private static final String URL = "jdbc:mariadb://localhost:3306/championnat_football";
    private static final String LOGIN = "root";
    private static final String PASS = "root";

    public ClubDAO() {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Erreur : Impossible de charger le driver MariaDB");
            e.printStackTrace();
        }
    }

    /**
     * Cree un club
     */
    public int ajouterClub(String nom, String ville) {
        int retour = 0;
        try (Connection con = DriverManager.getConnection(URL, LOGIN, PASS);
             PreparedStatement ps = con.prepareStatement("INSERT INTO Club (nom, ville) VALUES (?, ?)")) {
            
            ps.setString(1, nom);
            ps.setString(2, ville);
            retour = ps.executeUpdate();
            
            System.out.println("Club ajouté : " + nom + " - " + ville);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return retour;
    }

    /**
     * Récupère la liste des clubs
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
     * Supprime un club par son nom
     */
    public int supprimerClub(String nom) {
        int retour = 0;
        try (Connection con = DriverManager.getConnection(URL, LOGIN, PASS);
             PreparedStatement ps = con.prepareStatement("DELETE FROM Club WHERE nom = ?")) {

            ps.setString(1, nom);
            retour = ps.executeUpdate();

            System.out.println("Club supprimé : " + nom);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return retour;
    }

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
    

  
    public static void main(String[] args) 
    {
    }
}

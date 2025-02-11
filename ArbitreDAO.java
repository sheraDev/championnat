import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ArbitreDAO {
    private static final String URL = "jdbc:mariadb://localhost:3306/championnat_football";
    private static final String LOGIN = "root";
    private static final String PASS = "root";

    public ArbitreDAO() {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Erreur : Impossible de charger le driver MariaDB");
            e.printStackTrace();
        }
    }

    public int ajouterArbitre(String nom) {
        int retour = 0;
        String query = "INSERT INTO Arbitre (nom) VALUES (?)";
        try (Connection con = DriverManager.getConnection(URL, LOGIN, PASS);
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, nom);
            
            retour = ps.executeUpdate();
            System.out.println("Arbitre ajouté : " + nom );
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return retour;
    }

    public List<String> getListeArbitres() {
        List<String> arbitres = new ArrayList<>();
        String query = "SELECT * FROM Arbitre";
        try (Connection con = DriverManager.getConnection(URL, LOGIN, PASS);
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                arbitres.add(rs.getString("nom") );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return arbitres;
    }

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

    public int getArbitreIdByDisplay(String display) {
        int id = -1;
        // On s'attend à un format "Nom - Prénom"
        String[] parts = display.split(" - ");
        if (parts.length < 1) return id; // Vérifier que le format est correct
        String nom = parts[0].trim();
        // On pourrait également vérifier un prénom si nécessaire, mais ici on suppose uniquement le nom
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
        ArbitreDAO arbitreDAO = new ArbitreDAO();
        
        // Ajouter des arbitres
        arbitreDAO.ajouterArbitre("Pierre Dupont");
        arbitreDAO.ajouterArbitre("Jean Martin");
    }
}

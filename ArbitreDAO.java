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

    public int ajouterArbitre(String nom, String categorie) {
        int retour = 0;
        String query = "INSERT INTO Arbitre (nom, categorie) VALUES (?, ?)";
        try (Connection con = DriverManager.getConnection(URL, LOGIN, PASS);
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, nom);
            ps.setString(2, categorie);
            retour = ps.executeUpdate();
            System.out.println("Arbitre ajouté : " + nom + " - Catégorie : " + categorie);
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
                arbitres.add(rs.getString("nom") + " - " + rs.getString("categorie"));
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

    public static void main(String[] args) {
        ArbitreDAO arbitreDAO = new ArbitreDAO();
        
        // Ajouter des arbitres
        arbitreDAO.ajouterArbitre("Pierre Dupont", "Principal");
        arbitreDAO.ajouterArbitre("Jean Martin", "Assistant");
        
        // Afficher les arbitres
        List<String> arbitres = arbitreDAO.getListeArbitres();
        System.out.println("Liste des arbitres :");
        for (String arbitre : arbitres) {
            System.out.println(arbitre);
        }
        
        // Supprimer un arbitre
        arbitreDAO.supprimerArbitre("Jean Martin");
        
        // Afficher après suppression
        arbitres = arbitreDAO.getListeArbitres();
        System.out.println("Liste après suppression :");
        for (String arbitre : arbitres) {
            System.out.println(arbitre);
        }
    }
}

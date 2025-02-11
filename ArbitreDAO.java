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

    /**
     * Ajoute un arbitre dans la table Arbitre.
     * Ici, l'ID est auto-incrémenté dans la base.
     *
     * @param nom Le nom de l'arbitre.
     * @return Le nombre de lignes affectées (normalement 1 en cas de succès).
     */
    public int ajouterArbitre(String nom) {
        int retour = 0;
        String query = "INSERT INTO Arbitre (nom) VALUES (?)";
        try (Connection con = DriverManager.getConnection(URL, LOGIN, PASS);
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, nom);
            retour = ps.executeUpdate();
            System.out.println("Arbitre ajouté : " + nom);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return retour;
    }

    /**
     * Récupère la liste des arbitres enregistrés dans la base de données.
     *
     * @return Une liste de chaînes contenant le nom de chaque arbitre.
     */
    public List<String> getListeArbitres() {
        List<String> arbitres = new ArrayList<>();
        String query = "SELECT * FROM Arbitre";
        try (Connection con = DriverManager.getConnection(URL, LOGIN, PASS);
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                arbitres.add(rs.getString("nom"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return arbitres;
    }

    /**
     * Supprime un arbitre de la base de données à partir de son nom.
     *
     * @param nom Le nom de l'arbitre à supprimer.
     * @return Le nombre de lignes affectées (normalement 1 en cas de succès).
     */
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

    /**
     * Récupère l'identifiant d'un arbitre à partir d'une chaîne de type "Nom - ..." ou simplement "Nom".
     *
     * @param display La chaîne d'affichage contenant le nom (possiblement suivie d'un séparateur " - " et d'autres informations).
     * @return L'identifiant de l'arbitre s'il est trouvé, -1 sinon.
     */
    public int getArbitreIdByDisplay(String display) {
        int id = -1;
        String nom;
        if (display.contains(" - ")) {
            String[] parts = display.split(" - ");
            nom = parts[0].trim();
        } else {
            nom = display.trim();
        }
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

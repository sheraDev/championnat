import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EquipeDAO {

    // Paramètres de connexion à la base de données
    private static final String URL = "jdbc:mariadb://localhost:3306/championnat_football";
    private static final String LOGIN = "root";
    private static final String PASS = "root";

    // Constructeur : charge le driver MariaDB
    public EquipeDAO() {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            System.err.println("Erreur : Impossible de charger le driver MariaDB");
            ex.printStackTrace();
        }
    }

    /**
     * Ajoute une équipe dans la table Equipe.
     * @param nom Le nom de l'équipe (doit être unique)
     * @param clubId L'identifiant du club (clé étrangère référant à la table Club)
     * @param division La division dans laquelle l'équipe évolue
     * @param sexe Le sexe de l'équipe ("M" pour masculin ou "F" pour féminin)
     * @param niveau Le niveau de l'équipe
     * @return Le nombre de lignes affectées (normalement 1 en cas de succès)
     */
    public int ajouterEquipe(String nom, int clubId, String division, String sexe, String niveau) {
        int rowsAffected = 0;
        String sql = "INSERT INTO Equipe (nom, club_id, division, sexe, niveau) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL, LOGIN, PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nom);
            ps.setInt(2, clubId);
            ps.setString(3, division);
            ps.setString(4, sexe);
            ps.setString(5, niveau);
            rowsAffected = ps.executeUpdate();
            System.out.println("Équipe ajoutée : " + nom);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return rowsAffected;
    }

    /**
     * Modifie une équipe existante dans la table Equipe.
     * @param ancienNom Le nom de l'équipe à modifier.
     * @param nouveauNom Le nouveau nom de l'équipe.
     * @param clubId L'identifiant du club.
     * @param division La division.
     * @param sexe Le sexe de l'équipe.
     * @param niveau Le nouveau niveau de l'équipe.
     * @return Le nombre de lignes affectées (normalement 1 en cas de succès)
     */
    public int modifierEquipe(String ancienNom, String nouveauNom, int clubId, String division, String sexe, String niveau) {
        int rowsAffected = 0;
        String sql = "UPDATE Equipe SET nom = ?, club_id = ?, division = ?, sexe = ?, niveau = ? WHERE nom = ?";
        try (Connection conn = DriverManager.getConnection(URL, LOGIN, PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nouveauNom);
            ps.setInt(2, clubId);
            ps.setString(3, division);
            ps.setString(4, sexe);
            ps.setString(5, niveau);
            ps.setString(6, ancienNom);
            rowsAffected = ps.executeUpdate();
            System.out.println("Équipe modifiée : " + nouveauNom);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return rowsAffected;
    }

    /**
     * Récupère la liste des équipes enregistrées dans la base de données.
     * Réalise une jointure avec la table Club pour obtenir le nom du club.
     * @return Une liste d'objets Equipe
     */
    public List<Equipe> getListeEquipes() {
        List<Equipe> equipes = new ArrayList<>();
        String sql = "SELECT e.nom, c.nom AS club, e.division, e.sexe, e.niveau " +
                     "FROM Equipe e JOIN Club c ON e.club_id = c.id";
        try (Connection conn = DriverManager.getConnection(URL, LOGIN, PASS);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String nom = rs.getString("nom");
                String club = rs.getString("club");
                String division = rs.getString("division");
                String sexe = rs.getString("sexe");
                String niveau = rs.getString("niveau");
                equipes.add(new Equipe(nom, club, division, sexe, niveau));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return equipes;
    }

    /**
     * Supprime une équipe de la base de données à partir de son nom.
     * @param nom Le nom de l'équipe à supprimer
     * @return Le nombre de lignes affectées (normalement 1 en cas de succès)
     */
    public int supprimerEquipe(String nom) {
        int rowsAffected = 0;
        String sql = "DELETE FROM Equipe WHERE nom = ?";
        try (Connection conn = DriverManager.getConnection(URL, LOGIN, PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nom);
            rowsAffected = ps.executeUpdate();
            System.out.println("Équipe supprimée : " + nom);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return rowsAffected;
    }

    /**
     * Récupère l'identifiant d'une équipe à partir de son nom.
     * @param nom Le nom de l'équipe.
     * @return L'identifiant de l'équipe si trouvée, -1 sinon.
     */
    public int getEquipeId(String nom) {
        int id = -1;
        String sql = "SELECT id FROM Equipe WHERE nom = ?";
        try (Connection conn = DriverManager.getConnection(URL, LOGIN, PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {
         
            ps.setString(1, nom);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    id = rs.getInt("id");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return id;
    }
}

/**
 * Represents a football club.
 */
public class Club {
    private int id;
    private String nom;
    private String ville;

    /**
     * Constructor for creating a club.
     * 
     * @param id    The unique identifier of the club.
     * @param nom   The name of the club.
     * @param ville The city where the club is based.
     */
    public Club(int id, String nom, String ville) {
        this.id = id;
        this.nom = nom;
        this.ville = ville;
    }

    /**
     * Gets the club's ID.
     * 
     * @return The unique identifier of the club.
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the club's name.
     * 
     * @return The name of the club.
     */
    public String getNom() {
        return nom;
    }

    /**
     * Gets the city where the club is based.
     * 
     * @return The city of the club.
     */
    public String getVille() {
        return ville;
    }

    /**
     * Returns a string representation of the club.
     * 
     * @return A formatted string containing the club name and its city.
     */
    @Override
    public String toString() {
        return nom + " - " + ville;
    }
}

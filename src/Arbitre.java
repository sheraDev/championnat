/**
 * Represents a referee in the football championship.
 */
public class Arbitre {
    private int id;
    private String nom;

    /**
     * Constructs an Arbitre object with the specified id, name, and category.
     * <p>
     * Note: The 'categorie' parameter is currently not used.
     * </p>
     *
     * @param id        the identifier of the referee
     * @param nom       the name of the referee
     * @param categorie the category of the referee (unused)
     */
    public Arbitre(int id, String nom, String categorie) {
        this.id = id;
        this.nom = nom;
    }

    /**
     * Returns the identifier of the referee.
     *
     * @return the id of the referee
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the name of the referee.
     *
     * @return the name of the referee
     */
    public String getNom() {
        return nom;
    }

    /**
     * Returns a string representation of the referee.
     *
     * @return the referee's name followed by a hyphen and a space
     */
    @Override
    public String toString() {
        return nom + " - " ;
    }
}

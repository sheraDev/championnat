/**
 * Represents a team with a name, club affiliation, division, gender, and level.
 */
public class Equipe {
    private String nom;
    private String club;
    private String division;
    private String sexe;
    private String niveau;

    /**
     * Constructs a new Equipe with the specified details.
     *
     * @param nom the name of the team
     * @param club the club the team belongs to
     * @param division the division of the team
     * @param sexe the gender category of the team
     * @param niveau the level of the team
     */
    public Equipe(String nom, String club, String division, String sexe, String niveau) {
        this.nom = nom;
        this.club = club;
        this.division = division;
        this.sexe = sexe;
    }

    /**
     * Returns the name of the team.
     *
     * @return the team name
     */
    public String getNom() {
        return nom;
    }

    /**
     * Returns the club associated with the team.
     *
     * @return the club name
     */
    public String getClub() {
        return club;
    }

    /**
     * Returns the division in which the team competes.
     *
     * @return the division
     */
    public String getDivision() {
        return division;
    }

    /**
     * Returns the gender category of the team.
     *
     * @return the gender
     */
    public String getSexe() {
        return sexe;
    }

    /**
     * Returns the level of the team.
     *
     * @return the level
     */
    public String getNiveau() {
        return niveau;
    }
}

public class Arbitre {
    private int id;
    private String nom;

    public Arbitre(int id, String nom, String categorie) {
        this.id = id;
        this.nom = nom;
        }

    public int getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    @Override
    public String toString() {
        return nom + " - " ;
    }
}

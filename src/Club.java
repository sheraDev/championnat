public class Club {
    private int id;
    private String nom;
    private String ville;
    
    public Club(int id, String nom, String ville) {
        this.id = id;
        this.nom = nom;
        this.ville = ville;
    }
    
    public int getId() {
        return id;
    }
    
    public String getNom() {
        return nom;
    }
    
    public String getVille() {
        return ville;
    }
    
    @Override
    public String toString() {
        return nom + " - " + ville;
    }
}

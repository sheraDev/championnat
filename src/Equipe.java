public class Equipe {
    private String nom;
    private String club;
    private String division;
    private String sexe;
    private String niveau;

    public Equipe(String nom, String club, String division, String sexe,String niveau) {
        this.nom = nom;
        this.club = club;
        this.division = division;
        this.sexe = sexe;
    }

    public String getNom() {
        return nom;
    }

    public String getClub() {
        return club;
    }

    public String getDivision() {
        return division;
    }

    public String getSexe() {
        return sexe;
    }

    public String getNiveau(){
        return niveau;
    }
}

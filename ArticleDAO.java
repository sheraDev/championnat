import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe d'acc�s aux donn�es contenues dans la table article
 * @version 1.1
 * */
public class ArticleDAO {

	/**
	 * Param�tres de connexion � la base de donn�es oracle
	 * URL, LOGIN et PASS sont des constantes
	 */
	/*final static String URL = "jdbc:oracle:thin:@localhost:1521:xe";
	final static String LOGIN="bdd1";
	final static String PASS="bdd1"; */

	private static final String URL = "jdbc:mariadb://localhost:3306/gestion_articles";
    private static final String LOGIN = "root";
    private static final String PASS = "root";


	/**
	 * Constructeur de la classe
	 * 
	 */
	public ArticleDAO()
	{
		// chargement du pilote de bases de donn�es
		try {
			//Class.forName("oracle.jdbc.OracleDriver");
			Class.forName("org.mariadb.jdbc.Driver");
			
		} catch (ClassNotFoundException e2) {
			System.err.println("Impossible de charger le pilote de BDD, ne pas oublier d'importer le fichier .jar dans le projet");
		}

	}
	

	/**
	 * Permet d'ajouter un article dans la table article
	 * la r�f�rence de l'article est produite automatiquement par la base de donn�es en utilisant une s�quence
	 * Le mode est auto-commit par d�faut : chaque insertion est valid�e
	 * @param nouvArticle l'article � ajouter
	 * @return le nombre de ligne ajout�es dans la table
	 */
	public int ajouter(Article nouvArticle)
	{
		Connection con = null;
		PreparedStatement ps = null;
		int retour=0;

		//connexion � la base de donn�es
		try {

			//tentative de connexion
			con = DriverManager.getConnection(URL, LOGIN, PASS);
			//pr�paration de l'instruction SQL, chaque ? repr�sente une valeur � communiquer dans l'insertion
			//les getters permettent de r�cup�rer les valeurs des attributs souhait�s de nouvArticle
			//ps = con.prepareStatement("INSERT INTO article (art_reference, art_designation, art_pu_ht, art_qte_stock) VALUES (article_seq.NEXTVAL, ?, ?, ?)");
			ps = con.prepareStatement("INSERT INTO article (art_designation, art_pu_ht, art_qte_stock) VALUES (?, ?, ?)");

			ps.setString(1,nouvArticle.getDesignation());
			ps.setDouble(2,nouvArticle.getPuHt());
			ps.setInt(3,nouvArticle.getQteStock());

			//Ex�cution de la requ�te
			retour=ps.executeUpdate();


		} catch (Exception ee) {
			ee.printStackTrace();
		} finally {
			//fermeture du preparedStatement et de la connexion
			try {if (ps != null)ps.close();} catch (Exception t) {}
			try {if (con != null)con.close();} catch (Exception t) {}
		}
		return retour;

	}

	/**
	 * Permet de r�cup�rer un article � partir de sa r�f�rence
	 * @param reference la r�f�rence de l'article � r�cup�rer
	 * @return l'article
	 * @return null si aucun article ne correspond � cette r�f�rence
	 */
	public Article getArticle(int reference)
	{

		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs=null;
		Article retour=null;

		//connexion � la base de donn�es
		try {

			con = DriverManager.getConnection(URL, LOGIN, PASS);
			System.out.println("Connexion réussie à la base de données");

			ps = con.prepareStatement("SELECT * FROM article WHERE art_reference = ?");
			ps.setInt(1,reference);

			//on ex�cute la requ�te
			//rs contient un pointeur situ� jusute avant la premi�re ligne retourn�e
			rs=ps.executeQuery();
			//passe � la premi�re (et unique) ligne retourn�e 
			if(rs.next())
				retour=new Article(rs.getInt("art_reference"),rs.getString("art_designation"),rs.getDouble("art_pu_ht"),rs.getInt("art_qte_stock"));


		} catch (Exception ee) {
			ee.printStackTrace();
			System.out.println("Connexion échouée à la base de données");

			
		} finally {
			//fermeture du ResultSet, du PreparedStatement et de la Connection
			try {if (rs != null)rs.close();} catch (Exception t) {}
			try {if (ps != null)ps.close();} catch (Exception t) {}
			try {if (con != null)con.close();} catch (Exception t) {}
		}
		return retour;

	}

	/**
	 * Permet de r�cup�rer tous les articles stock�s dans la table article
	 * @return une ArrayList d'Articles
	 */
	public List<Article> getListeArticles()
	{

		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs=null;
		List<Article> retour=new ArrayList<Article>();

		//connexion � la base de donn�es
		try {

			con = DriverManager.getConnection(URL, LOGIN, PASS);
			ps = con.prepareStatement("SELECT * FROM article");

			//on ex�cute la requ�te
			rs=ps.executeQuery();
			//on parcourt les lignes du r�sultat
			while(rs.next())
				retour.add(new Article(rs.getInt("art_reference"),rs.getString("art_designation"),rs.getDouble("art_pu_ht"),rs.getInt("art_qte_stock")));


		} catch (Exception ee) {
			ee.printStackTrace();
		} finally {
			//fermeture du rs, du preparedStatement et de la connexion
			try {if (rs != null)rs.close();} catch (Exception t) {}
			try {if (ps != null)ps.close();} catch (Exception t) {}
			try {if (con != null)con.close();} catch (Exception t) {}
		}
		return retour;

	}

	//main permettant de tester la classe
	public static void main(String[] args)  throws SQLException {

		 ArticleDAO articleDAO=new ArticleDAO();
		//test de la m�thode ajouter
		Article a = new Article("Set de 2 raquettes de ping-pong",149.9,10);
		int retour=articleDAO.ajouter(a);

		System.out.println(retour+ " lignes ajout�es");

		//test de la m�thode getArticle
		Article a2 = articleDAO.getArticle(1);
		System.out.println(a2);

		 //test de la m�thode getListeArticles
		List<Article> liste=articleDAO.getListeArticles();
		//System.out.println(liste);
		for(Article art : liste)
		{
			System.out.println(art.toString());
		}

	}
}

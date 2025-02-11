import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class DatabaseConfig {
    private static final String CONFIG_FILE = "config.properties";
    private static Properties properties = new Properties();

    static {
        try {
            // Chercher config.properties dans le même dossier que le JAR
            File configFile = new File(CONFIG_FILE);
            if (!configFile.exists()) {
                System.err.println("Erreur : Fichier " + CONFIG_FILE + " introuvable !");
                System.exit(1); // Arrêter le programme
            }

            try (FileInputStream fis = new FileInputStream(configFile)) {
                properties.load(fis);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement du fichier de configuration.");
            System.exit(1);
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}

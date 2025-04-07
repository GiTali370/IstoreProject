package database;

    public class QueryUtils {

        // Utilisateurs

        // Requête pour insérer un utilisateur
        public static final String INSERT_USER = "INSERT INTO utilisateurs (email, password, pseudo, role) VALUES (?, ?, ?, ?)";

        // Requête pour récupérer tous les utilisateurs
        public static final String SELECT_ALL_USERS = "SELECT id, email, pseudo, role FROM utilisateurs";

        // Magasins

        // Requête pour insérer un magasin
        public static final String INSERT_STORE = "INSERT INTO store (nom) VALUES (?)";

        // Articles

        // Requête pour insérer un article
        public static final String INSERT_ARTICLE = "INSERT INTO item (nom, prix, quantite, magasin_id) VALUES (?, ?, ?, ?)";
    }


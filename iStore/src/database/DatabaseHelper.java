package database;

import model.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;

public class DatabaseHelper {
    // Informations de connexion MySQL
    private static final String DB_URL = "jdbc:mysql://localhost:3306/istore"; // Adresse de la base
    private static final String DB_USER = "root"; // Nom d'utilisateur MySQL
    private static final String DB_PASSWORD = "Password098890"; // Mot de passe MySQL

    // OBbtenir une connexion
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new SQLException("Le pilote MySQL n'a pas été trouvé !");
        }
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    // Initialisé la base de données (création des tables)
    public static void initializeDatabase() {
        try (Connection connection = getConnection()) {
            var statement = connection.createStatement();

            // Création de la tableutilisateurs
            String createUsersTable = """
                CREATE TABLE IF NOT EXISTS utilisateurs (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    email VARCHAR(255) UNIQUE NOT NULL,
                    password VARCHAR(255) NOT NULL,
                    pseudo VARCHAR(255) NOT NULL,
                    role VARCHAR(50) NOT NULL
                );
            """;
            statement.execute(createUsersTable);

            // Création de la table magasins
            String createStoresTable = """
                CREATE TABLE IF NOT EXISTS magasins (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    nom VARCHAR(255) UNIQUE NOT NULL
                );
            """;
            statement.execute(createStoresTable);

            // Création de la table article
            String createArticlesTable = """
                CREATE TABLE IF NOT EXISTS articles (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    nom VARCHAR(255) NOT NULL,
                    prix DOUBLE NOT NULL,
                    quantite INT NOT NULL,
                    magasin_id INT NOT NULL,
                    FOREIGN KEY (magasin_id) REFERENCES magasins(id)
                );
            """;
            statement.execute(createArticlesTable);
            System.out.println("Base de données initialisée avec succès (MySQL) !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Vérifier si un email existe déjà dans la base
    public static boolean emailExists(String email) {
        String query = "SELECT COUNT(*) FROM utilisateurs WHERE email = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // Retourne true si l'email existe
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Insérer un utilisateur dans la base
    public static boolean insertUser(String email, String hashedPassword, String pseudo, String role) {
        // Vérifier si l'email existe déjà
        if (emailExists(email)) {
            System.out.println("Erreur : cet email est déjà utilisé.");
            return false;
        }

        // Vérifier si l'email est dans la liste blanche
        if (!isEmailWhitelisted(email)) {
            System.out.println("Erreur : cet email n'est pas dans la liste blanche.");
            return false;
        }

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO utilisateurs (email, password, pseudo, role) VALUES (?, ?, ?, ?)")) {

            stmt.setString(1, email);
            stmt.setString(2, hashedPassword);
            stmt.setString(3, pseudo);
            stmt.setString(4, role);

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Utilisateur inséré avec succès : " + email);
                return true;  // Retourne true si l'insertion réussit
            } else {
                System.out.println("Échec de l'insertion de l'utilisateur.");
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Erreur SQL lors de l'insertion de l'utilisateur : " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    public static List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT id, email, pseudo, role FROM utilisateurs";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String email = resultSet.getString("email");
                String pseudo = resultSet.getString("pseudo");
                String role = resultSet.getString("role");

                // Crée un objet User et l'ajoute à la liste
                users.add(new User(id, email, pseudo, role));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users; // Retourne la liste des utilisateurs
    }
    public static void deleteUser(int userId) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM utilisateurs WHERE id = ?")) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Vérifier si unemail est dans la liste blanche
        public static boolean isEmailWhitelisted(String email) {
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement("SELECT email FROM whitelisted_emails WHERE email = ?")) {
                stmt.setString(1, email);
                ResultSet rs = stmt.executeQuery();
                return rs.next(); // Retourne true si l'email est trouvé
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        }

    public static boolean updateUserPseudo(int userId, String newPseudo) {
        String query = "UPDATE utilisateurs SET pseudo = ? WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, newPseudo);
            stmt.setInt(2, userId);

            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0; // Retourne true si la mise à jour a été effectuée

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public static boolean updateUserRole(int userId, String newRole) {
        String query = "UPDATE utilisateurs SET role = ? WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, newRole);
            stmt.setInt(2, userId);

            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getUserRoleByEmail(String email) {
        String role = null;
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT role FROM utilisateurs WHERE email = ?")) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                role = rs.getString("role");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return role;
    }

    public static void insertBoutique(String nom) {
        String query = "INSERT INTO boutiques (nom) VALUES (?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, nom);
            stmt.executeUpdate();
            System.out.println("Boutique créée avec succès : " + nom);
        } catch (SQLException e) {
            System.out.println("Erreur lors de la création de la boutique : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void insertItem(String nom, double prix, int quantite, int boutiqueId) {
        String query = "INSERT INTO inventaire (nom, prix, quantite, boutique_id) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, nom);
            stmt.setDouble(2, prix);
            stmt.setInt(3, quantite);
            stmt.setInt(4, boutiqueId);

            stmt.executeUpdate();
            System.out.println("Article ajouté avec succès : " + nom);
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout de l'article : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void deleteItem(int itemId) {
        String query = "DELETE FROM inventaire WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, itemId);
            int rowsDeleted = stmt.executeUpdate();

            if (rowsDeleted > 0) {
                System.out.println("Article supprimé avec succès : ID " + itemId);
            } else {
                System.out.println("Aucun article trouvé avec cet ID.");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression de l'article : " + e.getMessage());
            e.printStackTrace();
        }
    }
    // Récupéré les articles Dun magasin spécifique
    public static List<Object[]> getItemsByStore(int storeId) {
        List<Object[]> items = new ArrayList<>();
        String query = "SELECT i.id, i.nom, i.prix, i.quantite, b.nom AS boutique_nom " +
                "FROM inventaire i " +
                "JOIN boutiques b ON i.boutique_id = b.id " +
                "WHERE i.boutique_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, storeId); // Définir l'identifiant du magasin
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String nom = rs.getString("nom");
                double prix = rs.getDouble("prix");
                int quantite = rs.getInt("quantite");
                String boutiqueNom = rs.getString("boutique_nom");

                items.add(new Object[]{id, nom, prix, quantite, boutiqueNom});
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des articles pour le magasin : " + e.getMessage());
            e.printStackTrace();
        }

        return items;
    }

    public static boolean updateItemStock(int itemId, int newStock) {
        String query = "UPDATE inventaire SET stock = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, newStock);
            stmt.setInt(2, itemId);

            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0; // Retourne true si au moins une ligne est mise à jour
        } catch (SQLException e) {
            System.out.println("Erreur lors de la mise à jour du stock : " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Récupérer tous les magasins
    public static List<Object[]> getAllStores() {
        List<Object[]> stores = new ArrayList<>();
        String query = "SELECT id, nom FROM boutiques";  // Assurez-vous que la table s'appelle "boutiques"
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                stores.add(new Object[]{rs.getInt("id"), rs.getString("nom")});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stores;
    }

    // Insérer un magasin
    public static void insertStore(String storeName) {
        String query = "INSERT INTO boutiques (nom) VALUES (?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, storeName);
            stmt.executeUpdate();
            System.out.println("Magasin ajouté avec succès : " + storeName);
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout du magasin : " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Supprimer un magasin
    public static void deleteStore(int storeId) {
        String query = "DELETE FROM boutiques WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, storeId);
            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Magasin supprimé avec succès (ID: " + storeId + ")");
            } else {
                System.out.println("Aucun magasin trouvé avec cet ID.");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression du magasin : " + e.getMessage());
            e.printStackTrace();
        }
    }


}

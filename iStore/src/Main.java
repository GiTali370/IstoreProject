import model.User;
import model.Store;
import model.Item;
import database.DatabaseHelper;
import view.LoginView;


public class Main {
    public static void main(String[] args) {

        // Initialiser la base de données
        DatabaseHelper.initializeDatabase();

        // Ajouter un utilisateur
        DatabaseHelper.insertUser("admin@istore.com", "Password123", "Admin", "admin");

        // Lire les utilisateur
        DatabaseHelper.getAllUsers();

        // Lancer linterface graphique Swing
        javax.swing.SwingUtilities.invokeLater(() -> new LoginView().setVisible(true));





    }
}





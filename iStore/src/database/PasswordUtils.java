package database;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtils {



    public static String hashPassword(String Password) {
        // La méthode gensalt() génère un sel aléatoire (tu peux ajuster la complexité si besoin)
        return BCrypt.hashpw(Password, BCrypt.gensalt());
    }





    public static boolean checkPassword(String Password, String hashedPassword) {
        return BCrypt.checkpw(Password, hashedPassword);
    }
}
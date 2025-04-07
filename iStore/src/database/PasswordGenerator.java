package database;

import database.PasswordUtils;

public class PasswordGenerator {
    public static void main(String[] args) {
        // Mot de passe clair
        String plainPassword = "Password123";

        // Motde passe haché
        String hashedPassword = PasswordUtils.hashPassword(plainPassword);

        // Afficher le mot de passe haché
        System.out.println("Mot de passe haché : " + hashedPassword);
    }
}
package controller;

import database.DatabaseHelper;
import database.PasswordUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserAuthController {
    public static boolean authenticateUser(String email, String password) {
        try (Connection conn = DatabaseHelper.getConnection()) {
            String query = "SELECT password FROM utilisateurs WHERE email = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String hashedPassword = rs.getString("password");
                return PasswordUtils.checkPassword(password, hashedPassword);
            } else {
                System.out.println("Aucun utilisateur trouvé avec cet email.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }
}
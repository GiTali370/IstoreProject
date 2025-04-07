package view;

import database.DatabaseHelper;
import database.PasswordUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginView extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton, registerButton;

    public LoginView() {
        setTitle("Connexion - iStore");
        setSize(800, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10)); // Espacement global

        // Titre de la fenetre
        JLabel titleLabel = new JLabel("Connexion à iStore");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        // Panneau central
        JPanel centerPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Marges internes

        JLabel emailLabel = new JLabel("Email:");
        emailField = new JTextField();

        JLabel passwordLabel = new JLabel("Mot de passe:");
        passwordField = new JPasswordField();

        centerPanel.add(emailLabel);
        centerPanel.add(emailField);
        centerPanel.add(passwordLabel);
        centerPanel.add(passwordField);

        add(centerPanel, BorderLayout.CENTER);

        // Panneau de boutons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20)); // Marges internes

        loginButton = new JButton("Se connecter");
        loginButton.addActionListener(new LoginAction());

        registerButton = new JButton("S'inscrire");
        registerButton.addActionListener(e -> {
            new RegisterView().setVisible(true);
            dispose(); // Fermer la fenêtre actuelle
        });

        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    // Vérification des identifiants
    private class LoginAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            // Vérification des champs vides
            if (email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(LoginView.this, "Veuillez remplir tous les champs.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Authentification
            String authResult = authenticateUser(email, password);
            if (authResult.equals("success")) {
                JOptionPane.showMessageDialog(LoginView.this, "Connexion réussie !");
                new MainView(email, DatabaseHelper.getUserRoleByEmail(email)).setVisible(true); // Charge la MainView avec email et rôle
                dispose();
            } else {
                JOptionPane.showMessageDialog(LoginView.this, authResult, "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Authentifier un utilisateur
    private String authenticateUser(String email, String password) {
        try (Connection conn = DatabaseHelper.getConnection()) {
            String query = "SELECT password FROM utilisateurs WHERE email = ?";

            // Préparer la requête
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            // Vérifier si l'utilisateur existe
            if (!rs.next()) {
                return "Aucun compte associé à cet email. Veuillez vous inscrire.";
            }

            // Récupérer le mot de passe haché
            String hashedPassword = rs.getString("password");

            // Vérifier si le mot de passe saisi correspond
            if (!PasswordUtils.checkPassword(password, hashedPassword)) {
                return "Mot de passe incorrect. Veuillez réessayer.";
            }

            // Authentification réussie
            return "success";

        } catch (Exception ex) {
            ex.printStackTrace();
            return "Erreur de connexion au serveur. Veuillez réessayer plus tard.";
        }
    }
}

package view;

import database.DatabaseHelper;
import database.PasswordUtils;

import javax.swing.*;
import java.awt.*;

public class RegisterView extends JFrame {
    private JTextField emailField;
    private JTextField pseudoField;
    private JPasswordField passwordField;
    private JComboBox<String> roleDropdown;
    private JButton registerButton;

    public RegisterView() {
        setTitle("S'inscrire - iStore");
        setSize(800, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10)); // Espacement global

        // Titre
        JLabel titleLabel = new JLabel("Créer un compte");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        // Panneau central pour les champs
        JPanel centerPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Marges internes

        JLabel emailLabel = new JLabel("Email:");
        emailField = new JTextField();

        JLabel pseudoLabel = new JLabel("Pseudo:");
        pseudoField = new JTextField();

        JLabel passwordLabel = new JLabel("Mot de passe:");
        passwordField = new JPasswordField();

        JLabel roleLabel = new JLabel("Rôle:");
        roleDropdown = new JComboBox<>(new String[]{"employee", "admin"});

        centerPanel.add(emailLabel);
        centerPanel.add(emailField);
        centerPanel.add(pseudoLabel);
        centerPanel.add(pseudoField);
        centerPanel.add(passwordLabel);
        centerPanel.add(passwordField);
        centerPanel.add(roleLabel);
        centerPanel.add(roleDropdown);

        add(centerPanel, BorderLayout.CENTER);

        // Panneau pour le bouton
        JPanel buttonPanel = new JPanel();
        registerButton = new JButton("S'inscrire");
        registerButton.addActionListener(e -> registerUser());
        buttonPanel.add(registerButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    // Enregistrer un utilisateur
    private void registerUser() {
        String email = emailField.getText().trim();
        String pseudo = pseudoField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String role = (String) roleDropdown.getSelectedItem();

        if (email.isEmpty() || pseudo.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tous les champs doivent être remplis.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!email.contains("@")) {
            JOptionPane.showMessageDialog(this, "Veuillez entrer une adresse email valide.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Vérification si l'email est sur liste blanche
        if (!DatabaseHelper.isEmailWhitelisted(email)) {
            JOptionPane.showMessageDialog(this, "Cet email n'est pas autorisé à s'inscrire.", "Accès refusé", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String hashedPassword = PasswordUtils.hashPassword(password);

        if (DatabaseHelper.insertUser(email, hashedPassword, pseudo, role)) {
            JOptionPane.showMessageDialog(this, "Inscription réussie !");
            new LoginView().setVisible(true); // Retourne à la page de connexion
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Erreur lors de l'inscription. L'email existe peut-être déjà.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}
package view;

import database.DatabaseHelper;
import database.PasswordUtils;
import model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class UserManagementView extends JFrame {
    private JTable userTable;
    private DefaultTableModel tableModel;
    private String currentUserEmail;
    private String currentUserRole;

    public UserManagementView(String currentUserEmail, String currentUserRole) {
        this.currentUserEmail = currentUserEmail;
        this.currentUserRole = currentUserRole;

        // Configuration de la fenetre
        setTitle("Gestion des utilisateurs");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Titre de la fenetre
        JLabel titleLabel = new JLabel("Gestion des utilisateurs");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        // Configuration de la table
        String[] columnNames = {"ID", "Email", "Pseudo", "Rôle"};
        tableModel = new DefaultTableModel(columnNames, 0);
        userTable = new JTable(tableModel);
        userTable.setRowHeight(25);
        userTable.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane tableScrollPane = new JScrollPane(userTable);
        add(tableScrollPane, BorderLayout.CENTER);

        // Panneau de bouton
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 4, 10, 10)); // Grille avec espacement
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Marges internes

        // Bouton Ajouter (admin uniquement)
        if (currentUserRole.equalsIgnoreCase("admin")) {
            JButton addButton = new JButton("Ajouter");
            addButton.setFont(new Font("Arial", Font.PLAIN, 16));
            addButton.addActionListener(e -> addUser());
            buttonPanel.add(addButton);
        }

        // Bouton Modifier (admin uniquement)
        if (currentUserRole.equalsIgnoreCase("admin")) {
            JButton editButton = new JButton("Modifier");
            editButton.setFont(new Font("Arial", Font.PLAIN, 16));
            editButton.addActionListener(e -> editUser());
            buttonPanel.add(editButton);
        }

        // Bouton Supprimer (admin uniquement)
        if (currentUserRole.equalsIgnoreCase("admin")) {
            JButton deleteButton = new JButton("Supprimer");
            deleteButton.setFont(new Font("Arial", Font.PLAIN, 16));
            deleteButton.addActionListener(e -> deleteUser());
            buttonPanel.add(deleteButton);
        }

        // Bouton Rafraîchir
        JButton refreshButton = new JButton("Rafraîchir");
        refreshButton.setFont(new Font("Arial", Font.PLAIN, 16));
        refreshButton.addActionListener(e -> loadUsers());
        buttonPanel.add(refreshButton);

        // Ajouter les composants
        add(buttonPanel, BorderLayout.SOUTH);

        // Charger les utilisateurs
        loadUsers();
    }

    // Changer les utilisateurs depuis la base de donnée
    private void loadUsers() {
        tableModel.setRowCount(0); // Effacer les anciennes données

        List<User> users = DatabaseHelper.getAllUsers();
        for (User user : users) {
            tableModel.addRow(new Object[]{user.getId(), user.getEmail(), user.getPseudo(), user.getRole()});
        }
    }

    // Ajouter un utilisateur
    private void addUser() {
        JTextField emailField = new JTextField();
        JTextField pseudoField = new JTextField();
        JComboBox<String> roleDropdown = new JComboBox<>(new String[]{"admin", "employee"});
        JPasswordField passwordField = new JPasswordField();

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10)); // Grille avec espacement
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Marges internes
        panel.add(new JLabel("Email :"));
        panel.add(emailField);
        panel.add(new JLabel("Pseudo :"));
        panel.add(pseudoField);
        panel.add(new JLabel("Rôle :"));
        panel.add(roleDropdown);
        panel.add(new JLabel("Mot de passe :"));
        panel.add(passwordField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Ajouter un utilisateur", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String email = emailField.getText().trim();
            String pseudo = pseudoField.getText().trim();
            String role = (String) roleDropdown.getSelectedItem();
            String password = new String(passwordField.getPassword());

            if (!email.isEmpty() && !pseudo.isEmpty() && !password.isEmpty()) {
                String hashedPassword = PasswordUtils.hashPassword(password);
                DatabaseHelper.insertUser(email, hashedPassword, pseudo, role);
                loadUsers();
            } else {
                JOptionPane.showMessageDialog(this, "Tous les champs doivent être remplis.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Modifier un utilisateur
    private void editUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un utilisateur à modifier.");
            return;
        }

        int userId = (int) tableModel.getValueAt(selectedRow, 0);
        String currentPseudo = (String) tableModel.getValueAt(selectedRow, 2);
        String currentRole = (String) tableModel.getValueAt(selectedRow, 3);

        JTextField pseudoField = new JTextField(currentPseudo);
        JComboBox<String> roleDropdown = new JComboBox<>(new String[]{"admin", "employee"});
        roleDropdown.setSelectedItem(currentRole);

        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10)); // Grille avec espacement
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Marges internes
        panel.add(new JLabel("Nouveau pseudo :"));
        panel.add(pseudoField);
        panel.add(new JLabel("Nouveau rôle :"));
        panel.add(roleDropdown);

        int result = JOptionPane.showConfirmDialog(this, panel, "Modifier utilisateur", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String newPseudo = pseudoField.getText().trim();
            String newRole = (String) roleDropdown.getSelectedItem();

            if (!newPseudo.isEmpty()) {
                DatabaseHelper.updateUserPseudo(userId, newPseudo);
            }

            DatabaseHelper.updateUserRole(userId, newRole);
            loadUsers();
        }
    }

    // Supprimer un utilisateur
    private void deleteUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un utilisateur à supprimer.");
            return;
        }

        int userId = (int) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Etes-vous sûr de vouloir supprimer cet utilisateur?", "Confirmation", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            DatabaseHelper.deleteUser(userId);
            loadUsers();
        }
    }
}
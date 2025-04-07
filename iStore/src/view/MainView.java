package view;

import database.DatabaseHelper;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MainView extends JFrame {
    private String currentUserEmail;
    private String currentUserRole;

    public MainView(String email, String role) {
        this.currentUserEmail = email;
        this.currentUserRole = role;

        setTitle("Tableau de Bord - iStore");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Message de bienvenue
        JLabel welcomeLabel = new JLabel("Bienvenue sur iStore !");
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(welcomeLabel, BorderLayout.CENTER);

        // Panneau de bouton
        JPanel buttonPanel = new JPanel();

        // Bouton pour gérer les utilisateurs
        JButton manageUsersButton = new JButton("Gérer les utilisateurs");
        manageUsersButton.addActionListener(e -> new UserManagementView(currentUserEmail, currentUserRole).setVisible(true));
        buttonPanel.add(manageUsersButton);

        // Bouton pour gérer l'inventaire (choix du magasin)
        JButton manageInventoryButton = new JButton("Gérer l'Inventaire");
        manageInventoryButton.addActionListener(e -> selectStoreForInventory(currentUserRole));
        buttonPanel.add(manageInventoryButton);

        // Bouton pour la gestion des magasins (uniquement pour l'administrateur)
        if (currentUserRole.equals("admin")) {
            JButton manageStoresButton = new JButton("Gérer les Magasins");
            manageStoresButton.addActionListener(e -> new StoreManagementView(currentUserRole).setVisible(true));
            buttonPanel.add(manageStoresButton);
        }

        // Ajouter les bouton à la fenetre
        add(buttonPanel, BorderLayout.SOUTH);
    }

    //  Méthode pour sélectionner un magasin avant d'ouvrir l'inventaire
    private void selectStoreForInventory(String currentUserRole) {
        List<Object[]> stores = DatabaseHelper.getAllStores();

        if (stores.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Aucun magasin disponible.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String[] storeNames = stores.stream()
                .map(store -> store[1].toString())
                .toArray(String[]::new);

        String selectedStore = (String) JOptionPane.showInputDialog(
                this,
                "Sélectionnez un magasin :",
                "Choisir un Magasin",
                JOptionPane.PLAIN_MESSAGE,
                null,
                storeNames,
                storeNames[0]);

        if (selectedStore != null) {
            // Récupérer l'ID du magasin sélectionné
            int storeId = stores.stream()
                    .filter(store -> store[1].equals(selectedStore))
                    .mapToInt(store -> (int) store[0])
                    .findFirst()
                    .orElse(-1);

            if (storeId != -1) {
                new InventoryManagementView(currentUserRole, storeId).setVisible(true);
            }
        }
    }
}
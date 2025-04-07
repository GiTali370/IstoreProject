package view;

import database.DatabaseHelper;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class InventoryManagementView extends JFrame {
    private JTable inventoryTable;
    private DefaultTableModel tableModel;
    private int storeId; // ID du magasin pour lequel l'inventaire est géré

    public InventoryManagementView(String currentUserRole, int storeId) {
        this.storeId = storeId;

        // Configuration de la fenetre
        setTitle("Gestion de l'Inventaire");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10)); // Ajout d'espacement global

        // Titre de la fenêtre
        JLabel titleLabel = new JLabel("Gestion de l'Inventaire");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        // Configuration de la table
        String[] columnNames = {"ID", "Nom", "Prix", "Quantité"};
        tableModel = new DefaultTableModel(columnNames, 0);
        inventoryTable = new JTable(tableModel);
        inventoryTable.setRowHeight(25);
        inventoryTable.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane tableScrollPane = new JScrollPane(inventoryTable);
        add(tableScrollPane, BorderLayout.CENTER);

        // Panneau de boutons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 4, 10, 10)); // Grille avec espacement
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Marges internes

        // Bouton Ajouter
        JButton addButton = new JButton("Ajouter");
        addButton.setFont(new Font("Arial", Font.PLAIN, 16));
        addButton.addActionListener(e -> addItem());
        buttonPanel.add(addButton);

        // Bouton Supprimer
        JButton deleteButton = new JButton("Supprimer");
        deleteButton.setFont(new Font("Arial", Font.PLAIN, 16));
        deleteButton.addActionListener(e -> deleteItem());
        buttonPanel.add(deleteButton);

        // Bouton Vendre
        JButton sellButton = new JButton("Vendre");
        sellButton.setFont(new Font("Arial", Font.PLAIN, 16));
        sellButton.addActionListener(e -> updateStock(false));
        buttonPanel.add(sellButton);

        // Bouton Recevoir
        JButton receiveButton = new JButton("Recevoir");
        receiveButton.setFont(new Font("Arial", Font.PLAIN, 16));
        receiveButton.addActionListener(e -> updateStock(true));
        buttonPanel.add(receiveButton);

        // Bouton Rafraîchir
        JButton refreshButton = new JButton("Rafraîchir");
        refreshButton.setFont(new Font("Arial", Font.PLAIN, 16));
        refreshButton.addActionListener(e -> loadInventory());
        buttonPanel.add(refreshButton);

        // Ajouter le panneau de boutons
        add(buttonPanel, BorderLayout.SOUTH);

        // Charger l'inventaire
        loadInventory();
    }

    // Méthode pour charger les articles d'un magasin
    private void loadInventory() {
        tableModel.setRowCount(0); // Effacer les anciennes données

        List<Object[]> items = DatabaseHelper.getItemsByStore(storeId); // Obtenir les articles du magasin
        for (Object[] item : items) {
            tableModel.addRow(item);
        }
    }

    // Ajouter un article
    private void addItem() {
        JTextField nameField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField quantityField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10)); // Grille avec espacement
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Marges internes
        panel.add(new JLabel("Nom :"));
        panel.add(nameField);
        panel.add(new JLabel("Prix :"));
        panel.add(priceField);
        panel.add(new JLabel("Quantité :"));
        panel.add(quantityField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Ajouter un article", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText().trim();
                double price = Double.parseDouble(priceField.getText().trim());
                int quantity = Integer.parseInt(quantityField.getText().trim());

                if (!name.isEmpty() && price > 0 && quantity >= 0) {
                    DatabaseHelper.insertItem(name, price, quantity, storeId);
                    loadInventory();
                } else {
                    JOptionPane.showMessageDialog(this, "Tous les champs doivent être remplis correctement.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Veuillez saisir des valeurs valides.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Supprimer un article
    private void deleteItem() {
        int selectedRow = inventoryTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un article à supprimer.");
            return;
        }

        int itemId = (int) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Êtes-vous sûr de vouloir supprimer cet article ?", "Confirmation", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            DatabaseHelper.deleteItem(itemId);
            loadInventory();
        }
    }

    // Mettre à jour le stock
    private void updateStock(boolean isReceiving) {
        int selectedRow = inventoryTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un article.");
            return;
        }

        int itemId = (int) tableModel.getValueAt(selectedRow, 0);
        String itemName = (String) tableModel.getValueAt(selectedRow, 1);
        int currentQuantity = (int) tableModel.getValueAt(selectedRow, 3);

        String input = JOptionPane.showInputDialog(
                this,
                (isReceiving ? "Quantité reçue pour " : "Quantité vendue pour ") + itemName + ":",
                "Mise à jour du stock",
                JOptionPane.PLAIN_MESSAGE
        );

        if (input != null && !input.trim().isEmpty()) {
            try {
                int quantity = Integer.parseInt(input.trim());
                if (quantity <= 0) {
                    JOptionPane.showMessageDialog(this, "Veuillez saisir une quantité positive.", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int newQuantity = isReceiving ? currentQuantity + quantity : currentQuantity - quantity;
                if (newQuantity < 0) {
                    JOptionPane.showMessageDialog(this, "Le stock ne peut pas être négatif.", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                DatabaseHelper.updateItemStock(itemId, newQuantity);
                tableModel.setValueAt(newQuantity, selectedRow, 3); // Mettre à jour directement dans le tableau
                JOptionPane.showMessageDialog(this, "Stock mis à jour avec succès !");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Veuillez saisir un nombre valide.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

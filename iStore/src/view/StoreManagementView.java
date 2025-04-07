package view;

import database.DatabaseHelper;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StoreManagementView extends JFrame {
    private JTable storeTable;
    private DefaultTableModel tableModel;

    public StoreManagementView(String currentUserRole) {
        setTitle("Gestion des Magasins");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Configurer le tableau des magasins
        String[] columnNames = {"ID", "Nom"};
        tableModel = new DefaultTableModel(columnNames, 0);
        storeTable = new JTable(tableModel);

        // Panneau de boutons
        JPanel buttonPanel = new JPanel();

        //  Si l'utilisateur est un administrateur il peut gérer les magasins
        if (currentUserRole.equals("admin")) {
            JButton addButton = new JButton("Créer Magasin");
            addButton.addActionListener(e -> addStore());
            buttonPanel.add(addButton);

            JButton deleteButton = new JButton("Supprimer Magasin");
            deleteButton.addActionListener(e -> deleteStore());
            buttonPanel.add(deleteButton);
        }

        //  Bouton pour actulaiser la liste des magasins
        JButton refreshButton = new JButton("Rafraîchir");
        refreshButton.addActionListener(e -> loadStores());
        buttonPanel.add(refreshButton);

        // Bouton pour fermer la fenetre
        JButton closeButton = new JButton("Fermer");
        closeButton.addActionListener(e -> dispose());
        buttonPanel.add(closeButton);

        // Ajouter les composants
        add(new JScrollPane(storeTable), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Charger les magasins
        loadStores();
    }

    // Charger la liste des magasins depuis la base de données
    private void loadStores() {
        tableModel.setRowCount(0); // Effacer les anciennes données
        List<Object[]> stores = DatabaseHelper.getAllStores();
        for (Object[] store : stores) {
            tableModel.addRow(store);
        }
    }

    // Ajouter un magasin
    private void addStore() {
        String storeName = JOptionPane.showInputDialog(this, "Nom du magasin :", "Créer un Magasin", JOptionPane.PLAIN_MESSAGE);
        if (storeName != null && !storeName.trim().isEmpty()) {
            DatabaseHelper.insertStore(storeName.trim());
            loadStores();
        } else {
            JOptionPane.showMessageDialog(this, "Le nom du magasin ne peut pas être vide.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Supprimer un magasin
    private void deleteStore() {
        int selectedRow = storeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un magasin à supprimer.");
            return;
        }

        int storeId = (int) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Êtes-vous sûr de vouloir supprimer ce magasin ?", "Confirmation", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            DatabaseHelper.deleteStore(storeId);
            loadStores();
        }
    }
}


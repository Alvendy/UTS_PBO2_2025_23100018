package com.mycompany.mavenproject3;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class ProductForm extends JFrame {
    private JTable drinkTable;
    private DefaultTableModel tableModel;
    private JTextField codeField, nameField, priceField, stockField, searchField;
    private JComboBox<String> categoryField;
    private JButton addButton, editButton, deleteButton, searchButton;
    private List<Product> products;
    private Mavenproject3 parent;

    public ProductForm(List<Product> products, Mavenproject3 parent) {
        this.products = products;
        this.parent = parent;

        setTitle("WK. Cuan | Stok Barang");
        setSize(700, 500);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Panel Pencarian Produk
        JPanel searchPanel = new JPanel();
        searchField = new JTextField(15);
        searchButton = new JButton("Cari");
        searchPanel.add(new JLabel("Cari Produk:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        add(searchPanel, BorderLayout.NORTH);

        // Panel Form Produk
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        formPanel.add(new JLabel("Kode Barang:"));
        codeField = new JTextField();
        formPanel.add(codeField);

        formPanel.add(new JLabel("Nama Barang:"));
        nameField = new JTextField();
        formPanel.add(nameField);

        formPanel.add(new JLabel("Kategori:"));
        categoryField = new JComboBox<>(new String[]{"Coffee", "Dairy", "Juice", "Soda", "Tea"});
        formPanel.add(categoryField);

        formPanel.add(new JLabel("Harga Jual:"));
        priceField = new JTextField();
        formPanel.add(priceField);

        formPanel.add(new JLabel("Stok Tersedia:"));
        stockField = new JTextField();
        formPanel.add(stockField);

        JPanel buttonPanel = new JPanel();
        addButton = new JButton("Tambah");
        editButton = new JButton("Edit");
        deleteButton = new JButton("Hapus");
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        formPanel.add(new JLabel());
        formPanel.add(buttonPanel);

        add(formPanel, BorderLayout.SOUTH);

        tableModel = new DefaultTableModel(new String[]{"Kode", "Nama", "Kategori", "Harga", "Stok"}, 0);
        drinkTable = new JTable(tableModel);
        loadProductData();
        add(new JScrollPane(drinkTable), BorderLayout.CENTER);

        // Event Handling
        addButton.addActionListener(e -> tambahProduk());
        editButton.addActionListener(e -> editProduk());
        deleteButton.addActionListener(e -> hapusProduk());
        searchButton.addActionListener(e -> cariProduk());
        drinkTable.getSelectionModel().addListSelectionListener(e -> isiFormDariTabel());
    }

    private void loadProductData() {
        tableModel.setRowCount(0);
        for (Product p : products) {
            tableModel.addRow(new Object[]{p.getCode(), p.getName(), p.getCategory(), p.getPrice(), p.getStock()});
        }
    }

    private void tambahProduk() {
        try {
            double price = Double.parseDouble(priceField.getText());
            int stock = Integer.parseInt(stockField.getText());

            if (price < 0 || stock < 0) {
                JOptionPane.showMessageDialog(this, "Harga dan stok tidak boleh negatif.");
                return;
            }

            Product p = new Product(
                products.size() + 1,
                codeField.getText(),
                nameField.getText(),
                (String) categoryField.getSelectedItem(),
                price,
                stock
            );
            products.add(p);
            refreshTable();
            clearForm();
            parent.refreshBanner(); // Refresh banner dengan data terbaru!
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Harga dan stok harus berupa angka.");
        }
    }

    private void editProduk() {
        int selectedRow = drinkTable.getSelectedRow();
        if (selectedRow >= 0) {
            Product p = products.get(selectedRow);
            p.setCode(codeField.getText());
            p.setName(nameField.getText());
            p.setCategory((String) categoryField.getSelectedItem());

            try {
                double price = Double.parseDouble(priceField.getText());
                int stock = Integer.parseInt(stockField.getText());
                if (price < 0 || stock < 0) {
                    JOptionPane.showMessageDialog(this, "Harga dan stok tidak boleh negatif.");
                    return;
                }
                p.setPrice(price);
                p.setStock(stock);
                refreshTable();
                clearForm();
                parent.refreshBanner(); // Refresh banner
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Harga dan stok harus berupa angka.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Pilih produk yang ingin diedit.");
        }
    }

    private void hapusProduk() {
        int selectedRow = drinkTable.getSelectedRow();
        if (selectedRow >= 0) {
            products.remove(selectedRow);
            refreshTable();
            clearForm();
            parent.refreshBanner(); // Refresh banner
        } else {
            JOptionPane.showMessageDialog(this, "Pilih produk yang ingin dihapus.");
        }
    }

    private void cariProduk() {
        String keyword = searchField.getText().toLowerCase();
        List<Product> filteredProducts = products.stream()
                .filter(p -> p.getName().toLowerCase().contains(keyword))
                .collect(Collectors.toList());

        tableModel.setRowCount(0);
        for (Product p : filteredProducts) {
            tableModel.addRow(new Object[]{p.getCode(), p.getName(), p.getCategory(), p.getPrice(), p.getStock()});
        }
    }

    private void isiFormDariTabel() {
        int row = drinkTable.getSelectedRow();
        if (row >= 0) {
            Product p = products.get(row);
            codeField.setText(p.getCode());
            nameField.setText(p.getName());
            categoryField.setSelectedItem(p.getCategory());
            priceField.setText(String.valueOf(p.getPrice()));
            stockField.setText(String.valueOf(p.getStock()));
        }
    }

    private void refreshTable() {
        loadProductData();
    }

    private void clearForm() {
        codeField.setText("");
        nameField.setText("");
        categoryField.setSelectedIndex(0);
        priceField.setText("");
        stockField.setText("");
        drinkTable.clearSelection();
    }
}

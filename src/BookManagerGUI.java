import Model.Book;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class BookManagerGUI extends JFrame {
    private BookDAO bookDAO;
    private JTable table;
    private DefaultTableModel tableModel;

    // Компоненти за добавяне/обновяване на книги
    private JTextField txtTitle, txtAuthor, txtGenre, txtPrice, txtId;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear;


    public BookManagerGUI() throws SQLException {
        Connection connection = DbConnection.getConnection();
        BookDAO bookDAO = new BookDAO(connection);

        setTitle("Book Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        initComponents();
        setVisible(true);
    }

    private void initComponents() throws SQLException {
        // Панел за формуляра
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));

        formPanel.add(new JLabel("ID:"));
        txtId = new JTextField();
        txtId.setEditable(false);
        formPanel.add(txtId);

        formPanel.add(new JLabel("Title:"));
        txtTitle = new JTextField();
        formPanel.add(txtTitle);

        formPanel.add(new JLabel("Author:"));
        txtAuthor = new JTextField();
        formPanel.add(txtAuthor);

        formPanel.add(new JLabel("Genre:"));
        txtGenre = new JTextField();
        formPanel.add(txtGenre);

        formPanel.add(new JLabel("Price:"));
        txtPrice = new JTextField();
        formPanel.add(txtPrice);

        // Панел за бутоните
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        btnAdd = new JButton("Add Book");
        btnUpdate = new JButton("Update Book");
        btnDelete = new JButton("Delete Book");
        btnClear = new JButton("Clear Fields");

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);

        // Добавяне на слушатели за бутоните
        btnAdd.addActionListener(e -> {
            try {
                addBook();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
        btnUpdate.addActionListener(e -> {
            try {
                updateBook();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
        btnDelete.addActionListener(e -> {
            try {
                deleteBook();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
        btnClear.addActionListener(e -> clearFields());

        // Таблица за показване на книгите
        String[] columnNames = {"ID", "Title", "Author", "Genre", "Price"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        refreshTable();

        // Добавяне на слушател за избиране на ред от таблицата
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int selectedRow = table.getSelectedRow();
                txtId.setText(tableModel.getValueAt(selectedRow, 0).toString());
                txtTitle.setText(tableModel.getValueAt(selectedRow, 1).toString());
                txtAuthor.setText(tableModel.getValueAt(selectedRow, 2).toString());
                txtGenre.setText(tableModel.getValueAt(selectedRow, 3).toString());
                txtPrice.setText(tableModel.getValueAt(selectedRow, 4).toString());
            }
        });

        // Настройка на основния панел
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.add(formPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        mainPanel.add(new JScrollPane(table), BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void refreshTable() throws SQLException {
        // Изчистване на модела на таблицата
        tableModel.setRowCount(0);

        // Извличане на книгите от базата данни
        List<Book> books = BookDAO.getAllBooks();
        for (Book book : books) {
            Object[] rowData = {
                    book.getId(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getGenre(),
                    book.getPrice()
            };
            tableModel.addRow(rowData);
        }
    }

    private void addBook() throws SQLException {
        String title = txtTitle.getText();
        String author = txtAuthor.getText();
        String genre = txtGenre.getText();
        double price;

        try {
            price = Double.parseDouble(txtPrice.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid price.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Book book = new Book(0,title, author, genre, price);
        bookDAO.addBook(book);
        refreshTable();
        clearFields();
    }

    private void updateBook() throws SQLException {
        int id;
        try {
            id = Integer.parseInt(txtId.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "No book selected.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String title = txtTitle.getText();
        String author = txtAuthor.getText();
        String genre = txtGenre.getText();
        double price;

        try {
            price = Double.parseDouble(txtPrice.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid price.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Book book = new Book(id, title, author, genre, price);
        bookDAO.updateBook(book);
        refreshTable();
        clearFields();
    }

    private void deleteBook() throws SQLException {
        int id;
        try {
            id = Integer.parseInt(txtId.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "No book selected.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this book?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            bookDAO.deleteBook(id);
            refreshTable();
            clearFields();
        }
    }

    private void clearFields() {
        txtId.setText("");
        txtTitle.setText("");
        txtAuthor.setText("");
        txtGenre.setText("");
        txtPrice.setText("");
    }

}

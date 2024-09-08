import Model.Book;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class BookManagerGUI extends JFrame {
    private JTable bookTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton addButton, deleteButton, searchButton, refreshButton;

    private Connection connection;
    private BookDAO bookDAO;

    public BookManagerGUI() throws SQLException {
        // Инициализация на връзката
        connection = DbConnection.getConnection();
        bookDAO = new BookDAO(connection);

        setLayout(new BorderLayout());

        // Създаване на таблицата с книги
        tableModel = new DefaultTableModel();
        bookTable = new JTable(tableModel);

        // Добавяне на колони в таблицата
        tableModel.addColumn("ID");
        tableModel.addColumn("Title");
        tableModel.addColumn("Author");
        tableModel.addColumn("Genre");
        tableModel.addColumn("Price");




        tableModel = new DefaultTableModel(new Object[]{"Title", "Author", "Genre", "Price"}, 0);
        bookTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(bookTable);
        loadAllBooks();

        // Създаване на панел за бутоните
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        // Създаване на бутоните
        addButton = new JButton("Add");
        deleteButton = new JButton("Delete");
        searchButton = new JButton("Search");
        refreshButton = new JButton("Refresh");

        // Добавяне на полето за търсене
        searchField = new JTextField(15);
        buttonPanel.add(searchField);
        buttonPanel.add(searchButton);
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        // Добавяне на слушатели към бутоните
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    addBookAction();

                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    refreshTable();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteBookAction();

            }
        });

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchBookAction();
                try {
                    refreshTable();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        add(new JScrollPane(bookTable), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void deleteBookAction() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow >= 0) {
            String bookIdString = (String) bookTable.getValueAt(selectedRow, 0);
            int bookId = Integer.parseInt(bookIdString);

            try (Connection connection = DbConnection.getConnection()) {
                BookDAO bookDAO = new BookDAO(connection);
                bookDAO.deleteBook(bookId);
                JOptionPane.showMessageDialog(this, "Book deleted successfully!");
                BookDAO.getAllBooks();
                refreshTable();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error deleting book.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a book to delete.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchBookAction() {
        String searchText = searchField.getText();
        if (!searchText.isEmpty()) {
            try (Connection connection = DbConnection.getConnection()) {
                BookDAO bookDAO = new BookDAO(connection);
                List<Book> books = Collections.singletonList(bookDAO.getBookById(Integer.parseInt(searchText)));

                // Изчистване на таблицата
                tableModel.setRowCount(0);

                // Добавяне на намерените книги в таблицата
                for (Book book : books) {
                    tableModel.addRow(new Object[]{
                            book.getId(),
                            book.getTitle(),
                            book.getAuthor(),
                            book.getGenre(),
                            book.getPrice()
                    });
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error searching books.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please enter a title to search.", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }


    public void addBookAction() throws SQLException {
        // Създаване на нов прозорец за добавяне на книга
        JFrame addBookFrame = new JFrame("Add New Book");
        addBookFrame.setSize(300, 200);
        addBookFrame.setLayout(new GridLayout(5, 2));

        // Създаване на полета за въвеждане на данни
        JTextField titleField = new JTextField();
        JTextField authorField = new JTextField();
        JTextField genreField = new JTextField();
        JTextField priceField = new JTextField();

        // Добавяне на етикети и полета към новия прозорец
        addBookFrame.add(new JLabel("Title:"));
        addBookFrame.add(titleField);
        addBookFrame.add(new JLabel("Author:"));
        addBookFrame.add(authorField);
        addBookFrame.add(new JLabel("Genre:"));
        addBookFrame.add(genreField);
        addBookFrame.add(new JLabel("Price:"));
        addBookFrame.add(priceField);

        // Създаване на бутон за потвърждаване
        JButton saveButton = new JButton("Save");
        addBookFrame.add(saveButton);

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Вземане на стойностите от полетата за въвеждане
                String title = titleField.getText();
                String author = authorField.getText();
                String genre = genreField.getText();
                double price = Double.parseDouble(priceField.getText());

                // Създаване на нова книга и добавяне в базата данни
                Book newBook = new Book(null, title, author, genre, price);
                try {
                    bookDAO.addBook(newBook);
                    // Актуализиране на таблицата след добавяне
                    loadAllBooks();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                // Затваряне на прозореца
                addBookFrame.dispose();
            }
        });
        addBookFrame.setVisible(true);
    }

    private void loadAllBooks() {
        tableModel.setRowCount(0); // Изчистване на таблицата преди зареждане
        try {
            // Получаване на всички книги
            List<Book> books = bookDAO.getAllBooks();

            // Добавяне на всяка книга като ред в таблицата
            for (Book book : books) {
                tableModel.addRow(new Object[]{book.getId(), book.getTitle(), book.getAuthor(), book.getGenre(), book.getPrice()});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
}

import Model.Book;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class BookManagerGUI extends JFrame {
    private JTable bookTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton addButton, deleteButton, searchButton, refreshButton, updateButton;

    private Connection connection;
    private BookDAO bookDAO;

    public BookManagerGUI() throws SQLException {

        connection = DbConnection.getConnection();
        bookDAO = new BookDAO(connection);

        setLayout(new BorderLayout());

        tableModel = new DefaultTableModel();
        bookTable = new JTable(tableModel);

        tableModel.addColumn("ID");
        tableModel.addColumn("Title");
        tableModel.addColumn("Author");
        tableModel.addColumn("Genre");
        tableModel.addColumn("Price");


        tableModel = new DefaultTableModel(new Object[]{"ID", "Title", "Author", "Genre", "Price"}, 0);
        bookTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(bookTable);
        loadAllBooks();

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        addButton = new JButton("Add");
        deleteButton = new JButton("Delete");
        searchButton = new JButton("Search");
        refreshButton = new JButton("Refresh");
        updateButton = new JButton("Update");

        searchField = new JTextField(15);
        buttonPanel.add(searchField);
        buttonPanel.add(searchButton);
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(updateButton);

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
                    List<Book> books = BookDAO.refreshTable();

                    for (Book book : books) {
                        tableModel.addRow(new Object[]{book.getId(), book.getTitle(), book.getAuthor(), book.getGenre(), book.getPrice()});
                    }
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
            }
        });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateBookAction();
            }
        });

        add(new JScrollPane(bookTable), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void updateBookAction() {
        Book selectedBook = getSelectedBook();
        if (selectedBook != null) {
            UpdateBookDialog dialog = new UpdateBookDialog(this, selectedBook);
            dialog.setVisible(true);

            if (dialog.isConfirmed()) {
                Book updatedBook = dialog.getUpdatedBook();
                if (updatedBook != null) {
                    try {
                        bookDAO.updateBook(updatedBook);
                        loadAllBooks();
                        JOptionPane.showMessageDialog(this, "Book updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(this, "Error updating book.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }
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
                List<Book> books = bookDAO.getBooksByTitle(searchText);  // Търсене по заглавие

                tableModel.setRowCount(0);

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


    private Book getSelectedBook() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow != -1) {  // Check if a row is selected
            String id = (String) tableModel.getValueAt(selectedRow, 0);
            String title = (String) tableModel.getValueAt(selectedRow, 1);
            String author = (String) tableModel.getValueAt(selectedRow, 2);
            String genre = (String) tableModel.getValueAt(selectedRow, 3);
            Double price = (Double) tableModel.getValueAt(selectedRow, 4);

            return new Book(id, title, author, genre, price);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a book to update.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
    }

    public void addBookAction() throws SQLException {

        JFrame addBookFrame = new JFrame("Add New Book");
        addBookFrame.setSize(300, 200);
        addBookFrame.setLayout(new GridLayout(5, 2));

        JTextField titleField = new JTextField();
        JTextField authorField = new JTextField();
        JTextField genreField = new JTextField();
        JTextField priceField = new JTextField();

        addBookFrame.add(new JLabel("Title:"));
        addBookFrame.add(titleField);
        addBookFrame.add(new JLabel("Author:"));
        addBookFrame.add(authorField);
        addBookFrame.add(new JLabel("Genre:"));
        addBookFrame.add(genreField);
        addBookFrame.add(new JLabel("Price:"));
        addBookFrame.add(priceField);

        JButton saveButton = new JButton("Save");
        addBookFrame.add(saveButton);

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String title = titleField.getText();
                String author = authorField.getText();
                String genre = genreField.getText();
                double price = Double.parseDouble(priceField.getText());

                Book newBook = new Book(null, title, author, genre, price);
                try {
                    bookDAO.addBook(newBook);

                    loadAllBooks();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

                addBookFrame.dispose();
            }
        });
        addBookFrame.setVisible(true);
    }

    private void loadAllBooks() {
        tableModel.setRowCount(0);

        try {
            List<Book> books = BookDAO.getAllBooks();

            for (Book book : books) {
                tableModel.addRow(new Object[]{book.getId(), book.getTitle(), book.getAuthor(), book.getGenre(), book.getPrice()});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void refreshTable() throws SQLException {
        tableModel.setRowCount(0);

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

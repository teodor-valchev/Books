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
    private JButton addButton, deleteButton, searchButton;

    private BookDAO bookDAO;

    public BookManagerGUI() throws SQLException {
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

        // Зареждане на всички книги
        BookDAO.getAllBooks();

        // Създаване на панел за бутоните
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        // Създаване на бутоните
        addButton = new JButton("Add");
        deleteButton = new JButton("Delete");
        searchButton = new JButton("Search");

        // Добавяне на полето за търсене
        searchField = new JTextField(15);
        buttonPanel.add(searchField);
        buttonPanel.add(searchButton);
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);

        // Добавяне на слушатели към бутоните
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addBookAction();
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

        add(new JScrollPane(bookTable), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void deleteBookAction() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow >= 0) {
            int bookId = (int) bookTable.getValueAt(selectedRow, 0);

            try (Connection connection = DbConnection.getConnection()) {
                BookDAO bookDAO = new BookDAO(connection);
                bookDAO.deleteBook(bookId);
                JOptionPane.showMessageDialog(this, "Book deleted successfully!");
                BookDAO.getAllBooks();
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


    public void addBookAction() {
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

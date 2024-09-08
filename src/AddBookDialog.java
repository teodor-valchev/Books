import Model.Book;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;

public class AddBookDialog extends JDialog {
    private JTextField titleField, authorField, genreField, priceField;
    private JButton addButton;

    public AddBookDialog() {
        setTitle("Add New Book");
        setSize(400, 300);
        setLayout(new GridLayout(5, 2, 10, 10));
        setLocationRelativeTo(null);

        // Инициализиране на компонентите
        titleField = new JTextField();
        authorField = new JTextField();
        genreField = new JTextField();
        priceField = new JTextField();
        addButton = new JButton("Add Book");

        // Добавяне на компонентите
        add(new JLabel("Title:"));
        add(titleField);
        add(new JLabel("Author:"));
        add(authorField);
        add(new JLabel("Genre:"));
        add(genreField);
        add(new JLabel("Price:"));
        add(priceField);
        add(new JLabel()); // Празен компонент
        add(addButton);

        // Добавяне на действие към бутона
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addBook();
            }
        });
    }

    private void addBook() {
        String title = titleField.getText();
        String author = authorField.getText();
        String genre = genreField.getText();
        double price = Double.parseDouble(priceField.getText());

        try (Connection connection = DbConnection.getConnection()) {
            BookDAO bookDAO = new BookDAO(connection);
            bookDAO.addBook(new Book("1",title, author, genre, price));
            JOptionPane.showMessageDialog(this, "Book added successfully!");
            dispose(); // Затваряне на диалога
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding book.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

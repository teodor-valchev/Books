import Model.Book;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UpdateBookDialog extends JDialog {
    private JTextField titleField;
    private JTextField authorField;
    private JTextField genreField;
    private JTextField priceField;
    private Book updatedBook;
    private boolean confirmed;

    public UpdateBookDialog(Frame parent, Book book) {
        super(parent, "Update Book", true);
        setLayout(new GridLayout(5, 2));
        setSize(300, 200);
        setLocationRelativeTo(parent);

        titleField = new JTextField(book.getTitle(), 20);
        authorField = new JTextField(book.getAuthor(), 20);
        genreField = new JTextField(book.getGenre(), 20);
        priceField = new JTextField(String.valueOf(book.getPrice()), 20);

        add(new JLabel("Title:"));
        add(titleField);
        add(new JLabel("Author:"));
        add(authorField);
        add(new JLabel("Genre:"));
        add(genreField);
        add(new JLabel("Price:"));
        add(priceField);

        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        add(buttonPanel);

        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    updatedBook = new Book(
                            book.getId(),
                            titleField.getText(),
                            authorField.getText(),
                            genreField.getText(),
                            Double.parseDouble(priceField.getText())
                    );
                    confirmed = true;
                    dispose();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(UpdateBookDialog.this,
                            "Invalid price format.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                confirmed = false;
                dispose();
            }
        });
    }

    public Book getUpdatedBook() {
        return updatedBook;
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}
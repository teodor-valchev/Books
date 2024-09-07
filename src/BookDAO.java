import Model.Book;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {
    private Connection connection;

    public BookDAO(Connection connection) {
        this.connection = connection;
    }

    public static void initializeDatabase() {
        String url = "jdbc:h2:C:/Users/Tedii/Desktop/Books_Project/Books/LibraryDB";
        String user = "sa";
        String password = "";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement()) {

            // Create table if it does not exist
            String createTableSQL = "CREATE TABLE IF NOT EXISTS BOOKS (" +
                    "id IDENTITY PRIMARY KEY, " +
                    "title VARCHAR(255), " +
                    "author VARCHAR(255), " +
                    "genre VARCHAR(255), " +
                    "price DECIMAL(10, 2))";
            stmt.execute(createTableSQL);

            System.out.println("Database initialized.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addBook(Book book) throws SQLException {
        String query = "INSERT INTO BOOKS (title, author, genre, price) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, book.getTitle());
            statement.setString(2, book.getAuthor());
            statement.setString(3, book.getGenre());
            statement.setDouble(4, book.getPrice());
            statement.executeUpdate();
        }
    }

    public List<Book> getAllBooks() throws SQLException {
        String query = "SELECT * FROM BOOKS";
        List<Book> books = new ArrayList<>();
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                Book book = new Book(
                        resultSet.getInt("id"),
                        resultSet.getString("title"),
                        resultSet.getString("author"),
                        resultSet.getString("genre"),
                        resultSet.getDouble("price")
                );
                books.add(book);
            }
        }
        return books;
    }

    // Други методи: updateBook, deleteBook
}

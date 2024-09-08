import Model.Book;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {
    private static Connection connection;

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
        if (connection == null || connection.isClosed()) {
            connection = DbConnection.getConnection();
        }

        String query = "INSERT INTO BOOKS (title, author, genre, price) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, book.getTitle());
            statement.setString(2, book.getAuthor());
            statement.setString(3, book.getGenre());
            statement.setDouble(4, book.getPrice());
            statement.executeUpdate();
        }
    }

    public static List<Book> getAllBooks() throws SQLException {
        String query = "SELECT * FROM BOOKS";
        List<Book> books = new ArrayList<>();
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                Book book = new Book(
                        resultSet.getString("id"),
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

    public static List<Book> refreshTable() throws SQLException {
        // Ensure the connection is still open
        if (connection == null || connection.isClosed()) {
            connection = DbConnection.getConnection();
        }

        String query = "SELECT * FROM BOOKS";  // SQL query to retrieve all books
        List<Book> books = new ArrayList<>();  // List to hold the retrieved books

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            // Loop through the result set to get all book entries
            while (resultSet.next()) {
                // Create a new book object from each row in the result set
                Book book = new Book(
                        resultSet.getString("id"),
                        resultSet.getString("title"),
                        resultSet.getString("author"),
                        resultSet.getString("genre"),
                        resultSet.getDouble("price")
                );

                // Add the book to the books list
                books.add(book);
            }
        }

        return books;  // Return the list of books
    }

    public void deleteBook(int id) {
        try {
            String query = "DELETE FROM BOOKS WHERE id = ?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Book> getBooksByTitle(String title) throws SQLException {
        List<Book> books = new ArrayList<>();
        String query = "SELECT * FROM BOOKS WHERE LOWER(title) LIKE ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, "%" + title.toLowerCase() + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Book book = new Book(
                            rs.getString("id"),
                            rs.getString("title"),
                            rs.getString("author"),
                            rs.getString("genre"),
                            rs.getDouble("price")
                    );
                    books.add(book);
                }
            }
        }
        return books;
    }

    public void updateBook(Book book) throws SQLException {
        String query = "UPDATE BOOKS SET title = ?, author = ?, genre = ?, price = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setString(3, book.getGenre());
            stmt.setDouble(4, book.getPrice());
            stmt.setString(5, book.getId());
            stmt.executeUpdate();
        }
    }
}

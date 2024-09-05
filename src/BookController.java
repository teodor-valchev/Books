import Model.Book;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class BookController {
    private BookDAO bookDAO;

    public BookController() throws SQLException {
        Connection connection = DbConnection.getConnection();
        this.bookDAO = new BookDAO(connection);
    }

    public void addBook(Book book) throws SQLException {
        bookDAO.addBook(book);
    }

    public List<Book> getAllBooks() throws SQLException {
        return bookDAO.getAllBooks();
    }
}


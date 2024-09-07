import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnection {
    static Connection conn = null;

    static Connection getConnection() {

        try {
            Class.forName("org.h2.Driver");
            try {
                //  conn = DriverManager.getConnection("jdbc:h2:tcp://localhost/D:\\CarsProjecttt\\CarDB\\h2;USER=sa;PASSWORD=1234");
                //conn = DriverManager.getConnection("jdbc:h2:tcp://localhost/D:\\CarsProjecttt\\CarDB");
                //conn = DriverManager.getConnection("jdbc:h2:tcp://localhost/D:\\CarsProjecttt\\CarDB\\h2;USER=sa;PASSWORD=123");
                conn = DriverManager.getConnection("jdbc:h2:tcp://localhost/~/Desktop/Books_Project/Books/LibraryDB", "sa", "");
            } catch (SQLException e) {
                e.printStackTrace();
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();

        }
        return conn;
    }
}
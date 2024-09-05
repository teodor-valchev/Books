import java.sql.Connection;
import java.sql.DriverManager;

public class DbConnection {
    Connection connection = DriverManager.getConnection("jdbc:h2:~/test", "sa", "");
}

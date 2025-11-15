import java.sql.Connection;
import java.sql.DriverManager;


public class DatabaseConnection {
    private static final String URL = "jdbc:postgresql://localhost:5432/Capteurs";
    private static final String USER = "postgres";
    private static final String PASSWORD = "passroot";

    public static Connection connect() throws Exception {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

}

import java.sql.*;

public class DBConnection {
    static final String URL = "jdbc:mysql://localhost:3306/atm_db";
    static final String USER = "root"; // change if needed
    static final String PASSWORD = "Prasanna@1234"; // your MySQL password

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}

/*package dbaccess;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL =
        "jdbc:mysql://localhost:3306/silvercare_db?serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "Jeinakoh220407"; 

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException("MySQL JDBC Driver not found.", ex);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}*/
package dbaccess;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException("MySQL JDBC Driver not found.", ex);
        }
    }

    public static Connection getConnection() throws SQLException {
        // Read from environment variables (set in AWS Elastic Beanstalk)
        String url  = System.getenv("DB_URL");
        String user = System.getenv("DB_USER");
        String pass = System.getenv("DB_PASS");

        // fallback (optional) for local dev
        if (url == null)  url  = "jdbc:mysql://localhost:3306/silvercare_db?serverTimezone=UTC";
        if (user == null) user = "root";
        if (pass == null) pass = "Jeinakoh220407";

        return DriverManager.getConnection(url, user, pass);
    }
}


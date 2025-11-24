package bankapp;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    public static Connection getConnection() {
        Connection con = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            java.util.Properties props = new java.util.Properties();
            try (java.io.InputStream input = DBConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
                if (input == null) {
                    System.err.println("ERROR: Unable to find db.properties file in classpath");
                    System.err.println("Please ensure db.properties exists in src/main/resources/");
                    return null;
                }
                props.load(input);
            }

            String url = props.getProperty("db.url");
            String username = props.getProperty("db.username");
            String password = props.getProperty("db.password");

            if (url == null || username == null || password == null) {
                System.err.println("ERROR: Database properties are incomplete");
                return null;
            }

            con = DriverManager.getConnection(url, username, password);

            if (con != null) {
                System.out.println("Database connection established successfully");
            }

        } catch (ClassNotFoundException e) {
            System.err.println("ERROR: MySQL JDBC Driver not found: " + e.getMessage());
        } catch (java.sql.SQLException e) {
            System.err.println("ERROR: Database connection failed: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("ERROR: Unexpected error during database connection: " + e.getMessage());
        }
        return con;
    }
}

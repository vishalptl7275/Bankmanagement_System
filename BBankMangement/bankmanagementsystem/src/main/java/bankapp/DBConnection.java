package bankapp;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

/**
 * Opens a MySQL connection using environment variables first, then a local
 * db.properties file. Environment variables make container deployment safe.
 */
public final class DBConnection {
    private DBConnection() {
    }

    public static Connection getConnection() throws java.sql.SQLException {
        String url = System.getenv("DB_URL");
        String username = System.getenv("DB_USERNAME");
        String password = System.getenv("DB_PASSWORD");

        if (isBlank(url) || isBlank(username) || password == null) {
            Properties properties = new Properties();
            try (InputStream input = DBConnection.class.getClassLoader()
                    .getResourceAsStream("db.properties")) {
                if (input != null) {
                    properties.load(input);
                }
            } catch (java.io.IOException e) {
                throw new java.sql.SQLException("Could not read db.properties", e);
            }

            if (isBlank(url)) {
                url = properties.getProperty("db.url");
            }
            if (isBlank(username)) {
                username = properties.getProperty("db.username");
            }
            if (password == null) {
                password = properties.getProperty("db.password");
            }
        }

        if (isBlank(url) || isBlank(username) || password == null) {
            throw new java.sql.SQLException(
                    "Database configuration is missing. Set DB_URL, DB_USERNAME and DB_PASSWORD, "
                    + "or provide src/main/resources/db.properties.");
        }

        return DriverManager.getConnection(url, username, password);
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}

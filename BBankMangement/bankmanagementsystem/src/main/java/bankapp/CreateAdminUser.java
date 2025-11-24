package bankapp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utility class to create admin users in the database.
 * Run this once to create your first admin user.
 */
public class CreateAdminUser {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== Create Admin User ===");
        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();

        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();

        if (username.isEmpty() || password.isEmpty()) {
            System.err.println("Error: Username and password cannot be empty!");
            scanner.close();
            return;
        }

        // Hash the password with BCrypt
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        // Insert into database
        try (Connection conn = DBConnection.getConnection()) {

            if (conn == null) {
                System.err.println("ERROR: Could not connect to database!");
                System.err.println("Check your db.properties file.");
                scanner.close();
                return;
            }

            String sql = "INSERT INTO admins (username, password) VALUES (?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, username);
                ps.setString(2, hashedPassword);

                int rows = ps.executeUpdate();

                if (rows > 0) {
                    System.out.println("\n✅ SUCCESS! Admin user created:");
                    System.out.println("   Username: " + username);
                    System.out.println("   Password: " + password);
                    System.out.println("\nYou can now login at: http://localhost:8080/login.html");
                } else {
                    System.err.println("ERROR: Failed to create admin user.");
                }
            }

        } catch (SQLException e) {
            System.err.println("ERROR: Database error - " + e.getMessage());

            if (e.getMessage().contains("Duplicate entry")) {
                System.err.println("Username '" + username + "' already exists!");
            } else if (e.getMessage().contains("doesn't exist")) {
                System.err.println("\nThe 'admins' table doesn't exist!");
                System.err.println("Please create it first with this SQL:\n");
                System.err.println("CREATE TABLE admins (");
                System.err.println("    id INT PRIMARY KEY AUTO_INCREMENT,");
                System.err.println("    username VARCHAR(50) UNIQUE NOT NULL,");
                System.err.println("    password VARCHAR(100) NOT NULL");
                System.err.println(");");
            }
        } finally {
            scanner.close();
        }
    }
}

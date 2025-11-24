package bankapp;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.mindrot.jbcrypt.BCrypt;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirm_password");

        // Basic validation
        if (username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty() ||
                confirmPassword == null || confirmPassword.trim().isEmpty()) {
            response.sendRedirect("register.html?error=empty");
            return;
        }

        // Check if passwords match
        if (!password.equals(confirmPassword)) {
            response.sendRedirect("register.html?error=mismatch");
            return;
        }

        // Validate password length
        if (password.length() < 6) {
            response.sendRedirect("register.html?error=short_password");
            return;
        }

        // Validate username length
        if (username.length() < 3) {
            response.sendRedirect("register.html?error=short_username");
            return;
        }

        // Hash password with BCrypt
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        // Insert into database
        String sql = "INSERT INTO admins (username, password) VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection()) {

            if (conn == null) {
                System.err.println("ERROR: Failed to get database connection in RegisterServlet");
                response.sendRedirect("register.html?error=db");
                return;
            }

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, username.trim());
                ps.setString(2, hashedPassword);

                int rows = ps.executeUpdate();

                if (rows > 0) {
                    System.out.println("New admin registered: " + username);
                    response.sendRedirect("login.html?registered=success");
                } else {
                    System.err.println("ERROR: Failed to register user: " + username);
                    response.sendRedirect("register.html?error=failed");
                }
            }

        } catch (SQLException e) {
            System.err.println("ERROR: Database error in RegisterServlet: " + e.getMessage());

            // Check for duplicate username
            if (e.getMessage().contains("Duplicate entry")) {
                response.sendRedirect("register.html?error=duplicate");
            } else {
                response.sendRedirect("register.html?error=db");
            }
        }
    }
}

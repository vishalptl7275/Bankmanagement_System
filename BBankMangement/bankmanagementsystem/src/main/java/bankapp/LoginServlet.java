package bankapp;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.mindrot.jbcrypt.BCrypt;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/login") // make sure your login form uses action="login" and method="post"
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // basic validation
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            response.sendRedirect("login.html?error=empty");
            return;
        }

        // DB check
        String sql = "SELECT password FROM admins WHERE username = ?";

        try (Connection conn = DBConnection.getConnection()) {

            if (conn == null) {
                System.err.println("ERROR: Failed to get database connection in LoginServlet");
                response.sendRedirect("login.html?error=db");
                return;
            }

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {

                        String storedPassword = rs.getString("password");

                        // === BCrypt check ===
                        if (BCrypt.checkpw(password, storedPassword)) {
                            HttpSession previousSession = request.getSession(false);
                            if (previousSession != null) {
                                previousSession.invalidate();
                            }
                            HttpSession session = request.getSession(true);
                            session.setAttribute(Constants.SESSION_ADMIN_USER, username);
                            System.out.println("User logged in successfully: " + username);
                            // Redirect to the actual dashboard file name (case must match)
                            response.sendRedirect("Dashboard.html");
                            return;
                        }

                    }
                    // invalid credentials
                    System.out.println("Login failed for user: " + username);
                    response.sendRedirect("login.html?error=invalid");
                }
            }

        } catch (SQLException e) {
            System.err.println("ERROR: Database error during login: " + e.getMessage());
            response.sendRedirect("login.html?error=db");
        }
    }
}

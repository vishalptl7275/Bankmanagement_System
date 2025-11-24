package bankapp;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/checkBalance")
public class CheckBalanceServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        // Session check
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(Constants.SESSION_ADMIN_USER) == null) {
            response.sendRedirect("login.html");
            return;
        }

        String accNo = request.getParameter("account_number");

        // Validation
        if (accNo == null || accNo.trim().isEmpty()) {
            response.sendRedirect("check_balance.html?error=empty");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {

            if (conn == null) {
                System.err.println("ERROR: Failed to get database connection in CheckBalanceServlet");
                response.sendRedirect("check_balance.html?error=db");
                return;
            }

            String sql = "SELECT name, balance, account_type FROM customers WHERE account_number = ?";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, accNo);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String name = rs.getString("name");
                        double balance = rs.getDouble("balance");
                        String accountType = rs.getString("account_type");

                        System.out.println("Balance checked for account: " + accNo);

                        // Redirect with balance info
                        String redirectUrl = String.format(
                                "check_balance.html?success=1&accNo=%s&name=%s&balance=%.2f&type=%s",
                                java.net.URLEncoder.encode(accNo, "UTF-8"),
                                java.net.URLEncoder.encode(name, "UTF-8"),
                                balance,
                                java.net.URLEncoder.encode(accountType, "UTF-8"));

                        response.sendRedirect(redirectUrl);
                    } else {
                        System.out.println("Account not found: " + accNo);
                        response.sendRedirect("check_balance.html?error=notfound");
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("ERROR: Database error in CheckBalanceServlet: " + e.getMessage());
            response.sendRedirect("check_balance.html?error=db");
        }
    }
}

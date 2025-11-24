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

@WebServlet("/deposit")
public class DepositServlet extends HttpServlet {

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
        String amtStr = request.getParameter("amount");
        String desc = request.getParameter("description");

        // Validation
        if (accNo == null || accNo.trim().isEmpty() || amtStr == null || amtStr.trim().isEmpty()) {
            response.sendRedirect("deposit.html?error=empty");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amtStr);
            if (amount <= 0) {
                response.sendRedirect("deposit.html?error=negative");
                return;
            }
        } catch (NumberFormatException e) {
            response.sendRedirect("deposit.html?error=invalid_amount");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {

            if (conn == null) {
                System.err.println("ERROR: Failed to get database connection in DepositServlet");
                response.sendRedirect("deposit.html?error=db");
                return;
            }

            // Check if account exists and get current balance
            String selectSql = "SELECT balance FROM customers WHERE account_number = ?";
            try (PreparedStatement ps = conn.prepareStatement(selectSql)) {
                ps.setString(1, accNo);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        System.out.println("Account not found: " + accNo);
                        response.sendRedirect("deposit.html?error=notfound");
                        return;
                    }

                    double currentBalance = rs.getDouble("balance");
                    double newBalance = currentBalance + amount;

                    // Update balance
                    String updateSql = "UPDATE customers SET balance = ? WHERE account_number = ?";
                    try (PreparedStatement updatePs = conn.prepareStatement(updateSql)) {
                        updatePs.setDouble(1, newBalance);
                        updatePs.setString(2, accNo);
                        int rows = updatePs.executeUpdate();

                        if (rows > 0) {
                            System.out.println("Deposit successful for account " + accNo + ": ₹" + amount);
                            response.sendRedirect("deposit.html?success=1&bal=" + newBalance);
                        } else {
                            System.err.println("ERROR: Failed to update balance for: " + accNo);
                            response.sendRedirect("deposit.html?error=update_failed");
                        }
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("ERROR: Database error in DepositServlet: " + e.getMessage());
            response.sendRedirect("deposit.html?error=db");
        }
    }
}

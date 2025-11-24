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

@WebServlet("/transfer")
public class TransferServlet extends HttpServlet {

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

        String fromAcc = request.getParameter("from_account");
        String toAcc = request.getParameter("to_account");
        String amtStr = request.getParameter("amount");

        // Validation
        if (fromAcc == null || fromAcc.trim().isEmpty() ||
                toAcc == null || toAcc.trim().isEmpty() ||
                amtStr == null || amtStr.trim().isEmpty()) {
            response.sendRedirect("transfer.html?error=empty");
            return;
        }

        if (fromAcc.equals(toAcc)) {
            response.sendRedirect("transfer.html?error=same_account");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amtStr);
            if (amount <= 0) {
                response.sendRedirect("transfer.html?error=negative");
                return;
            }
        } catch (NumberFormatException e) {
            response.sendRedirect("transfer.html?error=invalid_amount");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {

            if (conn == null) {
                System.err.println("ERROR: Failed to get database connection in TransferServlet");
                response.sendRedirect("transfer.html?error=db");
                return;
            }

            // Start transaction
            conn.setAutoCommit(false);

            try {
                // Check FROM account and balance
                String selectSql = "SELECT balance FROM customers WHERE account_number = ?";
                double fromBalance;
                try (PreparedStatement ps = conn.prepareStatement(selectSql)) {
                    ps.setString(1, fromAcc);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            conn.rollback();
                            response.sendRedirect("transfer.html?error=from_notfound");
                            return;
                        }
                        fromBalance = rs.getDouble("balance");
                    }
                }

                // Check if sufficient balance
                if (fromBalance < amount) {
                    conn.rollback();
                    response.sendRedirect("transfer.html?error=insufficient");
                    return;
                }

                // Check TO account exists
                double toBalance;
                try (PreparedStatement ps = conn.prepareStatement(selectSql)) {
                    ps.setString(1, toAcc);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            conn.rollback();
                            response.sendRedirect("transfer.html?error=to_notfound");
                            return;
                        }
                        toBalance = rs.getDouble("balance");
                    }
                }

                // Deduct from FROM account
                String updateSql = "UPDATE customers SET balance = ? WHERE account_number = ?";
                try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                    ps.setDouble(1, fromBalance - amount);
                    ps.setString(2, fromAcc);
                    ps.executeUpdate();
                }

                // Add to TO account
                try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                    ps.setDouble(1, toBalance + amount);
                    ps.setString(2, toAcc);
                    ps.executeUpdate();
                }

                // Commit transaction
                conn.commit();
                System.out.println("Transfer successful: ₹" + amount + " from " + fromAcc + " to " + toAcc);
                response.sendRedirect("transfer.html?success=1&amount=" + amount);

            } catch (SQLException e) {
                conn.rollback();
                System.err.println("ERROR: Transaction failed: " + e.getMessage());
                response.sendRedirect("transfer.html?error=db");
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (SQLException e) {
            System.err.println("ERROR: Database error in TransferServlet: " + e.getMessage());
            response.sendRedirect("transfer.html?error=db");
        }
    }
}

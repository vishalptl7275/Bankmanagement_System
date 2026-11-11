package bankapp;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
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

@WebServlet("/withdraw")
public class WithdrawServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(Constants.SESSION_ADMIN_USER) == null) {
            response.sendRedirect("login.html");
            return;
        }

        String account = request.getParameter("account_number");
        String amountText = request.getParameter("amount");
        if (account == null || account.isBlank() || amountText == null || amountText.isBlank()) {
            response.sendRedirect("withdraw.html?error=empty");
            return;
        }

        BigDecimal amount;
        try {
            amount = new BigDecimal(amountText.trim()).setScale(2, RoundingMode.UNNECESSARY);
            if (amount.signum() <= 0) {
                response.sendRedirect("withdraw.html?error=negative");
                return;
            }
        } catch (NumberFormatException | ArithmeticException e) {
            response.sendRedirect("withdraw.html?error=invalid_amount");
            return;
        }

        String updateSql = "UPDATE customers SET balance = balance - ? "
                + "WHERE account_number = ? AND balance >= ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement update = conn.prepareStatement(updateSql)) {
            update.setBigDecimal(1, amount);
            update.setString(2, account);
            update.setBigDecimal(3, amount);
            if (update.executeUpdate() == 1) {
                response.sendRedirect("withdraw.html?success=1");
                return;
            }

            try (PreparedStatement lookup = conn.prepareStatement(
                    "SELECT 1 FROM customers WHERE account_number = ?")) {
                lookup.setString(1, account);
                try (ResultSet result = lookup.executeQuery()) {
                    response.sendRedirect(result.next()
                            ? "withdraw.html?error=insufficient"
                            : "withdraw.html?error=notfound");
                }
            }
        } catch (SQLException e) {
            getServletContext().log("Withdrawal failed", e);
            response.sendRedirect("withdraw.html?error=db");
        }
    }
}

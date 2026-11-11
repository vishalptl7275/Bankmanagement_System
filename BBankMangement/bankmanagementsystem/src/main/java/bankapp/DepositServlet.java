package bankapp;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
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
            response.sendRedirect("deposit.html?error=empty");
            return;
        }

        BigDecimal amount;
        try {
            amount = new BigDecimal(amountText.trim()).setScale(2, RoundingMode.UNNECESSARY);
            if (amount.signum() <= 0) {
                response.sendRedirect("deposit.html?error=negative");
                return;
            }
        } catch (NumberFormatException | ArithmeticException e) {
            response.sendRedirect("deposit.html?error=invalid_amount");
            return;
        }

        String sql = "UPDATE customers SET balance = balance + ? WHERE account_number = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setBigDecimal(1, amount);
            statement.setString(2, account);
            response.sendRedirect(statement.executeUpdate() == 1
                    ? "deposit.html?success=1"
                    : "deposit.html?error=notfound");
        } catch (SQLException e) {
            getServletContext().log("Deposit failed", e);
            response.sendRedirect("deposit.html?error=db");
        }
    }
}

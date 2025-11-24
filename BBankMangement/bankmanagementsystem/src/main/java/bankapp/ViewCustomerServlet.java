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

@WebServlet("/viewCustomer")
public class ViewCustomerServlet extends HttpServlet {

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
            response.sendRedirect("view_customer.html?error=empty");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {

            if (conn == null) {
                System.err.println("ERROR: Failed to get database connection in ViewCustomerServlet");
                response.sendRedirect("view_customer.html?error=db");
                return;
            }

            String sql = "SELECT account_number, name, email, phone, address, balance, account_type FROM customers WHERE account_number = ?";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, accNo);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        // Customer found - redirect with data in URL parameters
                        String name = rs.getString("name");
                        String email = rs.getString("email");
                        String phone = rs.getString("phone");
                        String address = rs.getString("address");
                        double balance = rs.getDouble("balance");
                        String accountType = rs.getString("account_type");

                        System.out.println("Customer found: " + accNo);

                        // Encode parameters for URL
                        String redirectUrl = String.format(
                                "view_customer.html?success=1&accNo=%s&name=%s&email=%s&phone=%s&address=%s&balance=%.2f&type=%s",
                                java.net.URLEncoder.encode(accNo, "UTF-8"),
                                java.net.URLEncoder.encode(name, "UTF-8"),
                                java.net.URLEncoder.encode(email, "UTF-8"),
                                java.net.URLEncoder.encode(phone, "UTF-8"),
                                java.net.URLEncoder.encode(address, "UTF-8"),
                                balance,
                                java.net.URLEncoder.encode(accountType, "UTF-8"));

                        response.sendRedirect(redirectUrl);
                    } else {
                        System.out.println("Customer not found: " + accNo);
                        response.sendRedirect("view_customer.html?error=notfound");
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("ERROR: Database error in ViewCustomerServlet: " + e.getMessage());
            response.sendRedirect("view_customer.html?error=db");
        }
    }
}

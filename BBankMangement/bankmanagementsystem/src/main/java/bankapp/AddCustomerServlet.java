package bankapp;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/addCustomer")
public class AddCustomerServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        // Session check
        jakarta.servlet.http.HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(Constants.SESSION_ADMIN_USER) == null) {
            response.sendRedirect("login.html");
            return;
        }

        String accNo = request.getParameter("account_number");
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String addr = request.getParameter("address");
        String pin = request.getParameter("pin");
        String type = request.getParameter("account_type");
        String balStr = request.getParameter("balance");

        // Input validation
        if (accNo == null || accNo.trim().isEmpty() ||
                name == null || name.trim().isEmpty() ||
                email == null || email.trim().isEmpty() ||
                phone == null || phone.trim().isEmpty() ||
                addr == null || addr.trim().isEmpty() ||
                pin == null || pin.trim().isEmpty() ||
                type == null || type.trim().isEmpty()) {
            response.sendRedirect("add_customer.html?error=empty");
            return;
        }

        // Validate account number format
        if (!accNo.matches(Constants.ACCOUNT_NUMBER_PATTERN)) {
            response.sendRedirect("add_customer.html?error=invalid_account");
            return;
        }

        // Validate email format
        if (!email.matches(Constants.EMAIL_PATTERN)) {
            response.sendRedirect("add_customer.html?error=invalid_email");
            return;
        }

        // Validate phone format
        if (!phone.matches(Constants.PHONE_PATTERN)) {
            response.sendRedirect("add_customer.html?error=invalid_phone");
            return;
        }

        // Validate PIN format
        if (!pin.matches(Constants.PIN_PATTERN)) {
            response.sendRedirect("add_customer.html?error=invalid_pin");
            return;
        }

        // Parse balance
        double bal = 0.0;
        try {
            if (balStr != null && !balStr.trim().isEmpty()) {
                bal = Double.parseDouble(balStr);
                if (bal < 0) {
                    response.sendRedirect("add_customer.html?error=negative_balance");
                    return;
                }
            }
        } catch (NumberFormatException e) {
            response.sendRedirect("add_customer.html?error=invalid_balance");
            return;
        }

        // Hash the PIN using BCrypt (IMPORTANT: PINs are now hashed like passwords)
        String hashedPin = org.mindrot.jbcrypt.BCrypt.hashpw(pin, org.mindrot.jbcrypt.BCrypt.gensalt());

        try (Connection conn = DBConnection.getConnection()) {

            if (conn == null) {
                System.err.println("ERROR: Failed to get database connection in AddCustomerServlet");
                response.sendRedirect("add_customer.html?error=db");
                return;
            }

            String sql = "INSERT INTO customers " +
                    "(account_number, name, email, phone, address, pin, balance, account_type) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, accNo);
                ps.setString(2, name);
                ps.setString(3, email);
                ps.setString(4, phone);
                ps.setString(5, addr);
                ps.setString(6, hashedPin); // Store hashed PIN
                ps.setDouble(7, bal);
                ps.setString(8, type);

                // INSERT / UPDATE / DELETE → executeUpdate(), NOT executeQuery()
                int rows = ps.executeUpdate();

                if (rows > 0) {
                    System.out.println("Customer added successfully: " + accNo);
                    response.sendRedirect("add_customer.html?success=1");
                } else {
                    System.err.println("ERROR: Failed to insert customer: " + accNo);
                    response.sendRedirect("add_customer.html?error=insert_failed");
                }
            }

        } catch (SQLException e) {
            System.err.println("ERROR: Database error in AddCustomerServlet: " + e.getMessage());

            // Check for duplicate account number
            if (e.getMessage().contains("Duplicate entry")) {
                response.sendRedirect("add_customer.html?error=duplicate");
            } else {
                response.sendRedirect("add_customer.html?error=db");
            }
        }
    }
}

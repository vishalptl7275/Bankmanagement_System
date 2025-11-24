package bankapp;

/**
 * Application-wide constants for session attributes, validation patterns, and
 * messages.
 */
public class Constants {

    // Session Attributes
    public static final String SESSION_ADMIN_USER = "adminUser";

    // Validation Patterns
    public static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    public static final String PHONE_PATTERN = "^[0-9]{10}$";
    public static final String ACCOUNT_NUMBER_PATTERN = "^[0-9]{8,16}$";
    public static final String PIN_PATTERN = "^[0-9]{4,6}$";

    // Error Messages
    public static final String ERROR_DB_CONNECTION = "Unable to connect to database";
    public static final String ERROR_INVALID_EMAIL = "Invalid email format";
    public static final String ERROR_INVALID_PHONE = "Phone number must be 10 digits";
    public static final String ERROR_INVALID_ACCOUNT = "Account number must be 8-16 digits";
    public static final String ERROR_INVALID_PIN = "PIN must be 4-6 digits";
    public static final String ERROR_MISSING_FIELDS = "All fields are required";

    // Success Messages
    public static final String SUCCESS_CUSTOMER_ADDED = "Customer added successfully";
    public static final String SUCCESS_DEPOSIT = "Deposit successful";

    private Constants() {
        // Prevent instantiation
    }
}

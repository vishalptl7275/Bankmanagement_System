# 🏦 Bank Management System

A comprehensive web-based Bank Management System built with Java Servlets, JSP, and MySQL. This application provides a complete solution for managing bank operations including customer accounts, transactions, and administrative functions.

![Bank Management System](https://img.shields.io/badge/Java-17-orange) ![Maven](https://img.shields.io/badge/Maven-3.9.6-blue) ![MySQL](https://img.shields.io/badge/MySQL-8.0-blue) ![Jetty](https://img.shields.io/badge/Jetty-11.0.15-yellow)

## 📋 Table of Contents

- [Features](#features)
- [Technology Stack](#technology-stack)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Database Setup](#database-setup)
- [Running the Application](#running-the-application)
- [Project Structure](#project-structure)
- [Usage](#usage)
- [API Endpoints](#api-endpoints)
- [Security](#security)
- [Contributing](#contributing)
- [License](#license)

## ✨ Features

### Admin Features
- 🔐 Secure admin authentication
- 👥 Add new customer accounts
- 📊 View customer details and account information
- 💰 Manage customer balances
- 🔍 Search and filter customers

### Customer Operations
- 💵 **Deposit**: Add funds to customer accounts
- 💸 **Withdraw**: Withdraw funds from accounts
- 💳 **Transfer**: Transfer money between accounts
- 📈 **Check Balance**: View current account balance
- 📜 View transaction history

### Security Features
- 🔒 BCrypt password hashing
- 🛡️ Session management (30-minute timeout)
- 🚫 SQL injection prevention with prepared statements
- ✅ Input validation and sanitization

## 🛠️ Technology Stack

- **Backend**: Java 17, Jakarta Servlets 5.0
- **Build Tool**: Apache Maven 3.9.6
- **Database**: MySQL 8.0+
- **Server**: Jetty 11.0.15
- **Security**: BCrypt (jBCrypt 0.4)
- **Frontend**: HTML5, CSS3, JavaScript
- **JDBC Driver**: MySQL Connector/J 8.0.33

## 📦 Prerequisites

Before you begin, ensure you have the following installed:

- **Java Development Kit (JDK) 17 or higher**
  ```bash
  java -version
  ```

- **MySQL Server 8.0 or higher**
  - Download from: https://dev.mysql.com/downloads/mysql/

- **Apache Maven** (optional - included in project)
  - The project includes Maven 3.9.6 in the `apache-maven-3.9.6` directory

## 🚀 Installation

### 1. Clone the Repository

```bash
git clone <repository-url>
cd bankmanagementsystem
```

### 2. Verify Java Installation

```bash
java -version
# Should show Java 17 or higher
```

### 3. Install Maven (if not using included version)

**macOS (using Homebrew):**
```bash
brew install maven
```

**Linux:**
```bash
sudo apt-get install maven  # Debian/Ubuntu
sudo yum install maven      # RHEL/CentOS
```

**Windows:**
Download from https://maven.apache.org/download.cgi

## 🗄️ Database Setup

### 1. Start MySQL Server

Ensure your MySQL server is running:

```bash
# macOS
sudo /usr/local/mysql/support-files/mysql.server start

# Linux
sudo systemctl start mysql

# Windows
net start MySQL
```

### 2. Run Database Setup Script

The project includes an automated setup script:

```bash
chmod +x setup-database.sh
./setup-database.sh
```

This script will:
- ✅ Test your MySQL connection
- ✅ Create the `BankDB` database
- ✅ Create necessary tables (`admins`, `customers`)
- ✅ Generate `db.properties` with your credentials
- ✅ Create an admin user (optional)

### 3. Manual Database Setup (Alternative)

If you prefer manual setup:

```sql
CREATE DATABASE BankDB;
USE BankDB;

-- Create admins table
CREATE TABLE admins (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL
);

-- Create customers table
CREATE TABLE customers (
    id INT PRIMARY KEY AUTO_INCREMENT,
    account_number VARCHAR(20) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(15),
    address TEXT,
    pin VARCHAR(100) NOT NULL,
    balance DECIMAL(15,2) DEFAULT 0.00,
    account_type VARCHAR(20) DEFAULT 'Savings',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 4. Configure Database Connection

Create `src/main/resources/db.properties`:

```properties
db.url=jdbc:mysql://localhost:3306/BankDB
db.username=root
db.password=your_mysql_password
```

## 🏃 Running the Application

### Option 1: Using Included Maven

```bash
./apache-maven-3.9.6/bin/mvn clean jetty:run
```

### Option 2: Using System Maven

```bash
mvn clean jetty:run
```

### Option 3: Build WAR and Deploy

```bash
mvn clean package
# Deploy target/bankmanagementsystem-1.0-SNAPSHOT.war to your application server
```

### Access the Application

Once the server starts, access the application at:

🌐 **http://localhost:8080**

The default port is `8080`. You can change it in `pom.xml` under the Jetty plugin configuration.

## 📁 Project Structure

```
bankmanagementsystem/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── bankapp/
│   │   │   │   ├── AddCustomerServlet.java      # Add new customers
│   │   │   │   ├── CheckBalanceServlet.java     # Check account balance
│   │   │   │   ├── Constants.java               # Application constants
│   │   │   │   ├── CreateAdminUser.java         # Admin user creation utility
│   │   │   │   ├── DBConnection.java            # Database connection manager
│   │   │   │   ├── DepositServlet.java          # Deposit transactions
│   │   │   │   ├── LoginServlet.java            # Admin authentication
│   │   │   │   ├── LogoutServlet.java           # Session logout
│   │   │   │   ├── RegisterServlet.java         # User registration
│   │   │   │   ├── TransferServlet.java         # Money transfer
│   │   │   │   ├── ViewCustomerServlet.java     # View customer details
│   │   │   │   └── WithdrawServlet.java         # Withdrawal transactions
│   │   │   └── com/bank/
│   │   │       └── Main.java                    # Application entry point
│   │   ├── resources/
│   │   │   ├── db.properties                    # Database configuration
│   │   │   └── db.properties.example            # Example configuration
│   │   └── webapp/
│   │       ├── WEB-INF/
│   │       │   └── web.xml                      # Web application descriptor
│   │       ├── css/                             # Stylesheets
│   │       ├── js/                              # JavaScript files
│   │       ├── Dashboard.html                   # Admin dashboard
│   │       ├── add_customer.html                # Add customer form
│   │       ├── check_balance.html               # Balance inquiry
│   │       ├── deposit.html                     # Deposit form
│   │       ├── login.html                       # Login page
│   │       ├── register.html                    # Registration form
│   │       ├── transfer.html                    # Transfer form
│   │       ├── view_customer.html               # Customer details
│   │       └── withdraw.html                    # Withdrawal form
├── pom.xml                                      # Maven configuration
├── setup-database.sh                            # Database setup script
└── README.md                                    # This file
```

## 📖 Usage

### Admin Login

1. Navigate to http://localhost:8080
2. Enter admin credentials:
   - **Username**: `admin` (or your created username)
   - **Password**: `admin123` (default)

### Managing Customers

#### Add New Customer
1. Click "Add Customer" from the dashboard
2. Fill in customer details:
   - Account Number (unique)
   - Name
   - Email
   - Phone
   - Address
   - PIN (for customer authentication)
   - Initial Balance
   - Account Type (Savings/Current)
3. Submit the form

#### View Customer Details
1. Click "View Customer"
2. Enter account number or search criteria
3. View complete customer information

### Performing Transactions

#### Deposit
1. Select "Deposit" from the menu
2. Enter account number
3. Enter deposit amount
4. Confirm transaction

#### Withdraw
1. Select "Withdraw"
2. Enter account number and PIN
3. Enter withdrawal amount
4. Confirm transaction

#### Transfer
1. Select "Transfer"
2. Enter source account number and PIN
3. Enter destination account number
4. Enter transfer amount
5. Confirm transaction

#### Check Balance
1. Select "Check Balance"
2. Enter account number
3. View current balance and account details

## 🔌 API Endpoints

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/login` | POST | Admin authentication |
| `/logout` | GET | End admin session |
| `/register` | POST | Register new user |
| `/add-customer` | POST | Add new customer account |
| `/view-customer` | GET | Retrieve customer details |
| `/deposit` | POST | Process deposit transaction |
| `/withdraw` | POST | Process withdrawal transaction |
| `/transfer` | POST | Process money transfer |
| `/check-balance` | GET | Check account balance |

## 🔒 Security

### Password Security
- Admin passwords are hashed using **BCrypt** with a cost factor of 10
- Customer PINs are securely stored and validated
- No plain-text passwords are stored in the database

### Session Management
- Sessions expire after 30 minutes of inactivity
- Secure session handling prevents unauthorized access
- Logout functionality clears all session data

### SQL Injection Prevention
- All database queries use **PreparedStatements**
- Input validation on all user inputs
- Parameterized queries prevent SQL injection attacks

### Best Practices
- ✅ Input sanitization
- ✅ Error handling without exposing sensitive information
- ✅ Secure database connection management
- ✅ Session-based authentication

## 🐛 Troubleshooting

### Database Connection Issues

**Error**: `Cannot connect to database`

**Solution**:
1. Verify MySQL is running
2. Check `db.properties` credentials
3. Ensure `BankDB` database exists
4. Verify MySQL is listening on port 3306

### Maven Build Errors

**Error**: `mvn: command not found`

**Solution**:
```bash
# Use included Maven
./apache-maven-3.9.6/bin/mvn clean jetty:run
```

### Port Already in Use

**Error**: `Address already in use: 8080`

**Solution**:
1. Change port in `pom.xml`:
```xml
<httpConnector>
    <port>9090</port>  <!-- Change to available port -->
</httpConnector>
```

2. Or kill the process using port 8080:
```bash
# macOS/Linux
lsof -ti:8080 | xargs kill -9

# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 👥 Authors

- Your Name - Initial work

## 🙏 Acknowledgments

- Jakarta EE for Servlet API
- MySQL for database management
- Eclipse Jetty for embedded server
- jBCrypt for password hashing

---

**Note**: This is a educational/demonstration project. For production use, consider additional security measures, comprehensive testing, and compliance with banking regulations.

## 📞 Support

For issues and questions:
- Create an issue in the repository
- Contact: your.email@example.com

---

Made with ❤️ using Java and MySQL

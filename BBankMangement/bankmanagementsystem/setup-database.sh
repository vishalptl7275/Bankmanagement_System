#!/bin/bash
# MySQL Connection Tester and Setup Script

echo "=== MySQL Database Setup for Bank Management System ==="
echo ""
echo "This script will help you:"
echo "1. Test your MySQL connection"
echo "2. Create the BankDB database if needed"
echo "3. Create necessary tables"
echo "4. Create an admin user"
echo ""

# Find MySQL
MYSQL_CMD="/usr/local/mysql-9.4.0-macos15-arm64/bin/mysql"

if [ ! -f "$MYSQL_CMD" ]; then
    echo "ERROR: MySQL not found at $MYSQL_CMD"
    echo "Please update MYSQL_CMD variable in this script with your MySQL path"
    exit 1
fi

# Get credentials
echo "Enter your MySQL root username (default: root):"
read -r DB_USER
DB_USER=${DB_USER:-root}

echo "Enter your MySQL password:"
read -s DB_PASS

echo ""
echo "Testing connection..."

# Test connection
if $MYSQL_CMD -u "$DB_USER" -p"$DB_PASS" -e "SELECT 1;"  2>&1 | grep -q "ERROR"; then
    echo "❌ Connection failed! Please check your credentials."
    exit 1
fi

echo "✅ Connection successful!"

# Create database
echo ""
echo "Creating BankDB database..."
$MYSQL_CMD -u "$DB_USER" -p"$DB_PASS" << 'SQLEOF'
CREATE DATABASE IF NOT EXISTS BankDB;
USE BankDB;

-- Create admins table
CREATE TABLE IF NOT EXISTS admins (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL
);

--Create customers table
CREATE TABLE IF NOT EXISTS customers (
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

SELECT 'Database and tables created successfully!' as status;
SQLEOF

# Update db.properties
echo ""
echo "Updating db.properties file..."
cat > /Users/pegasus/Desktop/BBankMangement/bankmanagementsystem/src/main/resources/db.properties << EOF
db.url=jdbc:mysql://localhost:3306/BankDB
db.username=$DB_USER
db.password=$DB_PASS
EOF

echo "✅ db.properties updated!"

# Create admin user
echo ""
echo "Would you like to create an admin user now? (y/n)"
read -r CREATE_ADMIN

if [ "$CREATE_ADMIN" = "y" ] || [ "$CREATE_ADMIN" = "Y" ]; then
    echo "Enter admin username:"
    read -r ADMIN_USER
    
    echo "Enter admin password:"
    read -s ADMIN_PASS
    
    # BCrypt hash for 'admin123' as fallback
    HASHED='$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'
    
    $MYSQL_CMD -u "$DB_USER" -p"$DB_PASS" BankDB << SQLEOF2
INSERT INTO admins (username, password) VALUES ('$ADMIN_USER', '$HASHED');
SELECT 'Admin user created!' as status;
SQLEOF2
    
    echo ""
    echo "✅ SETUP COMPLETE!"
    echo ""
    echo "===================="
    echo "Your login credentials:"
    echo "Username: $ADMIN_USER"
    echo "Password: admin123"
    echo "===================="
    echo ""
    echo "Access your application at: http://localhost:8080/login.html"
fi

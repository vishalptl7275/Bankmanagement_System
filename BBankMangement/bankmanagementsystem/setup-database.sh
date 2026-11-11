#!/usr/bin/env bash
set -Eeuo pipefail

echo "=== Bank Management System: database setup ==="
echo

MYSQL_CMD="${MYSQL_CMD:-$(command -v mysql || true)}"
if [[ -z "$MYSQL_CMD" ]]; then
  echo "Error: MySQL client 'mysql' was not found."
  echo "Install MySQL and ensure the mysql command is available on PATH."
  exit 1
fi

read -r -p "MySQL administrator username [root]: " DB_USER
DB_USER="${DB_USER:-root}"
read -r -s -p "MySQL password: " DB_PASS
echo

# Use a temporary, permission-restricted MySQL option file so the password
# is not placed in the command-line arguments.
MYSQL_CNF="$(mktemp)"
chmod 600 "$MYSQL_CNF"
cleanup() {
  rm -f "$MYSQL_CNF"
}
trap cleanup EXIT

escaped_pass="${DB_PASS//\\/\\\\}"
escaped_pass="${escaped_pass//\"/\\\"}"
{
  printf '[client]\nuser="%s"\npassword="%s"\n' "$DB_USER" "$escaped_pass"
} > "$MYSQL_CNF"
unset DB_PASS

if ! "$MYSQL_CMD" --defaults-extra-file="$MYSQL_CNF" -e "SELECT 1;" >/dev/null 2>&1; then
  echo "Error: Could not connect to MySQL. Check the username, password and server status."
  exit 1
fi

echo "Creating BankDB and required tables..."
"$MYSQL_CMD" --defaults-extra-file="$MYSQL_CNF" <<'SQL'
CREATE DATABASE IF NOT EXISTS BankDB;
USE BankDB;

CREATE TABLE IF NOT EXISTS admins (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL
);

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
SQL

echo
echo "Database and tables are ready."
echo "Next: configure src/main/resources/db.properties and create an admin with:"
echo "  mvn compile dependency:build-classpath -Dmdep.outputFile=target/classpath.txt"
echo '  java -cp "target/classes:$(cat target/classpath.txt)" bankapp.CreateAdminUser'

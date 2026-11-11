# Bank Management System

A Java-based web application for managing bank customers and common account operations. The project uses Java Servlets, JDBC and MySQL, with a browser-based interface.

## Features

- Admin registration and login with BCrypt password hashing
- Customer account creation and customer listing
- Balance inquiry
- Deposits and withdrawals
- Transfers between accounts
- MySQL persistence through JDBC

## Technology

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Web | Jakarta Servlets 5 |
| Database | MySQL |
| Data access | JDBC |
| Authentication | BCrypt |
| Build | Maven |
| Servlet container | Jetty 11 |

## Project structure

```text
BBankMangement/bankmanagementsystem/
├── src/main/java/bankapp/       # Servlets and application classes
├── src/main/resources/          # Database configuration
├── src/main/webapp/             # Web pages and web configuration
├── setup-database.sh            # Creates the database schema
└── pom.xml                      # Maven configuration
```

## Run locally

### Requirements

- JDK 17
- MySQL 8 or later
- Maven 3.8+

### 1. Open the application directory

```bash
cd BBankMangement/bankmanagementsystem
```

### 2. Configure the database

Create your local configuration from the example:

```bash
cp src/main/resources/db.properties.example src/main/resources/db.properties
```

Edit `src/main/resources/db.properties` with your local MySQL connection details:

```properties
db.url=jdbc:mysql://localhost:3306/BankDB
db.username=your_database_username
db.password=your_database_password
```

Keep this file private. It is intentionally excluded from version control.

### 3. Create the database tables

```bash
chmod +x setup-database.sh
./setup-database.sh
```

The script asks for a MySQL account that has permission to create the database and tables. It does not create a default admin account.

### 4. Create an admin account

Compile the application and build its dependency classpath:

```bash
mvn compile dependency:build-classpath -Dmdep.outputFile=target/classpath.txt
java -cp "target/classes:$(cat target/classpath.txt)" bankapp.CreateAdminUser
```

Choose your own admin username and password when prompted.

### 5. Start the application

```bash
mvn jetty:run
```

Open [http://localhost:8080](http://localhost:8080).

## Security notes

- Admin passwords are stored using BCrypt hashing.
- Database operations use JDBC; parameterized queries are used in the authentication and admin-creation flows.
- Do not use real banking credentials or personal financial data when testing this academic project.
- Use a private local database and unique credentials.
- This project is an educational demonstration, not production banking software.

## Current scope and limitations

This repository demonstrates core banking workflows for learning Java web development and database integration. It has not been independently audited or certified for real financial use.

## Author

Vishal Patel

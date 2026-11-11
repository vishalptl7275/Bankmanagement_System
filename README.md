# Bank Management System

A Java 17 web application for managing customers and common account operations. It uses Jakarta Servlets, JDBC, MySQL and a browser-based interface.

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
| Server | Jetty 11 |
| Database | MySQL 8.4 |
| Data access | JDBC |
| Authentication | BCrypt |
| Build / deployment | Maven, Docker |

## Run with Docker (recommended)

Requirements: Docker Desktop with Docker Compose.

1. Clone the repository and open its root directory.
2. Create your private environment file:

   ```bash
   cp .env.example .env
   ```

3. Edit `.env` and set two different strong random passwords for `DB_PASSWORD` and `MYSQL_ROOT_PASSWORD`.
4. Build and start the application:

   ```bash
   docker compose up --build -d
   ```

5. Open [http://localhost:8080](http://localhost:8080).
6. Register the first admin at `/register.html`, then sign in.

The Compose stack starts MySQL, waits for its health check, initializes the `BankDB` schema from `database/init.sql`, and then starts Jetty. Database data persists in the `bank_data` Docker volume.

To stop the services:

```bash
docker compose down
```

To stop and delete the local database volume (this permanently deletes local test data):

```bash
docker compose down -v
```

Never commit `.env` or publish database passwords.

## Run locally without Docker

Requirements: JDK 17, Maven 3.8+ and MySQL 8+.

1. Enter the application directory:

   ```bash
   cd BBankMangement/bankmanagementsystem
   ```

2. Create `src/main/resources/db.properties` from `db.properties.example` and fill in your local MySQL details. Alternatively set `DB_URL`, `DB_USERNAME` and `DB_PASSWORD` environment variables.
3. Create the schema using the root-level `database/init.sql` or run the included `setup-database.sh` script with a MySQL administrator.
4. Start the application:

   ```bash
   mvn clean package
   mvn jetty:run
   ```

5. Open [http://localhost:8080](http://localhost:8080), register an admin and log in.

## Project structure

```text
.
├── Dockerfile
├── docker-compose.yml
├── database/init.sql
└── BBankMangement/bankmanagementsystem/
    ├── pom.xml
    └── src/main/
        ├── java/bankapp/
        ├── resources/db.properties.example
        └── webapp/
```

## CI

GitHub Actions builds the Java 17 WAR on pushes and pull requests. The workflow checks compilation and packaging; it does not prove that a hosted database or live deployment is configured.

## Security and scope

This is an educational project, not production banking software. Do not use real financial data. Before any public production deployment, add CSRF protection, tighten admin registration, audit authorization and session handling, use HTTPS, and review transaction concurrency and input validation. Use unique secrets and restrict database network access.

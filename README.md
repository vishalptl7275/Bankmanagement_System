# 🏦 Bank Management System

A web-based banking application built with Java Servlets and MySQL.

## 🚀 Quick Start

### Prerequisites
- Java 17+
- MySQL 8.0+

### Setup & Run

1. **Setup Database**
```bash
chmod +x setup-database.sh
./setup-database.sh
```

2. **Run Application**
```bash
./apache-maven-3.9.6/bin/mvn clean jetty:run
```

3. **Access**
- URL: http://localhost:8080
- Default Login: `admin` / `admin123`

## ✨ Features

- � Customer account management
- � Deposit & withdraw funds
- 💳 Money transfers
- 📈 Balance inquiry
- 🔐 Secure authentication (BCrypt)

## 🛠️ Tech Stack

- **Backend**: Java 17, Jakarta Servlets 5.0
- **Database**: MySQL 8.0
- **Server**: Jetty 11.0.15
- **Build**: Maven 3.9.6
- **Security**: BCrypt password hashing

## 📁 Project Structure

```
bankmanagementsystem/
├── src/main/java/bankapp/          # Servlets
├── src/main/webapp/                # HTML pages
├── src/main/resources/             # Config files
├── pom.xml                         # Maven config
└── setup-database.sh               # DB setup script
```

## � Security

- BCrypt password hashing
- SQL injection prevention (PreparedStatements)
- Session timeout (30 min)
- Input validation

## 📖 Usage

### Add Customer
Dashboard → Add Customer → Fill form → Submit

### Deposit
Dashboard → Deposit → Enter account & amount → Confirm

### Withdraw
Dashboard → Withdraw → Enter account, PIN & amount → Confirm

### Transfer
Dashboard → Transfer → Enter from/to accounts, PIN & amount → Confirm

### Check Balance
Dashboard → Check Balance → Enter account number → View

## 🐛 Troubleshooting

**Database connection error?**
- Check MySQL is running
- Verify `src/main/resources/db.properties`

**Port 8080 in use?**
```bash
lsof -ti:8080 | xargs kill -9
```

---

Made with ❤️ using Java & MySQL

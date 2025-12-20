# 🎓 Online Learning Management System (LMS)

A comprehensive Learning Management System built with Java Swing for the desktop GUI, JDBC for database connectivity, and MySQL for data persistence. This system supports three user roles: **Admin**, **Instructor**, and **Student**, each with dedicated dashboards and functionalities.

## 📋 Table of Contents

- [Features](#-features)
- [Prerequisites](#-prerequisites)
- [Installation](#-installation)
- [Database Setup](#-database-setup)
- [Running the Application](#-running-the-application)
- [Default Login Credentials](#-default-login-credentials)
- [Project Structure](#-project-structure)
- [Technologies Used](#-technologies-used)
- [Troubleshooting](#-troubleshooting)

## ✨ Features

### 👨‍💼 Admin Dashboard
- Manage users (Students, Instructors, Admins)
- Manage courses
- View system-wide analytics
- Monitor enrollments

### 👨‍🏫 Instructor Dashboard
- Create and manage courses
- View enrolled students
- Track student performance
- Manage course content

### 👨‍🎓 Student Dashboard
- Browse available courses
- Enroll in courses
- View enrolled courses
- Track learning progress

## 📦 Prerequisites

Before running this application, ensure you have the following installed:

### Required Software

1. **Java Development Kit (JDK)**
   - Version: JDK 8 or higher
   - Download: [Oracle JDK](https://www.oracle.com/java/technologies/downloads/) or [OpenJDK](https://openjdk.org/)
   - Verify installation:
     ```bash
     java -version
     javac -version
     ```

2. **MySQL Server**
   - Version: MySQL 5.7 or higher (MySQL 8.0 recommended)
   - Download: [MySQL Community Server](https://dev.mysql.com/downloads/mysql/)
   - Verify installation:
     ```bash
     mysql --version
     ```

3. **MySQL JDBC Driver**
   - The project uses MySQL Connector/J.
   - Ensure you have the `mysql-connector-java-x.x.x.jar` in the `lib/` directory.

## 🚀 Installation

### 1. Clone the Repository

```bash
git clone https://github.com/aditya77ap/Online-Learning-Management-System-LMS-.git
cd Online-Learning-Management-System-LMS-
```

### 2. Set Up MySQL JDBC Driver

- Download the MySQL Connector/J JAR file.
- Create a `lib` directory in the project root (if it doesn't exist).
- Place the `mysql-connector-java-x.x.x.jar` file in the `lib/` directory.

```
Project LMS/
├── lib/
│   └── mysql-connector-java-8.0.33.jar  (or your version)
├── src/
├── database.sql
└── run.bat
```

### 3. Configure Database Credentials

The project uses a `config.properties` file for database configuration. 

**File: `config.properties`**
```properties
db.url=jdbc:mysql://localhost:3306/lms_db
db.user=root
db.password=YOUR_PASSWORD_HERE
```

> **Note**: Update `db.password` with your local MySQL root password.

## 🗄️ Database Setup

### Option 1: Automated Setup (Windows)

Run the provided batch script to automatically set up the database. 

**Important**: Open `reset_database.bat` and ensure the password in the command matches your MySQL password if it differs from the default.

```batch
reset_database.bat
```

This script will:
- Drop the existing `lms_db` database (if it exists)
- Create a new `lms_db` database
- Execute the `database.sql` script to create tables and insert sample data

### Option 2: Manual Setup

1. Open MySQL command line or MySQL Workbench.
2. Run the following commands:

```sql
DROP DATABASE IF EXISTS lms_db;
CREATE DATABASE lms_db;
USE lms_db;
SOURCE 'path/to/database.sql';
```

## ▶️ Running the Application

### Windows

Simply double-click the `run.bat` file or run it from the command prompt:

```bash
run.bat
```

This script will:
1. Compile all Java source files.
2. Create the `bin` directory for compiled classes.
3. Launch the application.

### Linux/Mac

Create and run a shell script `run.sh`:

```bash
#!/bin/bash
echo "Compiling Project..."
mkdir -p bin
javac -d bin -sourcepath src -cp "lib/*" src/com/lms/main/Main.java src/com/lms/util/*.java src/com/lms/model/*.java src/com/lms/dao/*.java src/com/lms/ui/*.java

if [ $? -eq 0 ]; then
    echo "Running LMS Application..."
    java -cp "bin:lib/*" com.lms.main.Main
else
    echo "Compilation Failed!"
fi
```

Make it executable:
```bash
chmod +x run.sh
./run.sh
```

## 🔐 Default Login Credentials

After setting up the database, you can log in with these default credentials:

| Role | Email | Password |
|------|-------|----------|
| **Admin** | admin@lms.com | admin123 |
| **Instructor** | instructor@lms.com | instructor123 |
| **Student** | student@lms.com | student123 |

> ⚠️ **Security Note**: Change these default passwords in a production environment!

## 📁 Project Structure

```
Online-Learning-Management-System-LMS/
│
├── src/
│   └── com/
│       └── lms/
│           ├── dao/              # Data Access Objects
│           ├── model/            # Data Models
│           ├── ui/               # User Interface (Swing)
│           ├── util/             # Utilities (DBConnection)
│           └── main/             # Main Entry Point
│
├── lib/                          # External Libraries (JDBC Driver)
├── bin/                          # Compiled Classes (generated)
├── config.properties             # Database Configuration
├── database.sql                  # Database Schema & Sample Data
├── run.bat                       # Windows Run Script
├── reset_database.bat            # Database Reset Script
├── .gitignore                    # Git Ignore File
└── README.md                     # This File
```

## 🛠️ Technologies Used

- **Java SE**: Core programming language
- **Java Swing**: Desktop GUI framework
- **JDBC**: Database connectivity
- **MySQL**: Relational database management system

## 🐛 Troubleshooting

### Issue: "MySQL JDBC Driver not found"
**Solution**: Ensure the MySQL Connector/J JAR file is in the `lib/` directory and properly referenced in the classpath.

### Issue: "Access denied for user 'root'@'localhost'"
**Solution**: 
- Update `config.properties` with your correct MySQL password.
- If using `reset_database.bat`, update the password inside the script as well.

### Issue: "Database 'lms_db' doesn't exist"
**Solution**: Run `reset_database.bat` or manually import `database.sql`.

## 📝 License

This project is open source and available under the [MIT License](LICENSE).

## � Contact

**Author**: Aditya Prakash
**GitHub**: [aditya77ap](https://github.com/aditya77ap)
**Email**: adityaprakash9199a@gmail.com

package com.lms.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DBConnection {
    private static Properties props = new Properties();
    private static boolean initialized = false;

    static {
        try (FileInputStream fis = new FileInputStream("config.properties")) {
            props.load(fis);
        } catch (IOException e) {
            System.err.println("WARNING: Could not load config.properties. Using default settings.");
            props.setProperty("db.url", "jdbc:mysql://localhost:3306/lms_db");
            props.setProperty("db.user", "root");
            props.setProperty("db.password", "password");
        }
    }

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Initialize database on first connection
            if (!initialized) {
                initializeDatabase();
                initialized = true;
            }

            return DriverManager.getConnection(
                    props.getProperty("db.url"),
                    props.getProperty("db.user"),
                    props.getProperty("db.password"));
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found. Please add the MySQL Connector jar to the lib folder.",
                    e);
        }
    }

    private static void initializeDatabase() {
        System.out.println("Checking database initialization...");

        String url = props.getProperty("db.url");
        String baseUrl = url.substring(0, url.lastIndexOf("/"));
        String dbName = url.substring(url.lastIndexOf("/") + 1);
        String user = props.getProperty("db.user");
        String password = props.getProperty("db.password");

        try (Connection conn = DriverManager.getConnection(baseUrl + "/", user, password);
                Statement stmt = conn.createStatement()) {

            // Create database if it doesn't exist
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + dbName);
            System.out.println("✓ Database '" + dbName + "' ready");

            // Use the database
            stmt.executeUpdate("USE " + dbName);

            // Create users table if it doesn't exist
            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS users (" +
                            "id INT AUTO_INCREMENT PRIMARY KEY, " +
                            "name VARCHAR(100) NOT NULL, " +
                            "email VARCHAR(100) UNIQUE NOT NULL, " +
                            "password VARCHAR(100) NOT NULL, " +
                            "role ENUM('Admin', 'Instructor', 'Student') NOT NULL)");
            System.out.println("✓ Table 'users' ready");

            // Create courses table if it doesn't exist
            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS courses (" +
                            "id INT AUTO_INCREMENT PRIMARY KEY, " +
                            "course_name VARCHAR(200) NOT NULL, " +
                            "description TEXT, " +
                            "instructor_id INT, " +
                            "FOREIGN KEY (instructor_id) REFERENCES users(id) ON DELETE SET NULL)");
            System.out.println("✓ Table 'courses' ready");

            // Create enrollments table if it doesn't exist
            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS enrollments (" +
                            "id INT AUTO_INCREMENT PRIMARY KEY, " +
                            "student_id INT, " +
                            "course_id INT, " +
                            "enrollment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                            "FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE, " +
                            "FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE)");
            System.out.println("✓ Table 'enrollments' ready");

            // Create attendance_records table if it doesn't exist
            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS attendance_records (" +
                            "id INT AUTO_INCREMENT PRIMARY KEY, " +
                            "student_id INT, " +
                            "course_id INT, " +
                            "date DATE NOT NULL, " +
                            "status ENUM('Present', 'Absent') NOT NULL, " +
                            "FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE, " +
                            "FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE)");
            System.out.println("✓ Table 'attendance_records' ready");

            // Create active_sessions table if it doesn't exist
            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS active_sessions (" +
                            "id INT AUTO_INCREMENT PRIMARY KEY, " +
                            "course_id INT, " +
                            "token VARCHAR(10) NOT NULL, " +
                            "generated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                            "FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE)");
            System.out.println("✓ Table 'active_sessions' ready");

            // Check if test users exist
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM users");
            rs.next();
            int userCount = rs.getInt("count");

            if (userCount == 0) {
                System.out.println("Initializing test users...");

                // Insert test users with Indian names
                stmt.executeUpdate(
                        "INSERT INTO users (name, email, password, role) VALUES " +
                                "('Rajesh Kumar', 'admin@lms.com', 'admin123', 'Admin'), " +
                                "('Priya Sharma', 'instructor@lms.com', 'instructor123', 'Instructor'), " +
                                "('Arjun Patel', 'student@lms.com', 'student123', 'Student')");
                System.out.println("✓ Test users created");

                // Insert test courses
                stmt.executeUpdate(
                        "INSERT INTO courses (course_name, description, instructor_id) VALUES " +
                                "('Java Programming', 'Learn Core Java and OOPs', 2), " +
                                "('Database Management', 'SQL and Database Design', 2)");
                System.out.println("✓ Test courses created");

                // Insert test enrollment
                stmt.executeUpdate("INSERT INTO enrollments (student_id, course_id) VALUES (3, 1)");
                System.out.println("✓ Test enrollment created");

                System.out.println("\n=== Database Initialized Successfully ===");
                System.out.println("You can now login with:");
                System.out.println("  Admin: admin@lms.com / admin123");
                System.out.println("  Instructor: instructor@lms.com / instructor123");
                System.out.println("  Student: student@lms.com / student123");
                System.out.println("=========================================\n");
            } else {
                System.out.println("✓ Database already initialized with " + userCount + " users");
            }

        } catch (SQLException e) {
            System.err.println("ERROR: Failed to initialize database!");
            System.err.println("Please check:");
            System.err.println("  1. MySQL is running");
            System.err.println("  2. Credentials in config.properties are correct");
            System.err.println("\nError details: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

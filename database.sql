CREATE DATABASE IF NOT EXISTS lms_db;
USE lms_db;

-- Users Table
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    role ENUM('Admin', 'Instructor', 'Student') NOT NULL
);

-- Courses Table
CREATE TABLE IF NOT EXISTS courses (
    id INT AUTO_INCREMENT PRIMARY KEY,
    course_name VARCHAR(200) NOT NULL,
    description TEXT,
    instructor_id INT,
    FOREIGN KEY (instructor_id) REFERENCES users(id) ON DELETE SET NULL
);

-- Enrollments Table
CREATE TABLE IF NOT EXISTS enrollments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT,
    course_id INT,
    enrollment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE
);

-- Insert Dummy Data
INSERT INTO users (name, email, password, role) VALUES 
('Admin User', 'admin@lms.com', 'admin123', 'Admin'),
('John Instructor', 'instructor@lms.com', 'instructor123', 'Instructor'),
('Alice Student', 'student@lms.com', 'student123', 'Student');

INSERT INTO courses (course_name, description, instructor_id) VALUES 
('Java Programming', 'Learn Core Java and OOPs', 2),
('Database Management', 'SQL and Database Design', 2);

INSERT INTO enrollments (student_id, course_id) VALUES 
(3, 1);

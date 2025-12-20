CREATE DATABASE IF NOT EXISTS lms_db;
USE lms_db;

-- Users Table
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    enrollment_no VARCHAR(50) UNIQUE,
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
INSERT INTO users (name, email, enrollment_no, password, role) VALUES 
('Rajesh Kumar', 'rajeshkumar@lms.com', 'ADMIN001', 'admin123', 'Admin'),
('Priya Sharma', 'priyasharma@lms.com', 'INST101', 'instructor123', 'Instructor'),
('Arjun Patel', 'arjunpatel@lms.com', '24SCSE10110', 'student123', 'Student'),
('Amit Verma', 'amitverma@lms.com', 'ADMIN002', 'admin123', 'Admin'),
('Anjali Gupta', 'anjaligupta@lms.com', 'INST102', 'instructor123', 'Instructor'),
('Rohan Singh', 'rohansingh@lms.com', '24SCSE10111', 'student123', 'Student'),
('Vikram Singh', 'vikramsingh@lms.com', 'INST103', 'instructor123', 'Instructor'),
('Neha Kapoor', 'nehakapoor@lms.com', 'INST104', 'instructor123', 'Instructor'),
('Rahul Malhotra', 'rahulmalhotra@lms.com', 'INST105', 'instructor123', 'Instructor');

INSERT INTO courses (course_name, description, instructor_id) VALUES 
('Java Programming', 'Learn Core Java and OOPs', 2),
('Database Management', 'SQL and Database Design', 2),
('Web Development Bootcamp', 'Full Stack Web Development with HTML, CSS, JS', 7),
('Data Science with Python', 'Analyze data and build models with Python', 8),
('Machine Learning Fundamentals', 'Introduction to ML algorithms', 8),
('Digital Marketing 101', 'SEO, SEM, and Social Media Marketing', 9);

INSERT INTO enrollments (student_id, course_id) VALUES 
(3, 1);

-- Attendance Records Table
CREATE TABLE IF NOT EXISTS attendance_records (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT,
    course_id INT,
    date DATE NOT NULL,
    status ENUM('Present', 'Absent') NOT NULL,
    FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE
);

-- Active Sessions Table (for Dynamic QR/Token)
CREATE TABLE IF NOT EXISTS active_sessions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    course_id INT,
    token VARCHAR(10) NOT NULL,
    generated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE
);

-- Dummy Attendance Data

-- Dummy Attendance Data
-- Enroll Rohan in Java
INSERT INTO enrollments (student_id, course_id) VALUES (6, 1); 

-- Enroll Arjun (ID 3) in all other courses
INSERT INTO enrollments (student_id, course_id) VALUES (3, 2), (3, 3), (3, 4), (3, 5), (3, 6);

-- Attendance for Arjun (ID 3)
INSERT INTO attendance_records (student_id, course_id, date, status) VALUES 
-- Course 1: Java (Mixed)
(3, 1, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 'Present'),
(3, 1, DATE_SUB(CURDATE(), INTERVAL 2 DAY), 'Present'),
(3, 1, DATE_SUB(CURDATE(), INTERVAL 3 DAY), 'Absent'),
(3, 1, DATE_SUB(CURDATE(), INTERVAL 4 DAY), 'Present'),

-- Course 2: DBMS (High Attendance)
(3, 2, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 'Present'),
(3, 2, DATE_SUB(CURDATE(), INTERVAL 2 DAY), 'Present'),
(3, 2, DATE_SUB(CURDATE(), INTERVAL 3 DAY), 'Present'),

-- Course 3: Web Dev (Perfect Attendance)
(3, 3, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 'Present'),
(3, 3, DATE_SUB(CURDATE(), INTERVAL 2 DAY), 'Present'),
(3, 3, DATE_SUB(CURDATE(), INTERVAL 3 DAY), 'Present'),
(3, 3, DATE_SUB(CURDATE(), INTERVAL 4 DAY), 'Present'),

-- Course 4: Data Science (Low Attendance)
(3, 4, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 'Absent'),
(3, 4, DATE_SUB(CURDATE(), INTERVAL 2 DAY), 'Absent'),
(3, 4, DATE_SUB(CURDATE(), INTERVAL 3 DAY), 'Present'),

-- Course 5: ML (Mixed)
(3, 5, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 'Present'),
(3, 5, DATE_SUB(CURDATE(), INTERVAL 2 DAY), 'Absent'),
(3, 5, DATE_SUB(CURDATE(), INTERVAL 3 DAY), 'Present'),

-- Course 6: Digital Marketing (Recent Absences)
(3, 6, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 'Absent'),
(3, 6, DATE_SUB(CURDATE(), INTERVAL 2 DAY), 'Absent'),
(3, 6, DATE_SUB(CURDATE(), INTERVAL 3 DAY), 'Absent');

-- Attendance for other students
INSERT INTO attendance_records (student_id, course_id, date, status) VALUES 
(6, 1, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 'Absent'),
(6, 1, DATE_SUB(CURDATE(), INTERVAL 2 DAY), 'Present');

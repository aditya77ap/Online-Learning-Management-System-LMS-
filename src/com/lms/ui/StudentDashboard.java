package com.lms.ui;

import com.lms.dao.CourseDAO;
import com.lms.model.Course;
import com.lms.model.User;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StudentDashboard extends JFrame {
    private User currentUser;
    private CourseDAO courseDAO;
    private JTable enrolledTable;
    private DefaultTableModel tableModel;

    public StudentDashboard(User user) {
        this.currentUser = user;
        this.courseDAO = new CourseDAO();

        setTitle("Student Dashboard - Welcome " + user.getName());
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Header
        JLabel headerLabel = new JLabel("My Enrolled Courses", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(headerLabel, BorderLayout.NORTH);

        // Center - Enrolled Courses List
        String[] columnNames = { "ID", "Course Name", "Description", "Instructor ID" };
        tableModel = new DefaultTableModel(columnNames, 0);
        enrolledTable = new JTable(tableModel);
        loadEnrolledCourses();
        add(new JScrollPane(enrolledTable), BorderLayout.CENTER);

        // Footer - Buttons
        JPanel buttonPanel = new JPanel();
        JButton enrollButton = new JButton("Enroll in New Course");
        enrollButton.addActionListener(e -> showEnrollDialog());

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });

        buttonPanel.add(enrollButton);
        buttonPanel.add(logoutButton);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void loadEnrolledCourses() {
        tableModel.setRowCount(0);
        List<Course> courses = courseDAO.getEnrolledCourses(currentUser.getId());
        for (Course c : courses) {
            tableModel.addRow(new Object[] { c.getId(), c.getCourseName(), c.getDescription(), c.getInstructorId() });
        }
    }

    private void showEnrollDialog() {
        List<Course> allCourses = courseDAO.getAllCourses();
        String[] courseNames = allCourses.stream().map(c -> c.getId() + ": " + c.getCourseName())
                .toArray(String[]::new);

        if (courseNames.length == 0) {
            JOptionPane.showMessageDialog(this, "No courses available to enroll.");
            return;
        }

        String selected = (String) JOptionPane.showInputDialog(this, "Select a course to enroll:",
                "Enrollment", JOptionPane.QUESTION_MESSAGE, null, courseNames, courseNames[0]);

        if (selected != null) {
            int courseId = Integer.parseInt(selected.split(":")[0]);
            if (courseDAO.enrollStudent(currentUser.getId(), courseId)) {
                JOptionPane.showMessageDialog(this, "Enrolled Successfully!");
                loadEnrolledCourses();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to Enroll (Maybe already enrolled?)", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

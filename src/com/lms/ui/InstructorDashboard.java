package com.lms.ui;

import com.lms.dao.CourseDAO;
import com.lms.model.Course;
import com.lms.model.User;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class InstructorDashboard extends JFrame {
    private User currentUser;
    private CourseDAO courseDAO;
    private JTable courseTable;
    private DefaultTableModel tableModel;

    public InstructorDashboard(User user) {
        this.currentUser = user;
        this.courseDAO = new CourseDAO();

        setTitle("Instructor Dashboard - Welcome " + user.getName());
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Header
        JLabel headerLabel = new JLabel("My Courses", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(headerLabel, BorderLayout.NORTH);

        // Center - Course List
        String[] columnNames = { "ID", "Course Name", "Description" };
        tableModel = new DefaultTableModel(columnNames, 0);
        courseTable = new JTable(tableModel);
        loadCourses();
        add(new JScrollPane(courseTable), BorderLayout.CENTER);

        // Footer - Buttons
        JPanel buttonPanel = new JPanel();
        JButton addCourseButton = new JButton("Add Course");
        addCourseButton.addActionListener(e -> showAddCourseDialog());

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });

        buttonPanel.add(addCourseButton);
        buttonPanel.add(logoutButton);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void loadCourses() {
        tableModel.setRowCount(0);
        List<Course> courses = courseDAO.getCoursesByInstructor(currentUser.getId());
        for (Course c : courses) {
            tableModel.addRow(new Object[] { c.getId(), c.getCourseName(), c.getDescription() });
        }
    }

    private void showAddCourseDialog() {
        JTextField nameField = new JTextField();
        JTextField descField = new JTextField();
        Object[] message = {
                "Course Name:", nameField,
                "Description:", descField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Add New Course", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            Course newCourse = new Course(0, nameField.getText(), descField.getText(), currentUser.getId());
            if (courseDAO.addCourse(newCourse)) {
                JOptionPane.showMessageDialog(this, "Course Added Successfully!");
                loadCourses();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to Add Course", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

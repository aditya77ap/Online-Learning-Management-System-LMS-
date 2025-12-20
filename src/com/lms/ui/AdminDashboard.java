package com.lms.ui;

import com.lms.dao.CourseDAO;
import com.lms.dao.UserDAO;
import com.lms.model.Course;
import com.lms.model.User;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class AdminDashboard extends JFrame {
    private User currentUser;
    private UserDAO userDAO;
    private CourseDAO courseDAO;
    private JTable userTable;
    private JTable courseTable;
    private DefaultTableModel userTableModel;
    private DefaultTableModel courseTableModel;

    public AdminDashboard(User user) {
        this.currentUser = user;
        this.userDAO = new UserDAO();
        this.courseDAO = new CourseDAO();

        setTitle("Admin Dashboard - Welcome " + currentUser.getName());
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Header
        JLabel headerLabel = new JLabel("Admin Dashboard", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(headerLabel, BorderLayout.NORTH);

        // Main Tabbed Pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // Tab 1: User Management
        JPanel userPanel = createUserManagementPanel();
        tabbedPane.addTab("User Management", userPanel);

        // Tab 2: Course Management
        JPanel coursePanel = createCourseManagementPanel();
        tabbedPane.addTab("Course Management", coursePanel);

        // Tab 3: Performance Analytics
        JPanel analyticsPanel = createAnalyticsPanel();
        tabbedPane.addTab("Performance Analytics", analyticsPanel);

        // Tab 4: System Settings
        JPanel settingsPanel = createSettingsPanel();
        tabbedPane.addTab("System Settings", settingsPanel);

        // Tab 5: System Activity Monitoring
        JPanel activityPanel = createActivityPanel();
        tabbedPane.addTab("System Activity", activityPanel);

        add(tabbedPane, BorderLayout.CENTER);

        // Footer - Logout Button
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        logoutButton.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });
        footerPanel.add(logoutButton);
        add(footerPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private JPanel createUserManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // User List
        String[] columnNames = { "ID", "Name", "Email", "Role" };
        userTableModel = new DefaultTableModel(columnNames, 0);
        userTable = new JTable(userTableModel);
        userTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userTable.setRowHeight(25);
        loadUsers();

        panel.add(new JScrollPane(userTable), BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton refreshButton = new JButton("Refresh User List");
        refreshButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        refreshButton.addActionListener(e -> loadUsers());

        JButton addStudentButton = new JButton("➕ Add Student");
        addStudentButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        addStudentButton.addActionListener(e -> showAddUserDialog("Student"));

        JButton addInstructorButton = new JButton("➕ Add Instructor");
        addInstructorButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        addInstructorButton.addActionListener(e -> showAddUserDialog("Instructor"));

        JButton addAdminButton = new JButton("➕ Add Admin");
        addAdminButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        addAdminButton.addActionListener(e -> showAddUserDialog("Admin"));

        JButton deleteUserButton = new JButton("🗑️ Delete User");
        deleteUserButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        deleteUserButton.addActionListener(e -> deleteSelectedUser());

        buttonPanel.add(refreshButton);
        buttonPanel.add(addStudentButton);
        buttonPanel.add(addInstructorButton);
        buttonPanel.add(addAdminButton);
        buttonPanel.add(deleteUserButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void showAddUserDialog(String role) {
        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField enrollmentField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        Object[] message = {
                "Name:", nameField,
                "Email:", emailField,
                "Enrollment No:", enrollmentField,
                "Password:", passwordField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Add New " + role, JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String enrollmentNo = enrollmentField.getText().trim();
            String password = new String(passwordField.getPassword());

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required.", "Validation Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            User newUser = new User(0, name, email, enrollmentNo, password, role);
            if (userDAO.register(newUser)) {
                JOptionPane.showMessageDialog(this, role + " Added Successfully!");
                loadUsers();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to Add " + role, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteSelectedUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to delete.", "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int userId = (int) userTableModel.getValueAt(selectedRow, 0);
        String userName = (String) userTableModel.getValueAt(selectedRow, 1);
        String userRole = (String) userTableModel.getValueAt(selectedRow, 3);

        if (userId == currentUser.getId()) {
            JOptionPane.showMessageDialog(this, "You cannot delete your own account.", "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete " + userRole + " '" + userName + "'?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (userDAO.deleteUser(userId)) {
                JOptionPane.showMessageDialog(this, "User Deleted Successfully!");
                loadUsers();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to Delete User", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JPanel createCourseManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Course List
        String[] columnNames = { "ID", "Course Name", "Description", "Instructor ID" };
        courseTableModel = new DefaultTableModel(columnNames, 0);
        courseTable = new JTable(courseTableModel);
        courseTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        courseTable.setRowHeight(25);
        loadCourses();

        panel.add(new JScrollPane(courseTable), BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        refreshButton.addActionListener(e -> loadCourses());

        JButton addButton = new JButton("➕ Add Course");
        addButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        addButton.addActionListener(e -> showAddCourseDialog());

        JButton deleteButton = new JButton("🗑️ Delete Course");
        deleteButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        deleteButton.addActionListener(e -> deleteSelectedCourse());

        buttonPanel.add(refreshButton);
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createAnalyticsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("System Analytics");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel chartsPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        chartsPanel.setBackground(Color.WHITE);

        // --- Bar Chart Panel ---
        JPanel barChartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int width = getWidth();
                int height = getHeight();
                int padding = 40;
                int labelPadding = 20;

                // Title for Bar Chart
                g2d.setColor(Color.DARK_GRAY);
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 14));
                g2d.drawString("User Growth (Last 6 Months)", width / 2 - 80, padding / 2);

                // Draw axes
                g2d.setColor(Color.LIGHT_GRAY);
                g2d.drawLine(padding, height - padding, width - padding, height - padding); // X-axis
                g2d.drawLine(padding, height - padding, padding, padding); // Y-axis

                // Data
                int[] data = { 15, 25, 40, 35, 50, 65 };
                String[] labels = { "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
                int barWidth = (width - 2 * padding) / data.length - 20;
                int maxVal = 70;

                g2d.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                for (int i = 0; i < data.length; i++) {
                    int barHeight = (int) ((double) data[i] / maxVal * (height - 2 * padding));
                    int x = padding + i * (barWidth + 20) + 10;
                    int y = height - padding - barHeight;

                    // Bar
                    g2d.setColor(new Color(41, 128, 185));
                    g2d.fillRect(x, y, barWidth, barHeight);

                    // Label
                    g2d.setColor(Color.DARK_GRAY);
                    g2d.drawString(labels[i], x + barWidth / 2 - 10, height - padding + labelPadding);
                    g2d.drawString(String.valueOf(data[i]), x + barWidth / 2 - 5, y - 5);
                }
            }
        };
        barChartPanel.setBackground(Color.WHITE);
        barChartPanel.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1));

        // --- Pie Chart Panel ---
        JPanel pieChartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int width = getWidth();
                int height = getHeight();
                int padding = 20;

                // Title for Pie Chart
                g2d.setColor(Color.DARK_GRAY);
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 14));
                g2d.drawString("User Distribution", width / 2 - 50, 30);

                // Data
                double[] values = { 60, 30, 10 }; // Students, Instructors, Admins
                Color[] colors = { new Color(46, 204, 113), new Color(52, 152, 219), new Color(231, 76, 60) };
                String[] labels = { "Students", "Instructors", "Admins" };

                int minDim = Math.min(width, height) - 2 * padding - 40;
                int x = (width - minDim) / 2;
                int y = (height - minDim) / 2 + 20;

                double startAngle = 0;
                for (int i = 0; i < values.length; i++) {
                    double angle = (values[i] / 100.0) * 360;

                    g2d.setColor(colors[i]);
                    g2d.fillArc(x, y, minDim, minDim, (int) startAngle, (int) angle);
                    startAngle += angle;
                }

                // Legend
                int legendX = width - 100;
                int legendY = height - 80;
                g2d.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                for (int i = 0; i < labels.length; i++) {
                    g2d.setColor(colors[i]);
                    g2d.fillRect(legendX, legendY + i * 20, 10, 10);
                    g2d.setColor(Color.DARK_GRAY);
                    g2d.drawString(labels[i], legendX + 15, legendY + i * 20 + 10);
                }
            }
        };
        pieChartPanel.setBackground(Color.WHITE);
        pieChartPanel.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1));

        chartsPanel.add(barChartPanel);
        chartsPanel.add(pieChartPanel);

        panel.add(chartsPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createSettingsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Title
        JLabel titleLabel = new JLabel("System Configuration");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        panel.add(titleLabel, gbc);

        gbc.gridy++;
        JCheckBox emailNotif = new JCheckBox("Enable Email Notifications");
        emailNotif.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        emailNotif.setBackground(Color.WHITE);
        emailNotif.setSelected(true);
        panel.add(emailNotif, gbc);

        gbc.gridy++;
        JCheckBox maintenanceMode = new JCheckBox("Maintenance Mode");
        maintenanceMode.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        maintenanceMode.setBackground(Color.WHITE);
        panel.add(maintenanceMode, gbc);

        gbc.gridy++;
        panel.add(new JLabel("Session Timeout (minutes):"), gbc);

        gbc.gridy++;
        String[] timeouts = { "15", "30", "60", "120" };
        JComboBox<String> timeoutCombo = new JComboBox<>(timeouts);
        timeoutCombo.setSelectedIndex(1);
        panel.add(timeoutCombo, gbc);

        gbc.gridy++;
        JButton saveButton = new JButton("Save Settings");
        saveButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        saveButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Settings Saved Successfully!"));
        panel.add(saveButton, gbc);

        // Push everything to top-left
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        panel.add(Box.createGlue(), gbc);

        return panel;
    }

    private JPanel createActivityPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Recent System Activity");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        String[] columnNames = { "Timestamp", "User", "Action", "Status" };
        Object[][] data = {
                { "2025-12-01 10:00:00", "Admin (Rajesh)", "Login", "Success" },
                { "2025-12-01 10:05:23", "Instructor (Priya)", "Add Course", "Success" },
                { "2025-12-01 10:15:45", "Student (Arjun)", "Enroll Course", "Success" },
                { "2025-12-01 10:30:12", "Admin (Rajesh)", "Delete User", "Success" },
                { "2025-12-01 11:00:00", "Unknown", "Login", "Failed" },
                { "2025-12-01 11:05:00", "Student (Rohan)", "Login", "Success" }
        };

        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(25);
        table.setEnabled(false); // Read-only

        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        return panel;
    }

    private void loadUsers() {
        new SwingWorker<List<User>, Void>() {
            @Override
            protected List<User> doInBackground() throws Exception {
                return userDAO.getAllUsers();
            }

            @Override
            protected void done() {
                try {
                    List<User> users = get();
                    userTableModel.setRowCount(0);
                    if (users != null) {
                        for (User u : users) {
                            userTableModel.addRow(new Object[] { u.getId(), u.getName(), u.getEmail(), u.getRole() });
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(AdminDashboard.this, "Error loading users: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void loadCourses() {
        new SwingWorker<List<Course>, Void>() {
            @Override
            protected List<Course> doInBackground() throws Exception {
                return courseDAO.getAllCourses();
            }

            @Override
            protected void done() {
                try {
                    List<Course> courses = get();
                    courseTableModel.setRowCount(0);
                    if (courses != null) {
                        for (Course c : courses) {
                            courseTableModel.addRow(new Object[] { c.getId(), c.getCourseName(), c.getDescription(),
                                    c.getInstructorId() });
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(AdminDashboard.this, "Error loading courses: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void showAddCourseDialog() {
        // Fetch instructors first
        new SwingWorker<List<User>, Void>() {
            @Override
            protected List<User> doInBackground() throws Exception {
                return userDAO.getAllUsers().stream()
                        .filter(u -> "Instructor".equalsIgnoreCase(u.getRole()))
                        .collect(Collectors.toList());
            }

            @Override
            protected void done() {
                try {
                    List<User> instructors = get();
                    if (instructors.isEmpty()) {
                        JOptionPane.showMessageDialog(AdminDashboard.this,
                                "No instructors found. Please create an instructor first.", "Error",
                                JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    JTextField nameField = new JTextField();
                    JTextField descField = new JTextField();
                    JComboBox<String> instructorCombo = new JComboBox<>();
                    for (User i : instructors) {
                        instructorCombo.addItem(i.getId() + ": " + i.getName());
                    }

                    Object[] message = {
                            "Course Name:", nameField,
                            "Description:", descField,
                            "Instructor:", instructorCombo
                    };

                    int option = JOptionPane.showConfirmDialog(AdminDashboard.this, message, "Add New Course",
                            JOptionPane.OK_CANCEL_OPTION);
                    if (option == JOptionPane.OK_OPTION) {
                        String name = nameField.getText().trim();
                        String desc = descField.getText().trim();
                        if (name.isEmpty() || desc.isEmpty()) {
                            JOptionPane.showMessageDialog(AdminDashboard.this, "Name and Description are required.",
                                    "Validation Error", JOptionPane.WARNING_MESSAGE);
                            return;
                        }

                        String selectedInstructor = (String) instructorCombo.getSelectedItem();
                        int instructorId = Integer.parseInt(selectedInstructor.split(":")[0]);

                        Course newCourse = new Course(0, name, desc, instructorId);
                        if (courseDAO.addCourse(newCourse)) {
                            JOptionPane.showMessageDialog(AdminDashboard.this, "Course Added Successfully!");
                            loadCourses();
                        } else {
                            JOptionPane.showMessageDialog(AdminDashboard.this, "Failed to Add Course", "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(AdminDashboard.this, "Error preparing dialog: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void deleteSelectedCourse() {
        int selectedRow = courseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a course to delete.", "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int courseId = (int) courseTableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this course?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (courseDAO.deleteCourse(courseId)) {
                JOptionPane.showMessageDialog(this, "Course Deleted Successfully!");
                loadCourses();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to Delete Course", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

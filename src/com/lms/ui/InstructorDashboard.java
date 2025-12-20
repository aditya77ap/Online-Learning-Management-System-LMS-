package com.lms.ui;

import com.lms.dao.AttendanceDAO;
import com.lms.dao.CourseDAO;
import com.lms.model.Course;
import com.lms.model.User;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.List;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.Timer;

public class InstructorDashboard extends JFrame {
    private User currentUser;
    private CourseDAO courseDAO;
    private AttendanceDAO attendanceDAO;
    private JTable courseTable;
    private DefaultTableModel tableModel;
    private Timer currentTimer;

    public InstructorDashboard(User user) {
        this.currentUser = user;
        this.courseDAO = new CourseDAO();
        this.attendanceDAO = new AttendanceDAO();

        setTitle("Instructor Dashboard - Welcome " + user.getName());
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Header
        JLabel headerLabel = new JLabel("Instructor Dashboard", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(headerLabel, BorderLayout.NORTH);

        // Main Tabbed Pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

        // Tab 1: Course Management
        JPanel coursePanel = createCourseManagementPanel();
        tabbedPane.addTab("📘 My Courses", coursePanel);

        // Tab 2: Course Catalog
        JPanel catalogPanel = createCourseCatalogPanel();
        tabbedPane.addTab("📖 Course Catalog", catalogPanel);

        // Tab 3: Assignment Grading
        JPanel gradingPanel = createGradingPanel();
        tabbedPane.addTab("📝 Assignment Grading", gradingPanel);

        // Tab 4: Student Performance
        JPanel performancePanel = createPerformancePanel();
        tabbedPane.addTab("📊 Student Performance", performancePanel);

        // Tab 5: Course Enrollment Stats
        JPanel statsPanel = createEnrollmentStatsPanel();
        tabbedPane.addTab("📈 Enrollment Stats", statsPanel);

        // Tab 6: Feedback Summary
        JPanel feedbackPanel = createFeedbackPanel();
        tabbedPane.addTab("💬 Feedback Summary", feedbackPanel);

        // Tab 7: Attendance
        JPanel attendancePanel = createAttendancePanel();
        tabbedPane.addTab("📅 Attendance", attendancePanel);

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

    private JPanel createCourseManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Course List
        String[] columnNames = { "ID", "Course Name", "Description" };
        tableModel = new DefaultTableModel(columnNames, 0);
        courseTable = new JTable(tableModel);
        courseTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        courseTable.setRowHeight(25);
        loadCourses();

        panel.add(new JScrollPane(courseTable), BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton addCourseButton = new JButton("Add Course");
        addCourseButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        addCourseButton.addActionListener(e -> showAddCourseDialog());
        buttonPanel.add(addCourseButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createCourseCatalogPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columnNames = { "ID", "Course Name", "Description", "Instructor ID" };
        DefaultTableModel catalogModel = new DefaultTableModel(columnNames, 0);
        JTable catalogTable = new JTable(catalogModel);
        catalogTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        catalogTable.setRowHeight(25);
        catalogTable.setEnabled(false); // Read-only

        // Load all courses
        new SwingWorker<List<Course>, Void>() {
            @Override
            protected List<Course> doInBackground() throws Exception {
                return courseDAO.getAllCourses();
            }

            @Override
            protected void done() {
                try {
                    List<Course> courses = get();
                    for (Course c : courses) {
                        catalogModel.addRow(
                                new Object[] { c.getId(), c.getCourseName(), c.getDescription(), c.getInstructorId() });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();

        panel.add(new JScrollPane(catalogTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createGradingPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        String[] columnNames = { "Student", "Course ID", "Course Name", "Assignment", "Status", "Grade" };
        Object[][] data = {
                { "Arjun Patel", "101", "Java Programming", "OOP Concepts", "Submitted", "Pending" },
                { "Rohan Singh", "102", "Database Management", "ER Diagrams", "Submitted", "A" },
                { "Arjun Patel", "102", "Database Management", "SQL Queries", "Late", "B+" },
                { "Neha Gupta", "101", "Java Programming", "Exception Handling", "Submitted", "Pending" }
        };

        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(25);

        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JButton gradeButton = new JButton("Grade Assignment");
        gradeButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gradeButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                String currentGrade = (String) model.getValueAt(row, 5);
                String grade = JOptionPane.showInputDialog(this, "Enter Grade:", currentGrade);
                if (grade != null && !grade.trim().isEmpty()) {
                    model.setValueAt(grade, row, 5);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a student to grade.");
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(gradeButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createPerformancePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Student Performance Analytics");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel chartsPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        chartsPanel.setBackground(Color.WHITE);

        // --- Bar Chart Panel (Average Grades) ---
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

                g2d.setColor(Color.DARK_GRAY);
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 14));
                g2d.drawString("Avg. Class Grades", width / 2 - 60, padding / 2);

                // Axes
                g2d.setColor(Color.LIGHT_GRAY);
                g2d.drawLine(padding, height - padding, width - padding, height - padding);
                g2d.drawLine(padding, height - padding, padding, padding);

                // Data
                int[] data = { 85, 78, 92, 88 };
                String[] labels = { "Java", "DBMS", "Web", "DS" };
                int barWidth = (width - 2 * padding) / data.length - 20;
                int maxVal = 100;

                g2d.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                for (int i = 0; i < data.length; i++) {
                    int barHeight = (int) ((double) data[i] / maxVal * (height - 2 * padding));
                    int x = padding + i * (barWidth + 20) + 10;
                    int y = height - padding - barHeight;

                    g2d.setColor(new Color(52, 152, 219));
                    g2d.fillRect(x, y, barWidth, barHeight);

                    g2d.setColor(Color.DARK_GRAY);
                    g2d.drawString(labels[i], x + barWidth / 2 - 10, height - padding + labelPadding);
                    g2d.drawString(String.valueOf(data[i]), x + barWidth / 2 - 5, y - 5);
                }
            }
        };
        barChartPanel.setBackground(Color.WHITE);
        barChartPanel.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1));

        // --- Pie Chart Panel (Pass/Fail) ---
        JPanel pieChartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int width = getWidth();
                int height = getHeight();
                int padding = 20;

                g2d.setColor(Color.DARK_GRAY);
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 14));
                g2d.drawString("Pass/Fail Distribution", width / 2 - 70, 30);

                double[] values = { 85, 15 }; // Pass, Fail
                Color[] colors = { new Color(46, 204, 113), new Color(231, 76, 60) };
                String[] labels = { "Pass", "Fail" };

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
                int legendX = width - 80;
                int legendY = height - 60;
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

    private JPanel createEnrollmentStatsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Course Enrollment Statistics");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        String[] columnNames = { "Course ID", "Course Name", "Total Enrolled Students", "Active Students" };
        Object[][] data = {
                { "101", "Java Programming", "45", "42" },
                { "102", "Database Management", "38", "35" },
                { "103", "Web Development", "50", "48" },
                { "104", "Data Structures", "30", "28" }
        };

        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(25);
        table.setEnabled(false);

        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createFeedbackPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Student Feedback Summary");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        String[] columnNames = { "Date", "Student", "Course ID", "Course Name", "Rating", "Comment" };
        Object[][] data = {
                { "2025-11-28", "Arjun Patel", "101", "Java Programming", "*****", "Great course structure!" },
                { "2025-11-29", "Rohan Singh", "102", "Database Management", "****",
                        "Very informative, but fast-paced." },
                { "2025-11-30", "Neha Gupta", "103", "Web Development", "*****", "Loved the practical examples." },
                { "2025-12-01", "Arjun Patel", "104", "Data Structures", "***", "Needs more practice problems." }
        };

        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(25);
        table.setEnabled(false);

        // Adjust column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(80); // Date
        table.getColumnModel().getColumn(2).setPreferredWidth(60); // Course ID
        table.getColumnModel().getColumn(5).setPreferredWidth(300); // Comment

        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createPlaceholderPanel(String title, String description) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(100, 100, 100));

        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descLabel.setForeground(new Color(150, 150, 150));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        panel.add(titleLabel, gbc);

        gbc.gridy = 1;
        panel.add(descLabel, gbc);

        return panel;
    }

    private void loadCourses() {
        new SwingWorker<List<Course>, Void>() {
            @Override
            protected List<Course> doInBackground() throws Exception {
                return courseDAO.getCoursesByInstructor(currentUser.getId());
            }

            @Override
            protected void done() {
                try {
                    List<Course> courses = get();
                    tableModel.setRowCount(0);
                    for (Course c : courses) {
                        tableModel.addRow(new Object[] { c.getId(), c.getCourseName(), c.getDescription() });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(InstructorDashboard.this, "Error loading courses: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
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
            String name = nameField.getText().trim();
            String desc = descField.getText().trim();

            if (name.isEmpty() || desc.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name and Description are required.", "Validation Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    Course newCourse = new Course(0, name, desc, currentUser.getId());
                    return courseDAO.addCourse(newCourse);
                }

                @Override
                protected void done() {
                    try {
                        if (get()) {
                            JOptionPane.showMessageDialog(InstructorDashboard.this, "Course Added Successfully!");
                            loadCourses();
                        } else {
                            JOptionPane.showMessageDialog(InstructorDashboard.this, "Failed to Add Course", "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(InstructorDashboard.this,
                                "Error adding course: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        }
    }

    private JPanel createAttendancePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Top: Course Selection & Start Session
        JPanel topPanel = new JPanel();
        JComboBox<String> courseCombo = new JComboBox<>();
        JButton startSessionBtn = new JButton("Start Attendance Session");
        JLabel tokenLabel = new JLabel("Token: -");
        tokenLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));

        topPanel.add(new JLabel("Select Course:"));
        topPanel.add(courseCombo);
        topPanel.add(startSessionBtn);
        topPanel.add(Box.createHorizontalStrut(20));
        topPanel.add(tokenLabel);

        panel.add(topPanel, BorderLayout.NORTH);

        // Split Pane
        JSplitPane splitPane = new JSplitPane();
        splitPane.setResizeWeight(0.4);

        // Left: QR Code
        JPanel qrPanel = new JPanel(new BorderLayout());
        JLabel qrImageLabel = new JLabel("Select a Course to View Students / Start Session", SwingConstants.CENTER);
        qrPanel.add(qrImageLabel, BorderLayout.CENTER);
        splitPane.setLeftComponent(qrPanel);

        // Right: Manual Attendance
        JPanel manualPanel = new JPanel(new BorderLayout());
        String[] cols = { "Student ID", "Name", "Present?" };

        // Use Boolean for generic Checkbox renderer
        DefaultTableModel manualModel = new DefaultTableModel(cols, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 2)
                    return Boolean.class; // Render as Checkbox
                return super.getColumnClass(columnIndex);
            }

            @Override
            public boolean isCellEditable(int row, int col) {
                return col == 2; // Only Checkbox is editable
            }
        };

        JTable manualTable = new JTable(manualModel);
        JButton saveBtn = new JButton("Save Manual Attendance");
        saveBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        saveBtn.setBackground(new Color(46, 204, 113));
        saveBtn.setForeground(Color.WHITE);

        manualPanel.add(new JScrollPane(manualTable), BorderLayout.CENTER);
        manualPanel.add(saveBtn, BorderLayout.SOUTH);
        splitPane.setRightComponent(manualPanel);

        panel.add(splitPane, BorderLayout.CENTER);

        // --- Logic ---

        // Helper to load students
        Runnable loadStudentsForSelected = () -> {
            if (courseCombo.getSelectedItem() == null)
                return;
            String selected = (String) courseCombo.getSelectedItem();
            int courseId = Integer.parseInt(selected.split(" - ")[0]);

            new SwingWorker<List<com.lms.model.User>, Void>() {
                @Override
                protected List<com.lms.model.User> doInBackground() throws Exception {
                    return courseDAO.getEnrolledStudents(courseId);
                }

                @Override
                protected void done() {
                    try {
                        List<com.lms.model.User> students = get();
                        manualModel.setRowCount(0);
                        for (com.lms.model.User s : students) {
                            // Default to False (Absent) or fetch exist status if complex
                            manualModel.addRow(new Object[] { s.getId(), s.getName(), false });
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }.execute();
        };

        // Course Selection Listener
        courseCombo.addActionListener(e -> {
            // 1. Stop existing timer/session
            if (currentTimer != null) {
                currentTimer.stop();
                currentTimer = null;
            }
            // 2. Reset UI
            tokenLabel.setText("Token: -");
            qrImageLabel.setIcon(null);
            qrImageLabel.setText("Click 'Start Session' to generate QR");

            // 3. Load Students for the new course
            loadStudentsForSelected.run();
        });

        // Load courses for combo
        new SwingWorker<List<Course>, Void>() {
            @Override
            protected List<Course> doInBackground() throws Exception {
                return courseDAO.getCoursesByInstructor(currentUser.getId());
            }

            @Override
            protected void done() {
                try {
                    List<Course> courses = get();
                    for (Course c : courses) {
                        courseCombo.addItem(c.getId() + " - " + c.getCourseName());
                    }
                    // Trigger selection for first item if exists
                    if (courseCombo.getItemCount() > 0) {
                        courseCombo.setSelectedIndex(0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();

        // Start Session Button
        startSessionBtn.addActionListener(e -> {
            if (courseCombo.getSelectedItem() == null)
                return;
            String selected = (String) courseCombo.getSelectedItem();
            int courseId = Integer.parseInt(selected.split(" - ")[0]);

            // Stop existing timer if any (just in case)
            if (currentTimer != null) {
                currentTimer.stop();
            }

            // Start Session Logic
            ActionListener tokenRefresher = evt -> {
                String token = String.format("%06d", new Random().nextInt(999999));
                tokenLabel.setText("Token: " + token);

                new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        attendanceDAO.createSession(courseId, token);
                        return null;
                    }
                }.execute();

                // Fetch QR
                new SwingWorker<BufferedImage, Void>() {
                    @Override
                    protected BufferedImage doInBackground() throws Exception {
                        URL url = new URL("https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=" + token);
                        return ImageIO.read(url);
                    }

                    @Override
                    protected void done() {
                        try {
                            BufferedImage image = get();
                            if (image != null) {
                                qrImageLabel.setIcon(new ImageIcon(image));
                                qrImageLabel.setText("");
                            }
                        } catch (Exception ex) {
                            qrImageLabel.setText("Error loading QR");
                        }
                    }
                }.execute();
            };

            // Run immediately then every 10s
            tokenRefresher.actionPerformed(null);
            currentTimer = new Timer(10000, tokenRefresher);
            currentTimer.start();
        });

        saveBtn.addActionListener(e -> {
            if (courseCombo.getSelectedItem() == null)
                return;
            String selected = (String) courseCombo.getSelectedItem();
            int courseId = Integer.parseInt(selected.split(" - ")[0]);
            java.sql.Date today = new java.sql.Date(System.currentTimeMillis());

            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    for (int i = 0; i < manualModel.getRowCount(); i++) {
                        int sId = (int) manualModel.getValueAt(i, 0);
                        Boolean isPresent = (Boolean) manualModel.getValueAt(i, 2);
                        String status = (isPresent != null && isPresent) ? "Present" : "Absent";

                        // Upsert attendance
                        if (attendanceDAO.checkAttendanceExists(sId, courseId, today)) {
                            attendanceDAO.updateAttendance(sId, courseId, status, today);
                        } else {
                            attendanceDAO.markAttendance(sId, courseId, status, today);
                        }
                    }
                    return null;
                }

                @Override
                protected void done() {
                    JOptionPane.showMessageDialog(panel, "Attendance Saved Successfully!");
                }
            }.execute();
        });

        return panel;
    }
}

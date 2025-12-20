package com.lms.ui;

import com.lms.dao.AttendanceDAO;
import com.lms.dao.CourseDAO;
import com.lms.model.Course;
import com.lms.model.User;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class StudentDashboard extends JFrame {
    private User currentUser;
    private CourseDAO courseDAO;
    private AttendanceDAO attendanceDAO;
    private JTable enrolledTable;
    private DefaultTableModel tableModel;

    public StudentDashboard(User user) {
        this.currentUser = user;
        this.currentUser = user;
        this.courseDAO = new CourseDAO();
        this.attendanceDAO = new AttendanceDAO();

        setTitle("Student Dashboard - Welcome " + user.getName());
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Header
        JLabel headerLabel = new JLabel("Student Dashboard", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(headerLabel, BorderLayout.NORTH);

        // Main Tabbed Pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

        // Tab 1: Material Access (Study)
        JPanel materialPanel = createMaterialAccessPanel();
        tabbedPane.addTab("📚 Material Access", materialPanel);

        // Tab 3: Assignment Submission (Work)
        JPanel assignmentPanel = createAssignmentSubmissionPanel();
        tabbedPane.addTab("📝 Assignment Submission", assignmentPanel);

        // Tab 4: Feedback and Grades (Result)
        JPanel feedbackPanel = createFeedbackPanel();
        tabbedPane.addTab("🏆 Feedback and Grades", feedbackPanel);

        // Tab 5: Attendance (Record)
        JPanel attendancePanel = createAttendancePanel();
        tabbedPane.addTab("📅 Attendance", attendancePanel);

        // Tab 6: Progress Tracking (Review)
        JPanel progressPanel = createProgressTrackingPanel();
        tabbedPane.addTab("📊 Progress Tracking", progressPanel);

        // Tab 7: Available Courses (Extra)
        JPanel availablePanel = createAvailableCoursesPanel();
        tabbedPane.addTab("🔍 Available Courses", availablePanel);

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

    private JPanel createEnrollmentPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Enrolled Courses List
        String[] columnNames = { "ID", "Course Name", "Description", "Instructor ID" };
        tableModel = new DefaultTableModel(columnNames, 0);
        enrolledTable = new JTable(tableModel);
        enrolledTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        enrolledTable.setRowHeight(25);
        loadEnrolledCourses();

        panel.add(new JScrollPane(enrolledTable), BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton enrollButton = new JButton("Enroll in New Course");
        enrollButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        enrollButton.addActionListener(e -> showEnrollDialog());
        buttonPanel.add(enrollButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createAvailableCoursesPanel() {
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

    private void loadEnrolledCourses() {
        new SwingWorker<List<Course>, Void>() {
            @Override
            protected List<Course> doInBackground() throws Exception {
                return courseDAO.getEnrolledCourses(currentUser.getId());
            }

            @Override
            protected void done() {
                try {
                    List<Course> courses = get();
                    tableModel.setRowCount(0);
                    for (Course c : courses) {
                        tableModel.addRow(
                                new Object[] { c.getId(), c.getCourseName(), c.getDescription(), c.getInstructorId() });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(StudentDashboard.this,
                            "Error loading enrolled courses: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void showEnrollDialog() {
        new SwingWorker<List<Course>, Void>() {
            @Override
            protected List<Course> doInBackground() throws Exception {
                return courseDAO.getAllCourses();
            }

            @Override
            protected void done() {
                try {
                    List<Course> allCourses = get();
                    String[] courseNames = allCourses.stream().map(c -> c.getId() + ": " + c.getCourseName())
                            .toArray(String[]::new);

                    if (courseNames.length == 0) {
                        JOptionPane.showMessageDialog(StudentDashboard.this, "No courses available to enroll.");
                        return;
                    }

                    String selected = (String) JOptionPane.showInputDialog(StudentDashboard.this,
                            "Select a course to enroll:",
                            "Enrollment", JOptionPane.QUESTION_MESSAGE, null, courseNames, courseNames[0]);

                    if (selected != null) {
                        int courseId = Integer.parseInt(selected.split(":")[0]);
                        enrollInCourse(courseId);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(StudentDashboard.this, "Error fetching courses: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void enrollInCourse(int courseId) {
        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return courseDAO.enrollStudent(currentUser.getId(), courseId);
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        JOptionPane.showMessageDialog(StudentDashboard.this, "Enrolled Successfully!");
                        loadEnrolledCourses();
                    } else {
                        JOptionPane.showMessageDialog(StudentDashboard.this,
                                "Failed to Enroll (Maybe already enrolled?)", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(StudentDashboard.this, "Error enrolling: " + e.getMessage(), "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private JPanel createMaterialAccessPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columnNames = { "Course", "Material Title", "Type", "Date Uploaded" };
        Object[][] data = {
                { "Java Basics", "Intro to Java", "PDF", "2025-10-01" },
                { "Java Basics", "OOP Concepts", "Video", "2025-10-05" },
                { "Advanced Java", "Streams API", "PDF", "2025-10-10" },
                { "Database Systems", "Normalization", "PPT", "2025-10-12" }
        };

        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(25);

        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton downloadButton = new JButton("Download Selected Material");
        downloadButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        downloadButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                String material = (String) table.getValueAt(selectedRow, 1);
                JOptionPane.showMessageDialog(this,
                        "Downloading: " + material + "...\n(Simulation: File saved to Downloads)");
            } else {
                JOptionPane.showMessageDialog(this, "Please select a material to download.");
            }
        });
        buttonPanel.add(downloadButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createAssignmentSubmissionPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // List of Assignments
        String[] columnNames = { "Course", "Assignment Title", "Due Date", "Status" };
        Object[][] data = {
                { "Java Basics", "Calculator Project", "2025-11-01", "Pending" },
                { "Advanced Java", "Chat Application", "2025-11-15", "Submitted" },
                { "Database Systems", "ER Diagram", "2025-10-20", "Graded" }
        };

        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Pending Assignments"));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Submission Form
        JPanel submissionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        submissionPanel.setBorder(BorderFactory.createTitledBorder("Submit Assignment"));

        JLabel fileLabel = new JLabel("Select File:");
        JTextField fileField = new JTextField(20);
        fileField.setEditable(false);
        JButton browseButton = new JButton("Browse...");
        JButton submitButton = new JButton("Submit");

        browseButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int option = fileChooser.showOpenDialog(this);
            if (option == JFileChooser.APPROVE_OPTION) {
                fileField.setText(fileChooser.getSelectedFile().getName());
            }
        });

        submitButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select an assignment from the list first.");
                return;
            }

            if (fileField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select a file to upload.");
                return;
            }

            String assignment = (String) table.getValueAt(selectedRow, 1);
            JOptionPane.showMessageDialog(this, "Assignment '" + assignment + "' submitted successfully!");
            fileField.setText("");
        });

        submissionPanel.add(fileLabel);
        submissionPanel.add(fileField);
        submissionPanel.add(browseButton);
        submissionPanel.add(submitButton);

        panel.add(submissionPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createProgressTrackingPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        String[] courses = { "Java Basics", "Advanced Java", "Database Systems", "Web Development" };
        int[] progressValues = { 85, 40, 60, 20 };

        for (int i = 0; i < courses.length; i++) {
            JPanel coursePanel = new JPanel(new BorderLayout());
            coursePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

            JLabel nameLabel = new JLabel(courses[i]);
            nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

            JProgressBar progressBar = new JProgressBar(0, 100);
            progressBar.setValue(progressValues[i]);
            progressBar.setStringPainted(true);
            progressBar.setForeground(new Color(50, 150, 50));
            progressBar.setPreferredSize(new Dimension(0, 25));

            coursePanel.add(nameLabel, BorderLayout.NORTH);
            coursePanel.add(progressBar, BorderLayout.CENTER);

            panel.add(coursePanel);
            panel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        // Initialize Pie Chart with average progress
        double totalProgress = 0;
        for (int p : progressValues)
            totalProgress += p;
        double average = totalProgress / courses.length;

        PieChartPanel pieChart = new PieChartPanel(average);
        pieChart.setPreferredSize(new Dimension(300, 200));
        pieChart.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(new JScrollPane(panel), BorderLayout.CENTER);
        mainPanel.add(pieChart, BorderLayout.EAST);

        return mainPanel;
    }

    // Custom Pie Chart Component
    private class PieChartPanel extends JPanel {
        private double completed;

        public PieChartPanel(double completed) {
            this.completed = completed;
            setBackground(Color.WHITE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();
            int size = Math.min(width, height) - 40;
            int x = (width - size) / 2;
            int y = (height - size) / 2;

            // Completed Slice (Green)
            g2d.setColor(new Color(50, 150, 50));
            int angle = (int) (completed * 360 / 100);
            g2d.fillArc(x, y, size, size, 90, -angle);

            // Remaining Slice (Light Gray)
            g2d.setColor(new Color(220, 220, 220));
            g2d.fillArc(x, y, size, size, 90 - angle, -(360 - angle));

            // Legend
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 12));
            g2d.setColor(Color.BLACK);
            String text = String.format("Avg Completion: %.1f%%", completed);
            FontMetrics fm = g2d.getFontMetrics();
            int textX = (width - fm.stringWidth(text)) / 2;
            int textY = y + size + 20;
            g2d.drawString(text, textX, textY);
        }
    }

    private JPanel createFeedbackPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columnNames = { "Course", "Assignment", "Grade", "Feedback" };
        Object[][] data = {
                { "Java Basics", "Hello World", "A", "Good job!" },
                { "Database Systems", "SQL Queries", "B+", "Check join syntax." },
                { "Web Development", "HTML Layout", "A-", "Nice design." }
        };

        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(25);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createAttendancePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Stats Panel
        JPanel statsPanel = new JPanel(new GridLayout(1, 4));
        statsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel totalLabel = new JLabel("Total: 0");
        JLabel presentLabel = new JLabel("Present: 0");
        JLabel absentLabel = new JLabel("Absent: 0");
        JLabel percentLabel = new JLabel("0%");

        statsPanel.add(totalLabel);
        statsPanel.add(presentLabel);
        statsPanel.add(absentLabel);
        statsPanel.add(percentLabel);

        panel.add(statsPanel, BorderLayout.NORTH);

        // Subject Wise Stats Table
        String[] cols = { "Course", "Total", "Present", "Percentage" };
        DefaultTableModel statsModel = new DefaultTableModel(cols, 0);
        JTable statsTable = new JTable(statsModel);
        panel.add(new JScrollPane(statsTable), BorderLayout.CENTER);

        // Load Stats Logic
        Runnable loadStats = () -> {
            new SwingWorker<Map<String, Object>, Void>() {
                @Override
                protected Map<String, Object> doInBackground() throws Exception {
                    return attendanceDAO.getStudentStats(currentUser.getId());
                }

                @Override
                protected void done() {
                    try {
                        Map<String, Object> stats = get();
                        if (stats.containsKey("total")) {
                            totalLabel.setText("Total: " + stats.get("total"));
                            presentLabel.setText("Present: " + stats.get("present"));
                            absentLabel.setText("Absent: " + stats.get("absent"));
                            percentLabel.setText(String.format("Overall: %.1f%%", stats.get("percentage")));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.execute();

            new SwingWorker<List<Map<String, Object>>, Void>() {
                @Override
                protected List<Map<String, Object>> doInBackground() throws Exception {
                    return attendanceDAO.getSubjectWiseStats(currentUser.getId());
                }

                @Override
                protected void done() {
                    try {
                        List<Map<String, Object>> list = get();
                        statsModel.setRowCount(0);
                        for (Map<String, Object> m : list) {
                            statsModel.addRow(new Object[] {
                                    m.get("course"), m.get("total"), m.get("present"),
                                    String.format("%.1f%%", m.get("percentage"))
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.execute();
        };

        loadStats.run(); // Initial Load

        // Scan Button
        JButton scanBtn = new JButton("Scan QR Code (Enter Token)");
        scanBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        scanBtn.setBackground(new Color(52, 152, 219));
        scanBtn.setForeground(Color.WHITE);

        scanBtn.addActionListener(e -> {
            String token = JOptionPane.showInputDialog(panel, "Enter Token from Instructor Screen:");
            if (token != null && !token.trim().isEmpty()) {
                new SwingWorker<Boolean, Void>() {

                    @Override
                    protected Boolean doInBackground() throws Exception {
                        int courseId = attendanceDAO.validateToken(token.trim());
                        if (courseId != -1) {
                            java.sql.Date today = new java.sql.Date(System.currentTimeMillis());
                            // Check if already marked?
                            if (attendanceDAO.checkAttendanceExists(currentUser.getId(), courseId, today)) {
                                return true; // Already marked
                            }
                            return attendanceDAO.markAttendance(currentUser.getId(), courseId, "Present", today);
                        }
                        return false;
                    }

                    @Override
                    protected void done() {
                        try {
                            if (get()) {
                                JOptionPane.showMessageDialog(panel, "Attendance Marked Successfully!");
                                loadStats.run();
                            } else {
                                JOptionPane.showMessageDialog(panel, "Invalid or Expired Token!", "Error",
                                        JOptionPane.ERROR_MESSAGE);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }.execute();
            }
        });

        panel.add(scanBtn, BorderLayout.SOUTH);

        return panel;
    }
}

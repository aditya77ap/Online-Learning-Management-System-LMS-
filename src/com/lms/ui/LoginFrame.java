package com.lms.ui;

import com.lms.dao.UserDAO;
import com.lms.model.User;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private UserDAO userDAO;

    public LoginFrame() {
        userDAO = new UserDAO();
        setTitle("Project LMS - Login");
        setSize(500, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Set background color
        getContentPane().setBackground(new Color(245, 245, 245));

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(41, 128, 185));
        headerPanel.setPreferredSize(new Dimension(500, 80));
        JLabel headerLabel = new JLabel("Project LMS");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 28));
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel);
        add(headerPanel, BorderLayout.NORTH);

        // Main Panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(30, 50, 20, 50));
        mainPanel.setBackground(new Color(245, 245, 245));

        // Login Title
        JLabel loginTitle = new JLabel("Login to Your Account");
        loginTitle.setFont(new Font("Arial", Font.BOLD, 18));
        loginTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(loginTitle);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 25)));

        // Email Panel
        JPanel emailPanel = new JPanel(new BorderLayout(10, 5));
        emailPanel.setBackground(new Color(245, 245, 245));
        emailPanel.setMaximumSize(new Dimension(400, 60));
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        emailField = new JTextField();
        emailField.setFont(new Font("Arial", Font.PLAIN, 14));
        emailField.setPreferredSize(new Dimension(400, 35));
        emailPanel.add(emailLabel, BorderLayout.NORTH);
        emailPanel.add(emailField, BorderLayout.CENTER);
        mainPanel.add(emailPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Password Panel
        JPanel passwordPanel = new JPanel(new BorderLayout(10, 5));
        passwordPanel.setBackground(new Color(245, 245, 245));
        passwordPanel.setMaximumSize(new Dimension(400, 60));
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setPreferredSize(new Dimension(400, 35));
        passwordPanel.add(passwordLabel, BorderLayout.NORTH);
        passwordPanel.add(passwordField, BorderLayout.CENTER);
        mainPanel.add(passwordPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 25)));

        // Login Button
        loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 16));
        loginButton.setBackground(new Color(41, 128, 185));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setPreferredSize(new Dimension(400, 40));
        loginButton.setMaximumSize(new Dimension(400, 40));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });
        mainPanel.add(loginButton);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Test Credentials Panel
        JPanel credentialsPanel = new JPanel();
        credentialsPanel.setLayout(new BoxLayout(credentialsPanel, BoxLayout.Y_AXIS));
        credentialsPanel.setBackground(new Color(236, 240, 241));
        credentialsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                new EmptyBorder(10, 15, 10, 15)));
        credentialsPanel.setMaximumSize(new Dimension(400, 120));

        JLabel credTitle = new JLabel("Test Credentials:");
        credTitle.setFont(new Font("Arial", Font.BOLD, 12));
        credTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        credentialsPanel.add(credTitle);
        credentialsPanel.add(Box.createRigidArea(new Dimension(0, 8)));

        JLabel adminCred = new JLabel("Admin - ID: admin@lms.com | Pass: admin123");
        adminCred.setFont(new Font("Courier New", Font.PLAIN, 11));
        adminCred.setAlignmentX(Component.LEFT_ALIGNMENT);
        credentialsPanel.add(adminCred);

        JLabel instructorCred = new JLabel("Instructor - ID: instructor@lms.com | Pass: instructor123");
        instructorCred.setFont(new Font("Courier New", Font.PLAIN, 11));
        instructorCred.setAlignmentX(Component.LEFT_ALIGNMENT);
        credentialsPanel.add(instructorCred);

        JLabel studentCred = new JLabel("Student - ID: student@lms.com | Pass: student123");
        studentCred.setFont(new Font("Courier New", Font.PLAIN, 11));
        studentCred.setAlignmentX(Component.LEFT_ALIGNMENT);
        credentialsPanel.add(studentCred);

        mainPanel.add(credentialsPanel);

        add(mainPanel, BorderLayout.CENTER);

        // Register Button (Demo)
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(new Color(245, 245, 245));
        footerPanel.setBorder(new EmptyBorder(0, 0, 15, 0));
        JButton registerButton = new JButton("Register (Demo)");
        registerButton.setFont(new Font("Arial", Font.PLAIN, 12));
        registerButton.setEnabled(false);
        footerPanel.add(registerButton);
        add(footerPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void login() {
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());

        User user = userDAO.login(email, password);

        if (user != null) {
            JOptionPane.showMessageDialog(this, "Login Successful! Welcome " + user.getName());
            dispose(); // Close login window
            openDashboard(user);
        } else {
            JOptionPane.showMessageDialog(this, "Invalid Email or Password", "Login Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openDashboard(User user) {
        switch (user.getRole()) {
            case "Admin":
                new AdminDashboard(user);
                break;
            case "Instructor":
                new InstructorDashboard(user);
                break;
            case "Student":
                new StudentDashboard(user);
                break;
            default:
                JOptionPane.showMessageDialog(this, "Unknown Role", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

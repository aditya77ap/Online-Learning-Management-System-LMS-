package com.lms.ui;

import com.lms.dao.UserDAO;
import com.lms.model.User;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RegistrationFrame extends JFrame {
    private JTextField nameField;
    private JTextField emailField;
    private JTextField enrollmentField;
    private JPasswordField passwordField;
    private JComboBox<String> roleComboBox;
    private JButton registerButton;
    private UserDAO userDAO;
    private LoginFrame loginFrame;

    public RegistrationFrame(LoginFrame loginFrame) {
        this.loginFrame = loginFrame;
        userDAO = new UserDAO();
        setTitle("LMS - Registration");
        setSize(600, 750);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Set modern background color
        getContentPane().setBackground(new Color(240, 242, 245));

        // Main container with centered content
        JPanel mainContainer = new JPanel(new GridBagLayout());
        mainContainer.setBackground(new Color(240, 242, 245));

        // Registration card panel
        JPanel regCard = new JPanel();
        regCard.setLayout(new BoxLayout(regCard, BoxLayout.Y_AXIS));
        regCard.setBackground(Color.WHITE);
        regCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(40, 50, 40, 50)));
        regCard.setPreferredSize(new Dimension(500, 650));

        // Title Section
        JLabel titleLabel = new JLabel("Create Account");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(41, 128, 185));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        regCard.add(titleLabel);

        regCard.add(Box.createRigidArea(new Dimension(0, 30)));

        // Name Field
        regCard.add(createLabel("Full Name:"));
        nameField = createTextField();
        regCard.add(createFieldPanel(nameField));
        regCard.add(Box.createRigidArea(new Dimension(0, 15)));

        // Email Field
        regCard.add(createLabel("Email:"));
        emailField = createTextField();
        regCard.add(createFieldPanel(emailField));
        regCard.add(Box.createRigidArea(new Dimension(0, 15)));

        // Enrollment No Field
        regCard.add(createLabel("Enrollment No:"));
        enrollmentField = createTextField();
        regCard.add(createFieldPanel(enrollmentField));
        regCard.add(Box.createRigidArea(new Dimension(0, 15)));

        // Password Field
        regCard.add(createLabel("Password:"));
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setBorder(new EmptyBorder(5, 10, 5, 10));
        regCard.add(createFieldPanel(passwordField));
        regCard.add(Box.createRigidArea(new Dimension(0, 15)));

        // Role Selection
        regCard.add(createLabel("Role:"));
        String[] roles = { "Student", "Instructor" };
        roleComboBox = new JComboBox<>(roles);
        roleComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        roleComboBox.setBackground(Color.WHITE);
        regCard.add(createFieldPanel(roleComboBox));
        regCard.add(Box.createRigidArea(new Dimension(0, 30)));

        // Register Button
        registerButton = new JButton("REGISTER");
        registerButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        registerButton.setBackground(new Color(39, 174, 96));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        registerButton.setBorderPainted(false);
        registerButton.setPreferredSize(new Dimension(400, 45));
        registerButton.setMaximumSize(new Dimension(400, 45));
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                register();
            }
        });
        regCard.add(registerButton);

        regCard.add(Box.createRigidArea(new Dimension(0, 20)));

        // Back to Login Link
        JLabel loginLink = new JLabel("Already have an account? Login");
        loginLink.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        loginLink.setForeground(new Color(41, 128, 185));
        loginLink.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
                loginFrame.setVisible(true);
            }
        });
        regCard.add(loginLink);

        mainContainer.add(regCard);
        add(mainContainer, BorderLayout.CENTER);

        // Handle window closing to show login frame
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                loginFrame.setVisible(true);
            }
        });
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(new Color(100, 100, 100));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        // Align left relative to the field, but since we use BoxLayout Y_AXIS,
        // we need a container or just set alignment.
        // Let's keep it simple and center or use a panel.
        // Actually, let's wrap label and field in a panel for better alignment if
        // needed.
        // For now, just returning label.
        return label;
    }

    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(new EmptyBorder(5, 10, 5, 10));
        return field;
    }

    private JPanel createFieldPanel(JComponent field) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        panel.setMaximumSize(new Dimension(400, 40));
        panel.setPreferredSize(new Dimension(400, 40));
        panel.add(field, BorderLayout.CENTER);

        // Wrap in a container to handle alignment if needed, but BoxLayout handles
        // width via MaximumSize
        return panel;
    }

    private void register() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String enrollmentNo = enrollmentField.getText().trim();
        String password = new String(passwordField.getPassword());
        String role = (String) roleComboBox.getSelectedItem();

        if (name.isEmpty() || email.isEmpty() || enrollmentNo.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.", "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        User newUser = new User(0, name, email, enrollmentNo, password, role);
        if (userDAO.register(newUser)) {
            JOptionPane.showMessageDialog(this, "Registration Successful! Please Login.", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
            loginFrame.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Registration Failed. Email or Enrollment No might already exist.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

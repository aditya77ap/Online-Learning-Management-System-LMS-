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

public class LoginFrame extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private EyeIconPanel eyeIcon;
    private boolean passwordVisible = false;
    private UserDAO userDAO;

    public LoginFrame() {
        userDAO = new UserDAO();
        setTitle("LMS - Login");
        setSize(600, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Set modern background color
        getContentPane().setBackground(new Color(240, 242, 245));

        // Main container with centered content
        JPanel mainContainer = new JPanel(new GridBagLayout());
        mainContainer.setBackground(new Color(240, 242, 245));

        // Login card panel
        JPanel loginCard = new JPanel();
        loginCard.setLayout(new BoxLayout(loginCard, BoxLayout.Y_AXIS));
        loginCard.setBackground(Color.WHITE);
        loginCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(50, 50, 50, 50))); // Reduced side padding slightly
        loginCard.setPreferredSize(new Dimension(500, 600));

        // Logo/Title Section
        JLabel titleLabel = new JLabel("Learning Management System");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(41, 128, 185));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginCard.add(titleLabel);

        loginCard.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel subtitleLabel = new JLabel("Sign in to continue");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(100, 100, 100));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginCard.add(subtitleLabel);

        loginCard.add(Box.createRigidArea(new Dimension(0, 40)));

        // Email Field with inline label
        JPanel emailPanel = new JPanel();
        emailPanel.setLayout(new BorderLayout());
        emailPanel.setBackground(Color.WHITE);
        emailPanel.setMaximumSize(new Dimension(400, 40));
        emailPanel.setPreferredSize(new Dimension(400, 40));
        emailPanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

        JLabel emailLabel = new JLabel("Email/ID: ");
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        emailLabel.setForeground(new Color(100, 100, 100));
        emailLabel.setBorder(new EmptyBorder(0, 12, 0, 5));

        emailField = new JTextField();
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        emailField.setBorder(new EmptyBorder(5, 0, 5, 12));

        emailPanel.add(emailLabel, BorderLayout.WEST);
        emailPanel.add(emailField, BorderLayout.CENTER);

        // Center the email panel
        JPanel emailContainer = new JPanel();
        emailContainer.setLayout(new BoxLayout(emailContainer, BoxLayout.X_AXIS));
        emailContainer.setBackground(Color.WHITE);
        emailContainer.add(Box.createHorizontalGlue());
        emailContainer.add(emailPanel);
        emailContainer.add(Box.createHorizontalGlue());
        emailContainer.setMaximumSize(new Dimension(500, 40));

        loginCard.add(emailContainer);
        loginCard.add(Box.createRigidArea(new Dimension(0, 20)));

        // Password Field with inline label and eye icon
        JPanel passwordPanel = new JPanel();
        passwordPanel.setLayout(new BorderLayout());
        passwordPanel.setBackground(Color.WHITE);
        passwordPanel.setMaximumSize(new Dimension(400, 40));
        passwordPanel.setPreferredSize(new Dimension(400, 40));
        passwordPanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

        JLabel passwordLabel = new JLabel("Password: ");
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordLabel.setForeground(new Color(100, 100, 100));
        passwordLabel.setBorder(new EmptyBorder(0, 12, 0, 5));

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setBorder(new EmptyBorder(5, 0, 5, 5));

        // Custom eye icon panel
        eyeIcon = new EyeIconPanel();
        eyeIcon.setPreferredSize(new Dimension(30, 30));
        eyeIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));
        eyeIcon.setToolTipText("Show password");
        eyeIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                togglePasswordVisibility();
            }
        });

        JPanel eyeContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 5));
        eyeContainer.setBackground(Color.WHITE);
        eyeContainer.add(eyeIcon);

        passwordPanel.add(passwordLabel, BorderLayout.WEST);
        passwordPanel.add(passwordField, BorderLayout.CENTER);
        passwordPanel.add(eyeContainer, BorderLayout.EAST);

        // Center the password panel
        JPanel passwordContainer = new JPanel();
        passwordContainer.setLayout(new BoxLayout(passwordContainer, BoxLayout.X_AXIS));
        passwordContainer.setBackground(Color.WHITE);
        passwordContainer.add(Box.createHorizontalGlue());
        passwordContainer.add(passwordPanel);
        passwordContainer.add(Box.createHorizontalGlue());
        passwordContainer.setMaximumSize(new Dimension(500, 40));

        loginCard.add(passwordContainer);
        loginCard.add(Box.createRigidArea(new Dimension(0, 35)));

        // Login Button
        loginButton = new JButton("LOGIN");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginButton.setBackground(new Color(41, 128, 185));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.setPreferredSize(new Dimension(400, 45));
        loginButton.setMaximumSize(new Dimension(400, 45));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });
        loginCard.add(loginButton);

        loginCard.add(Box.createRigidArea(new Dimension(0, 15)));

        // Registration Link
        JLabel registerLink = new JLabel("Don't have an account? Register");
        registerLink.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        registerLink.setForeground(new Color(41, 128, 185));
        registerLink.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                openRegistration();
            }
        });
        loginCard.add(registerLink);

        loginCard.add(Box.createRigidArea(new Dimension(0, 20)));

        // Divider
        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(400, 1));
        separator.setForeground(new Color(220, 220, 220));
        loginCard.add(separator);

        loginCard.add(Box.createRigidArea(new Dimension(0, 20)));

        // Test Credentials Section
        JLabel credLabel = new JLabel("Test Credentials");
        credLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        credLabel.setForeground(new Color(80, 80, 80));
        credLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginCard.add(credLabel);

        loginCard.add(Box.createRigidArea(new Dimension(0, 12)));

        // Credentials Panel
        JPanel credentialsPanel = new JPanel();
        credentialsPanel.setLayout(new BoxLayout(credentialsPanel, BoxLayout.Y_AXIS));
        credentialsPanel.setBackground(new Color(248, 249, 250));
        credentialsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(12, 15, 12, 15)));
        credentialsPanel.setMaximumSize(new Dimension(400, 90));
        credentialsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        String[] credentials = {
                "Admin: rajeshkumar@lms.com / ADMIN001 / admin123",
                "Instructor: priyasharma@lms.com / INST101 / instructor123",
                "Student: arjunpatel@lms.com / 24SCSE10110 / student123"
        };

        for (String cred : credentials) {
            JLabel credItem = new JLabel(cred);
            credItem.setFont(new Font("Consolas", Font.PLAIN, 11));
            credItem.setForeground(new Color(70, 70, 70));
            credItem.setAlignmentX(Component.CENTER_ALIGNMENT);
            credentialsPanel.add(credItem);
            if (!cred.equals(credentials[credentials.length - 1])) {
                credentialsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            }
        }

        loginCard.add(credentialsPanel);

        // Add login card to main container
        mainContainer.add(loginCard);

        add(mainContainer, BorderLayout.CENTER);

        // Footer
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(new Color(240, 242, 245));
        footerPanel.setBorder(new EmptyBorder(10, 0, 15, 0));
        JLabel footerLabel = new JLabel("© 2025 Learning Management System");
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        footerLabel.setForeground(new Color(120, 120, 120));
        footerPanel.add(footerLabel);
        add(footerPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void togglePasswordVisibility() {
        passwordVisible = !passwordVisible;
        if (passwordVisible) {
            passwordField.setEchoChar((char) 0); // Show password
            eyeIcon.setVisible(false);
            eyeIcon.setToolTipText("Hide password");
        } else {
            passwordField.setEchoChar('•'); // Hide password
            eyeIcon.setVisible(true);
            eyeIcon.setToolTipText("Show password");
        }
        eyeIcon.repaint();
    }

    private void login() {
        String identifier = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (identifier.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter both email/enrollment no and password",
                    "Login Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        loginButton.setEnabled(false);
        loginButton.setText("Logging in...");
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        SwingWorker<User, Void> worker = new SwingWorker<User, Void>() {
            @Override
            protected User doInBackground() throws Exception {
                return userDAO.login(identifier, password);
            }

            @Override
            protected void done() {
                try {
                    User user = get();
                    if (user != null) {
                        JOptionPane.showMessageDialog(LoginFrame.this,
                                "Welcome, " + user.getName() + "!",
                                "Login Successful",
                                JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                        openDashboard(user);
                    } else {
                        JOptionPane.showMessageDialog(LoginFrame.this,
                                "Invalid Credentials",
                                "Login Error",
                                JOptionPane.ERROR_MESSAGE);
                        passwordField.setText("");
                    }
                } catch (Exception e) {
                    String message = "Login Failed: " + e.getMessage();
                    if (e.getCause() != null) {
                        String causeMsg = e.getCause().getMessage();
                        if (causeMsg != null && causeMsg.contains("Driver not found")) {
                            message = "Database Driver Missing!\nPlease add mysql-connector-j.jar to the 'lib' folder.";
                        } else if (causeMsg != null && causeMsg.contains("Access denied")) {
                            message = "Database Access Denied!\nPlease check your password in config.properties.";
                        }
                    }

                    JOptionPane.showMessageDialog(LoginFrame.this,
                            message,
                            "System Error",
                            JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                } finally {
                    loginButton.setEnabled(true);
                    loginButton.setText("LOGIN");
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        };

        worker.execute();
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

    private void openRegistration() {
        setVisible(false);
        new RegistrationFrame(this).setVisible(true);
    }

    // Custom panel to draw eye icon
    class EyeIconPanel extends JPanel {
        private boolean isVisible = true;

        public void setVisible(boolean visible) {
            this.isVisible = visible;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(new Color(80, 80, 80));
            g2d.setStroke(new BasicStroke(2));

            int width = getWidth();
            int height = getHeight();
            int centerX = width / 2;
            int centerY = height / 2;

            if (isVisible) {
                // Draw open eye
                // Eye outline (ellipse)
                g2d.drawArc(centerX - 10, centerY - 5, 20, 10, 0, 180);
                g2d.drawArc(centerX - 10, centerY - 5, 20, 10, 180, 180);

                // Pupil (filled circle)
                g2d.fillOval(centerX - 3, centerY - 3, 6, 6);
            } else {
                // Draw closed eye (eye with slash)
                // Eye outline
                g2d.drawArc(centerX - 10, centerY - 5, 20, 10, 0, 180);
                g2d.drawArc(centerX - 10, centerY - 5, 20, 10, 180, 180);

                // Pupil
                g2d.fillOval(centerX - 3, centerY - 3, 6, 6);

                // Slash through eye
                g2d.drawLine(centerX - 12, centerY + 8, centerX + 12, centerY - 8);
            }
        }
    }
}

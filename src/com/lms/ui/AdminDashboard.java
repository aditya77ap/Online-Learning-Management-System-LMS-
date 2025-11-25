package com.lms.ui;

import com.lms.dao.UserDAO;
import com.lms.model.User;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class AdminDashboard extends JFrame {
    private User currentUser;
    private UserDAO userDAO;
    private JTable userTable;
    private DefaultTableModel tableModel;

    public AdminDashboard(User user) {
        this.currentUser = user;
        this.userDAO = new UserDAO();

        setTitle("Admin Dashboard - Welcome " + currentUser.getName());
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Header
        JLabel headerLabel = new JLabel("Admin Dashboard", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(headerLabel, BorderLayout.NORTH);

        // Center - User List
        String[] columnNames = { "ID", "Name", "Email", "Role" };
        tableModel = new DefaultTableModel(columnNames, 0);
        userTable = new JTable(tableModel);
        loadUsers();
        add(new JScrollPane(userTable), BorderLayout.CENTER);

        // Footer - Buttons
        JPanel buttonPanel = new JPanel();
        JButton refreshButton = new JButton("Refresh User List");
        refreshButton.addActionListener(e -> loadUsers());

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });

        buttonPanel.add(refreshButton);
        buttonPanel.add(logoutButton);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void loadUsers() {
        try {
            tableModel.setRowCount(0); // Clear existing data
            java.util.List<User> users = userDAO.getAllUsers();
            if (users != null) {
                for (User u : users) {
                    tableModel.addRow(new Object[] { u.getId(), u.getName(), u.getEmail(), u.getRole() });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading users: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}

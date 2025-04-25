package com.trackon.view;

import com.trackon.dao.UserDAO;
import com.trackon.dao.impl.UserDAOImpl;
import com.trackon.model.User;

import javax.swing.*;
import java.awt.*;

public class LoginView extends JPanel {
    private final JFrame parentFrame;
    private final UserDAO userDAO;
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    
    public LoginView(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        this.userDAO = new UserDAOImpl();
        
        setLayout(new BorderLayout());
        
        // Create the main panel with a white background
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);
        
        // Create the login panel
        JPanel loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBackground(Color.WHITE);
        loginPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Add title
        JLabel titleLabel = new JLabel("TrackOn");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        loginPanel.add(titleLabel, gbc);
        
        // Add subtitle
        JLabel subtitleLabel = new JLabel("Certificate Tracking System");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        loginPanel.add(subtitleLabel, gbc);
        
        // Add some spacing
        loginPanel.add(Box.createVerticalStrut(20), gbc);
        
        // Username field
        usernameField = new JTextField(20);
        loginPanel.add(new JLabel("Username:"), gbc);
        loginPanel.add(usernameField, gbc);
        
        // Password field
        passwordField = new JPasswordField(20);
        loginPanel.add(new JLabel("Password:"), gbc);
        loginPanel.add(passwordField, gbc);
        
        // Add some spacing
        loginPanel.add(Box.createVerticalStrut(10), gbc);
        
        // Login button
        JButton loginButton = new JButton("Login");
        loginButton.setBackground(new Color(0, 123, 255));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.addActionListener(e -> handleLogin());
        loginPanel.add(loginButton, gbc);
        
        // Register link
        JButton registerButton = new JButton("Register");
        registerButton.setBorderPainted(false);
        registerButton.setContentAreaFilled(false);
        registerButton.setForeground(new Color(0, 123, 255));
        registerButton.addActionListener(e -> showRegisterDialog());
        loginPanel.add(registerButton, gbc);
        
        // Add the login panel to the main panel
        mainPanel.add(loginPanel);
        
        // Add the main panel to this panel
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter both username and password",
                "Login Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (userDAO.authenticate(username, password)) {
            User user = userDAO.findByUsername(username);
            if (user != null) {
                // Clear the fields
                usernameField.setText("");
                passwordField.setText("");
                
                // Show appropriate dashboard based on user role
                if ("ADMIN".equals(user.getRole())) {
                    showAdminDashboard(user);
                } else {
                    showUserDashboard(user);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this,
                "Invalid username or password",
                "Login Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showRegisterDialog() {
        JDialog dialog = new JDialog(parentFrame, "Register", true);
        dialog.setLayout(new BorderLayout());
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        JTextField usernameField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);
        JTextField emailField = new JTextField(20);
        
        // Create radio buttons for role selection
        JPanel rolePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ButtonGroup roleGroup = new ButtonGroup();
        JRadioButton userRadio = new JRadioButton("User", true);
        JRadioButton adminRadio = new JRadioButton("Admin");
        roleGroup.add(userRadio);
        roleGroup.add(adminRadio);
        rolePanel.add(userRadio);
        rolePanel.add(adminRadio);
        
        panel.add(new JLabel("Username:"), gbc);
        panel.add(usernameField, gbc);
        panel.add(new JLabel("Password:"), gbc);
        panel.add(passwordField, gbc);
        panel.add(new JLabel("Email:"), gbc);
        panel.add(emailField, gbc);
        panel.add(new JLabel("Role:"), gbc);
        panel.add(rolePanel, gbc);
        
        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String email = emailField.getText();
            String role = userRadio.isSelected() ? "USER" : "ADMIN";
            
            if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                    "Please fill in all fields",
                    "Registration Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setPassword(password);
            newUser.setEmail(email);
            newUser.setRole(role);
            
            if (userDAO.save(newUser)) {
                JOptionPane.showMessageDialog(dialog,
                    "Registration successful! Please login.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog,
                    "Registration failed. Username or email might already exist.",
                    "Registration Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        panel.add(registerButton, gbc);
        dialog.add(panel, BorderLayout.CENTER);
        
        dialog.pack();
        dialog.setLocationRelativeTo(parentFrame);
        dialog.setVisible(true);
    }
    
    private void showAdminDashboard(User user) {
        parentFrame.getContentPane().removeAll();
        parentFrame.add(new AdminDashboardView(parentFrame, user));
        parentFrame.revalidate();
        parentFrame.repaint();
    }
    
    private void showUserDashboard(User user) {
        parentFrame.getContentPane().removeAll();
        parentFrame.add(new UserDashboardView(parentFrame, user));
        parentFrame.revalidate();
        parentFrame.repaint();
    }
} 
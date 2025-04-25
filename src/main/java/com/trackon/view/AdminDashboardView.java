package com.trackon.view;

import com.trackon.dao.ActivityLogDAO;
import com.trackon.dao.CertificateApplicationDAO;
import com.trackon.dao.GrievanceDAO;
import com.trackon.dao.UserDAO;
import com.trackon.dao.impl.ActivityLogDAOImpl;
import com.trackon.dao.impl.CertificateApplicationDAOImpl;
import com.trackon.dao.impl.GrievanceDAOImpl;
import com.trackon.dao.impl.UserDAOImpl;
import com.trackon.model.ActivityLog;
import com.trackon.model.CertificateApplication;
import com.trackon.model.Grievance;
import com.trackon.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.sql.Timestamp;

public class AdminDashboardView extends JPanel {
    private final JFrame parentFrame;
    private final User currentUser;
    private final CertificateApplicationDAO applicationDAO;
    private final ActivityLogDAO activityLogDAO;
    private final GrievanceDAO grievanceDAO;
    private final JTabbedPane tabbedPane;
    
    private JTable applicationsTable;
    private JTable grievancesTable;
    private DefaultTableModel grievancesTableModel;
    
    public AdminDashboardView(JFrame parentFrame, User user) {
        this.parentFrame = parentFrame;
        this.currentUser = user;
        this.applicationDAO = new CertificateApplicationDAOImpl();
        this.activityLogDAO = new ActivityLogDAOImpl();
        this.grievanceDAO = new GrievanceDAOImpl();
        
        setLayout(new BorderLayout());
        
        // Create the top panel with logout button
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(0, 123, 255));
        topPanel.setPreferredSize(new Dimension(getWidth(), 50));
        
        JLabel welcomeLabel = new JLabel("Welcome, " + user.getUsername());
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        topPanel.add(welcomeLabel, BorderLayout.WEST);
        
        JButton logoutButton = new JButton("Logout");
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setBackground(new Color(0, 123, 255));
        logoutButton.setBorderPainted(false);
        logoutButton.setFocusPainted(false);
        logoutButton.addActionListener(e -> handleLogout());
        topPanel.add(logoutButton, BorderLayout.EAST);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Create the tabbed pane
        tabbedPane = new JTabbedPane();
        
        // Add tabs
        tabbedPane.addTab("Home", createHomeTab());
        tabbedPane.addTab("Logs", createLogsTab());
        tabbedPane.addTab("Grievances", createGrievancesTab());
        
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    private JPanel createHomeTab() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Create table model
        String[] columnNames = {"ID", "Application Number", "Applicant", "Certificate Type", 
                              "Status", "Description", "Application Date"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        applicationsTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(applicationsTable);
        
        // Add buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JButton addButton = new JButton("Add Application");
        addButton.addActionListener(e -> showAddApplicationDialog());
        
        JButton updateButton = new JButton("Update Status");
        updateButton.addActionListener(e -> showUpdateStatusDialog(applicationsTable));
        
        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> deleteSelectedApplication(applicationsTable));
        
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> searchApplications());
        
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(searchButton);
        
        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Load initial data
        refreshApplicationsTable();
        
        return panel;
    }
    
    private JPanel createLogsTab() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Create table model
        String[] columnNames = {"Admin", "Action", "Description", "Timestamp"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        
        // Add refresh button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshLogsTable(table));
        buttonPanel.add(refreshButton);
        
        // Load initial data
        refreshLogsTable(table);
        
        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createGrievancesTab() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Create table model
        String[] columnNames = {"Application Number", "Applicant", "Subject", "Description", 
                              "Status", "Admin Response", "Last Updated"};
        grievancesTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        grievancesTable = new JTable(grievancesTableModel);
        JScrollPane scrollPane = new JScrollPane(grievancesTable);
        
        // Add button to respond to grievance
        JButton respondButton = new JButton("Respond to Grievance");
        respondButton.addActionListener(e -> respondToGrievance());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(respondButton);
        
        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Load initial data
        refreshGrievancesTable();
        
        return panel;
    }
    
    private void refreshApplicationsTable() {
        // Clear existing data
        DefaultTableModel model = (DefaultTableModel) applicationsTable.getModel();
        model.setRowCount(0);
        
        // Get all applications
        List<CertificateApplication> applications = applicationDAO.findAll();
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        for (CertificateApplication app : applications) {
            String applicationDate = app.getApplicationDate() != null ? 
                dateFormat.format(app.getApplicationDate()) : "N/A";
            
            model.addRow(new Object[]{
                app.getId(),
                app.getApplicationNumber(),
                app.getUserId(),
                app.getCertificateType(),
                app.getStatus(),
                app.getDescription(),
                applicationDate
            });
        }
    }
    
    private void refreshLogsTable(JTable table) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        
        List<ActivityLog> logs = activityLogDAO.findAll();
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        for (ActivityLog log : logs) {
            String timestamp = log.getTimestamp() != null ? 
                dateFormat.format(log.getTimestamp()) : "N/A";
            
            model.addRow(new Object[]{
                log.getAdminId(),
                log.getAction(),
                log.getDetails(),
                timestamp
            });
        }
    }
    
    private void refreshGrievancesTable() {
        // Clear existing data
        grievancesTableModel.setRowCount(0);
        
        // Get all grievances
        List<Grievance> grievances = grievanceDAO.findAll();
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        for (Grievance grievance : grievances) {
            CertificateApplication app = applicationDAO.findById(grievance.getApplicationId());
            String lastUpdated = grievance.getUpdatedAt() != null ? 
                dateFormat.format(grievance.getUpdatedAt()) : "N/A";
            
            grievancesTableModel.addRow(new Object[]{
                app != null ? app.getApplicationNumber() : "N/A",
                grievance.getUserId(),
                grievance.getSubject(),
                grievance.getDescription(),
                grievance.getStatus(),
                grievance.getAdminResponse() != null ? grievance.getAdminResponse() : "No response yet",
                lastUpdated
            });
        }
    }
    
    private void showSearchResults(List<CertificateApplication> results) {
        if (results.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "No applications found matching the search criteria",
                "No Results",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Show details for the first result in a box format
        CertificateApplication app = results.get(0);
        JDialog dialog = new JDialog(parentFrame, "Application Details", true);
        dialog.setLayout(new BorderLayout());
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        panel.add(new JLabel("Application Number: " + app.getApplicationNumber()), gbc);
        panel.add(new JLabel("Certificate Type: " + app.getCertificateType()), gbc);
        panel.add(new JLabel("Status: " + app.getStatus()), gbc);
        panel.add(new JLabel("Description: " + app.getDescription()), gbc);
        
        String applicationDate = app.getApplicationDate() != null ? 
            dateFormat.format(app.getApplicationDate()) : "N/A";
        panel.add(new JLabel("Application Date: " + applicationDate), gbc);
        
        String lastUpdated = app.getLastUpdated() != null ? 
            dateFormat.format(app.getLastUpdated()) : "N/A";
        panel.add(new JLabel("Last Updated: " + lastUpdated), gbc);
        
        // Add buttons for admin actions
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        
        JButton updateButton = new JButton("Update Status");
        updateButton.addActionListener(e -> {
            dialog.dispose();
            showUpdateStatusDialog(applicationsTable);
        });
        
        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> {
            dialog.dispose();
            deleteSelectedApplication(applicationsTable);
        });
        
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        
        panel.add(buttonPanel, gbc);
        
        dialog.add(panel, BorderLayout.CENTER);
        
        dialog.pack();
        dialog.setLocationRelativeTo(parentFrame);
        dialog.setVisible(true);
    }
    
    private void showRespondDialog(int grievanceId) {
        Grievance grievance = grievanceDAO.findById(grievanceId);
        
        if (grievance == null) {
            JOptionPane.showMessageDialog(this,
                "Grievance not found",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        JDialog dialog = new JDialog(parentFrame, "Respond to Grievance", true);
        dialog.setLayout(new BorderLayout());
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Show grievance details
        CertificateApplication app = applicationDAO.findById(grievance.getApplicationId());
        panel.add(new JLabel("Application: " + (app != null ? app.getCertificateType() : "N/A")), gbc);
        panel.add(new JLabel("Subject: " + grievance.getSubject()), gbc);
        panel.add(new JLabel("Description: " + grievance.getDescription()), gbc);
        
        // Status selection
        String[] statuses = {"OPEN", "IN_PROGRESS", "RESOLVED"};
        JComboBox<String> statusCombo = new JComboBox<>(statuses);
        statusCombo.setSelectedItem(grievance.getStatus());
        
        panel.add(new JLabel("Status:"), gbc);
        panel.add(statusCombo, gbc);
        
        // Admin response
        JTextArea responseArea = new JTextArea(4, 20);
        if (grievance.getAdminResponse() != null) {
            responseArea.setText(grievance.getAdminResponse());
        }
        
        panel.add(new JLabel("Response:"), gbc);
        panel.add(new JScrollPane(responseArea), gbc);
        
        JButton submitButton = new JButton("Submit Response");
        submitButton.addActionListener(e -> {
            String status = (String) statusCombo.getSelectedItem();
            String response = responseArea.getText().trim();
            
            if (response.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                    "Please enter a response",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            grievance.setStatus(status);
            grievance.setAdminResponse(response);
            grievance.setAdminId(currentUser.getId());
            
            if (grievanceDAO.update(grievance)) {
                // Log the activity
                ActivityLog log = new ActivityLog();
                log.setAdminId(currentUser.getId());
                log.setAction("RESPOND_GRIEVANCE");
                log.setDetails("Responded to grievance ID: " + grievanceId);
                log.setTimestamp(new Timestamp(System.currentTimeMillis()));
                
                // Set the application ID if available
                if (grievance.getApplicationId() > 0) {
                    log.setApplicationId(grievance.getApplicationId());
                }
                
                activityLogDAO.save(log);
                
                // Refresh both tables
                refreshGrievancesTable();
                JTable logsTable = getLogsTable();
                if (logsTable != null) {
                    refreshLogsTable(logsTable);
                }
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog,
                    "Failed to submit response",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        panel.add(submitButton, gbc);
        dialog.add(panel, BorderLayout.CENTER);
        
        dialog.pack();
        dialog.setLocationRelativeTo(parentFrame);
        dialog.setVisible(true);
    }
    
    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            parentFrame.getContentPane().removeAll();
            parentFrame.add(new LoginView(parentFrame));
            parentFrame.revalidate();
            parentFrame.repaint();
        }
    }

    private void showAddApplicationDialog() {
        JDialog dialog = new JDialog(parentFrame, "Add Application", true);
        dialog.setLayout(new BorderLayout());
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        JTextField appNumberField = new JTextField(20);
        JTextField userEmailField = new JTextField(20);
        JComboBox<String> certTypeCombo = new JComboBox<>(new String[]{
            "CASTE", "INCOME", "MARRIAGE", "BIRTH", "DEATH", "RESIDENCE",
            "NATIONALITY", "EMPLOYMENT", "PROPERTY", "DISABILITY"
        });
        JTextArea descriptionArea = new JTextArea(4, 20);
        JTextField amountField = new JTextField(20);
        JComboBox<String> paymentStatusCombo = new JComboBox<>(new String[]{"PENDING", "PAID", "REFUNDED"});
        
        panel.add(new JLabel("Application Number:"), gbc);
        panel.add(appNumberField, gbc);
        panel.add(new JLabel("Applicant Email:"), gbc);
        panel.add(userEmailField, gbc);
        panel.add(new JLabel("Certificate Type:"), gbc);
        panel.add(certTypeCombo, gbc);
        panel.add(new JLabel("Description:"), gbc);
        panel.add(new JScrollPane(descriptionArea), gbc);
        panel.add(new JLabel("Amount:"), gbc);
        panel.add(amountField, gbc);
        panel.add(new JLabel("Payment Status:"), gbc);
        panel.add(paymentStatusCombo, gbc);
        
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            try {
                String appNumber = appNumberField.getText();
                String userEmail = userEmailField.getText();
                String certType = (String) certTypeCombo.getSelectedItem();
                String description = descriptionArea.getText();
                String amountStr = amountField.getText();
                String paymentStatus = (String) paymentStatusCombo.getSelectedItem();
                
                // Validate inputs
                if (appNumber.isEmpty() || userEmail.isEmpty() || description.isEmpty() || amountStr.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog,
                        "Please fill in all fields",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Parse amount
                BigDecimal amount;
                try {
                    amount = new BigDecimal(amountStr);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog,
                        "Invalid amount format",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Check if application number already exists
                if (applicationDAO.findByApplicationNumber(appNumber) != null) {
                    JOptionPane.showMessageDialog(dialog,
                        "Application number already exists",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Find user by email
                UserDAO userDAO = new UserDAOImpl();
                User user = userDAO.findByEmail(userEmail);
                
                if (user == null) {
                    JOptionPane.showMessageDialog(dialog,
                        "User with email " + userEmail + " not found",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Create and save the application
                CertificateApplication app = new CertificateApplication();
                app.setApplicationNumber(appNumber);
                app.setUserId(user.getId());
                app.setCertificateType(certType);
                app.setDescription(description);
                app.setStatus("PENDING");
                app.setAdminId(currentUser.getId());
                app.setAmount(amount);
                app.setPaymentStatus(paymentStatus);
                app.setApplicationDate(new Date());
                
                if (applicationDAO.save(app)) {
                    // Log the activity
                    ActivityLog log = new ActivityLog();
                    log.setAdminId(currentUser.getId());
                    log.setAction("ADD_APPLICATION");
                    log.setDetails("Added new application: " + appNumber);
                    log.setTimestamp(new Timestamp(System.currentTimeMillis()));
                    
                    // Set the application ID
                    log.setApplicationId(app.getId());
                    
                    activityLogDAO.save(log);
                    
                    // Refresh both tables
                    refreshApplicationsTable();
                    JTable logsTable = getLogsTable();
                    if (logsTable != null) {
                        refreshLogsTable(logsTable);
                    }
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog,
                        "Failed to add application",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Error: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        panel.add(saveButton, gbc);
        dialog.add(panel, BorderLayout.CENTER);
        
        dialog.pack();
        dialog.setLocationRelativeTo(parentFrame);
        dialog.setVisible(true);
    }
    
    private void showUpdateStatusDialog(JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select an application to update",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int appId = (int) table.getValueAt(selectedRow, 0);
        CertificateApplication app = applicationDAO.findById(appId);
        
        if (app == null) {
            JOptionPane.showMessageDialog(this,
                "Application not found",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        JDialog dialog = new JDialog(parentFrame, "Update Status", true);
        dialog.setLayout(new BorderLayout());
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"PENDING", "APPROVED", "REJECTED"});
        JTextArea descriptionArea = new JTextArea(4, 20);
        descriptionArea.setText(app.getDescription());
        
        panel.add(new JLabel("Status:"), gbc);
        panel.add(statusCombo, gbc);
        panel.add(new JLabel("Description:"), gbc);
        panel.add(new JScrollPane(descriptionArea), gbc);
        
        JButton updateButton = new JButton("Update");
        updateButton.addActionListener(e -> {
            String newStatus = (String) statusCombo.getSelectedItem();
            String newDescription = descriptionArea.getText();
            
            if (applicationDAO.updateStatus(app.getId(), newStatus, newDescription)) {
                // Log the activity
                ActivityLog log = new ActivityLog();
                log.setAdminId(currentUser.getId());
                log.setAction("STATUS_CHANGE");
                log.setDetails("Changed application status to: " + newStatus);
                log.setTimestamp(new Timestamp(System.currentTimeMillis()));
                
                // Set the application ID
                log.setApplicationId(app.getId());
                
                activityLogDAO.save(log);
                
                JOptionPane.showMessageDialog(dialog,
                    "Application status updated successfully",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                
                // Refresh both tables
                refreshApplicationsTable();
                JTable logsTable = getLogsTable();
                if (logsTable != null) {
                    refreshLogsTable(logsTable);
                }
            } else {
                JOptionPane.showMessageDialog(dialog,
                    "Failed to update status",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        panel.add(updateButton, gbc);
        dialog.add(panel, BorderLayout.CENTER);
        
        dialog.pack();
        dialog.setLocationRelativeTo(parentFrame);
        dialog.setVisible(true);
    }
    
    private void deleteSelectedApplication(JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select an application to delete",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int appId = (int) table.getValueAt(selectedRow, 0);
        String appNumber = (String) table.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete application " + appNumber + "?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            if (applicationDAO.delete(appId)) {
                // Log the activity
                ActivityLog log = new ActivityLog();
                log.setAdminId(currentUser.getId());
                log.setAction("DELETE_APPLICATION");
                log.setDetails("Deleted application: " + appNumber);
                log.setTimestamp(new Timestamp(System.currentTimeMillis()));
                
                // Set the application ID
                log.setApplicationId(appId);
                
                activityLogDAO.save(log);
                
                JOptionPane.showMessageDialog(this,
                    "Application deleted successfully",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Refresh both tables
                refreshApplicationsTable();
                JTable logsTable = getLogsTable();
                if (logsTable != null) {
                    refreshLogsTable(logsTable);
                }
            } else {
                JOptionPane.showMessageDialog(this,
                    "Failed to delete application",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void searchApplications() {
        JDialog dialog = new JDialog(parentFrame, "Search Applications", true);
        dialog.setLayout(new BorderLayout());
        
        JPanel panel = new JPanel(new BorderLayout());
        
        // Create the search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        
        searchPanel.add(new JLabel("Application Number:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        
        // Add action listener for search
        searchButton.addActionListener(e -> {
            String appNumber = searchField.getText().trim();
            if (!appNumber.isEmpty()) {
                CertificateApplication app = applicationDAO.findByApplicationNumber(appNumber);
                if (app != null) {
                    List<CertificateApplication> results = new ArrayList<>();
                    results.add(app);
                    showSearchResults(results);
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog,
                        "No application found with number: " + appNumber,
                        "Not Found",
                        JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(dialog,
                    "Please enter an application number",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        panel.add(searchPanel, BorderLayout.NORTH);
        
        // Add a welcome message
        JPanel welcomePanel = new JPanel(new GridBagLayout());
        welcomePanel.setBackground(Color.WHITE);
        
        JLabel welcomeLabel = new JLabel("Search Applications");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        
        JLabel instructionLabel = new JLabel("Enter an application number above to search");
        instructionLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        welcomePanel.add(welcomeLabel);
        welcomePanel.add(Box.createVerticalStrut(20));
        welcomePanel.add(instructionLabel);
        
        panel.add(welcomePanel, BorderLayout.CENTER);
        
        dialog.add(panel, BorderLayout.CENTER);
        
        dialog.pack();
        dialog.setLocationRelativeTo(parentFrame);
        dialog.setVisible(true);
    }
    
    private void respondToGrievance() {
        int selectedRow = grievancesTable.getSelectedRow();
        if (selectedRow >= 0) {
            String appNumber = (String) grievancesTableModel.getValueAt(selectedRow, 0);
            CertificateApplication app = applicationDAO.findByApplicationNumber(appNumber);
            if (app != null) {
                List<Grievance> grievances = grievanceDAO.findByApplicationId(app.getId());
                if (!grievances.isEmpty()) {
                    showRespondDialog(grievances.get(0).getId());
                }
            }
        } else {
            JOptionPane.showMessageDialog(this,
                "Please select a grievance to respond to",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
        }
    }

    private JTable getLogsTable() {
        // Get the logs tab component
        Component logsTab = tabbedPane.getComponentAt(1); // Index 1 is the logs tab
        
        // Find the JScrollPane in the logs tab
        if (logsTab instanceof Container) {
            Container container = (Container) logsTab;
            for (Component comp : container.getComponents()) {
                if (comp instanceof JScrollPane) {
                    JScrollPane scrollPane = (JScrollPane) comp;
                    return (JTable) scrollPane.getViewport().getView();
                }
            }
        }
        
        return null;
    }
} 
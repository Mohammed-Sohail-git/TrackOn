package com.trackon.view;

import com.trackon.dao.CertificateApplicationDAO;
import com.trackon.dao.GrievanceDAO;
import com.trackon.dao.impl.CertificateApplicationDAOImpl;
import com.trackon.dao.impl.GrievanceDAOImpl;
import com.trackon.model.CertificateApplication;
import com.trackon.model.Grievance;
import com.trackon.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class UserDashboardView extends JPanel {
    private final JFrame parentFrame;
    private final User currentUser;
    private final CertificateApplicationDAO applicationDAO;
    private final GrievanceDAO grievanceDAO;
    private final JTabbedPane tabbedPane;
    
    private JTable applicationsTable;
    private DefaultTableModel applicationsTableModel;
    
    private JTable grievancesTable;
    private DefaultTableModel grievancesTableModel;
    
    public UserDashboardView(JFrame parentFrame, User user) {
        this.parentFrame = parentFrame;
        this.currentUser = user;
        this.applicationDAO = new CertificateApplicationDAOImpl();
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
        tabbedPane.addTab("Activity", createActivityTab());
        tabbedPane.addTab("Grievances", createGrievancesTab());
        
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    private JPanel createHomeTab() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Create the search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Track Application");
        
        searchPanel.add(new JLabel("Application Number:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        
        // Add action listener for search
        searchButton.addActionListener(e -> {
            String appNumber = searchField.getText().trim();
            if (!appNumber.isEmpty()) {
                CertificateApplication app = applicationDAO.findByApplicationNumber(appNumber);
                if (app != null && app.getUserId() == currentUser.getId()) {
                    showApplicationDetails(app);
                } else {
                    JOptionPane.showMessageDialog(this,
                        "No application found with number: " + appNumber,
                        "Not Found",
                        JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this,
                    "Please enter an application number",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        panel.add(searchPanel, BorderLayout.NORTH);
        
        // Add a welcome message
        JPanel welcomePanel = new JPanel(new GridBagLayout());
        welcomePanel.setBackground(Color.WHITE);
        
        JLabel welcomeLabel = new JLabel("Welcome to TrackOn");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        
        JLabel instructionLabel = new JLabel("Enter your application number above to track your application");
        instructionLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        welcomePanel.add(welcomeLabel);
        welcomePanel.add(Box.createVerticalStrut(20));
        welcomePanel.add(instructionLabel);
        
        panel.add(welcomePanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createActivityTab() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Create table model
        String[] columnNames = {"ID", "Application Number", "Certificate Type", "Status", "Application Date", "Description"};
        applicationsTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Create table
        applicationsTable = new JTable(applicationsTableModel);
        JScrollPane scrollPane = new JScrollPane(applicationsTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Load initial data
        refreshApplicationsTable();
        
        return panel;
    }
    
    private JPanel createGrievancesTab() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Create table model
        String[] columnNames = {"Application Number", "Subject", "Description", "Status", "Admin Response", "Last Updated"};
        grievancesTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        grievancesTable = new JTable(grievancesTableModel);
        JScrollPane scrollPane = new JScrollPane(grievancesTable);
        
        // Add button to create new grievance
        JButton createButton = new JButton("Raise Grievance");
        createButton.addActionListener(e -> createGrievance());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(createButton);
        
        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Load initial data
        refreshGrievancesTable();
        
        return panel;
    }
    
    private void refreshApplicationsTable() {
        applicationsTableModel.setRowCount(0);
        
        List<CertificateApplication> applications = applicationDAO.findByUserId(currentUser.getId());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        for (CertificateApplication app : applications) {
            String applicationDate = app.getApplicationDate() != null ? 
                dateFormat.format(app.getApplicationDate()) : "N/A";
            
            applicationsTableModel.addRow(new Object[]{
                app.getId(),
                app.getApplicationNumber(),
                app.getCertificateType(),
                app.getStatus(),
                applicationDate,
                app.getDescription()
            });
        }
    }
    
    private void refreshGrievancesTable() {
        // Clear existing data
        grievancesTableModel.setRowCount(0);
        
        // Get user's grievances
        List<Grievance> grievances = grievanceDAO.findByUserId(currentUser.getId());
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        for (Grievance grievance : grievances) {
            CertificateApplication app = applicationDAO.findById(grievance.getApplicationId());
            String lastUpdated = grievance.getUpdatedAt() != null ? 
                dateFormat.format(grievance.getUpdatedAt()) : "N/A";
            
            grievancesTableModel.addRow(new Object[]{
                app != null ? app.getApplicationNumber() : "N/A",
                grievance.getSubject(),
                grievance.getDescription(),
                grievance.getStatus(),
                grievance.getAdminResponse() != null ? grievance.getAdminResponse() : "No response yet",
                lastUpdated
            });
        }
    }
    
    private void showApplicationDetails(CertificateApplication app) {
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
        
        // Add a button to raise grievance if the application belongs to the current user
        if (app.getUserId() == currentUser.getId()) {
            JButton raiseGrievanceButton = new JButton("Raise Grievance");
            raiseGrievanceButton.addActionListener(e -> {
                dialog.dispose();
                createGrievance();
            });
            panel.add(raiseGrievanceButton, gbc);
        }
        
        dialog.add(panel, BorderLayout.CENTER);
        
        dialog.pack();
        dialog.setLocationRelativeTo(parentFrame);
        dialog.setVisible(true);
    }
    
    private void createGrievance() {
        JDialog dialog = new JDialog(parentFrame, "Raise Grievance", true);
        dialog.setLayout(new BorderLayout());
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Application selection
        JLabel appLabel = new JLabel("Select Application:");
        JComboBox<CertificateApplication> appComboBox = new JComboBox<>();
        
        // Load user's applications
        List<CertificateApplication> userApplications = applicationDAO.findByUserId(currentUser.getId());
        for (CertificateApplication app : userApplications) {
            appComboBox.addItem(app);
        }
        
        // Format the display of applications in the combo box
        appComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, 
                                                boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof CertificateApplication) {
                    CertificateApplication app = (CertificateApplication) value;
                    setText(app.getApplicationNumber() + " - " + app.getCertificateType() + 
                            " (" + app.getStatus() + ")");
                }
                return this;
            }
        });
        
        JTextField subjectField = new JTextField(20);
        JTextArea descriptionArea = new JTextArea(4, 20);
        
        panel.add(appLabel, gbc);
        panel.add(appComboBox, gbc);
        panel.add(new JLabel("Subject:"), gbc);
        panel.add(subjectField, gbc);
        panel.add(new JLabel("Description:"), gbc);
        panel.add(new JScrollPane(descriptionArea), gbc);
        
        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(e -> {
            CertificateApplication selectedApp = (CertificateApplication) appComboBox.getSelectedItem();
            String subject = subjectField.getText().trim();
            String description = descriptionArea.getText().trim();
            
            if (selectedApp == null) {
                JOptionPane.showMessageDialog(dialog,
                    "Please select an application",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (subject.isEmpty() || description.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                    "Please fill in all fields",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Grievance grievance = new Grievance();
            grievance.setUserId(currentUser.getId());
            grievance.setApplicationId(selectedApp.getId());
            grievance.setSubject(subject);
            grievance.setDescription(description);
            grievance.setStatus("OPEN");
            grievance.setAdminResponse(null);
            grievance.setAdminId(0);  // Set to 0 since no admin has been assigned yet
            
            if (grievanceDAO.save(grievance)) {
                JOptionPane.showMessageDialog(dialog,
                    "Grievance submitted successfully",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                
                // Refresh the grievances table
                refreshGrievancesTable();
            } else {
                JOptionPane.showMessageDialog(dialog,
                    "Failed to submit grievance",
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
} 
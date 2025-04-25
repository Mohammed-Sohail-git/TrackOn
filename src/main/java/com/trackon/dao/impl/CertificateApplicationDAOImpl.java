package com.trackon.dao.impl;

import com.trackon.config.DatabaseConfig;
import com.trackon.dao.CertificateApplicationDAO;
import com.trackon.model.CertificateApplication;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class CertificateApplicationDAOImpl implements CertificateApplicationDAO {
    
    @Override
    public CertificateApplication findById(int id) {
        String sql = "SELECT * FROM certificate_applications WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToApplication(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error finding application by ID: " + e.getMessage());
        }
        return null;
    }
    
    @Override
    public List<CertificateApplication> findAll() {
        List<CertificateApplication> applications = new ArrayList<>();
        String sql = "SELECT * FROM certificate_applications";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                applications.add(mapResultSetToApplication(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding all applications: " + e.getMessage());
        }
        return applications;
    }
    
    @Override
    public boolean save(CertificateApplication application) {
        // First try with all columns
        String sql = "INSERT INTO certificate_applications (application_number, user_id, certificate_type, status, " +
                    "description, admin_id, application_date) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, application.getApplicationNumber());
            stmt.setInt(2, application.getUserId());
            stmt.setString(3, application.getCertificateType());
            stmt.setString(4, application.getStatus());
            stmt.setString(5, application.getDescription());
            stmt.setInt(6, application.getAdminId());
            stmt.setTimestamp(7, new Timestamp(application.getApplicationDate().getTime()));
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    application.setId(generatedKeys.getInt(1));
                    
                    // Try to update amount and payment status if they exist
                    try {
                        String updateSql = "UPDATE certificate_applications SET amount = ?, payment_status = ? WHERE id = ?";
                        try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                            updateStmt.setBigDecimal(1, application.getAmount());
                            updateStmt.setString(2, application.getPaymentStatus());
                            updateStmt.setInt(3, application.getId());
                            updateStmt.executeUpdate();
                        }
                    } catch (SQLException e) {
                        // Ignore this error - the columns might not exist
                        System.err.println("Note: Amount and payment_status columns might not exist: " + e.getMessage());
                    }
                    
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error saving application: " + e.getMessage());
        }
        return false;
    }
    
    @Override
    public boolean update(CertificateApplication application) {
        String sql = "UPDATE certificate_applications SET application_number = ?, user_id = ?, " +
                    "certificate_type = ?, status = ?, description = ?, admin_id = ?, " +
                    "amount = ?, payment_status = ?, payment_date = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, application.getApplicationNumber());
            stmt.setInt(2, application.getUserId());
            stmt.setString(3, application.getCertificateType());
            stmt.setString(4, application.getStatus());
            stmt.setString(5, application.getDescription());
            stmt.setInt(6, application.getAdminId());
            stmt.setBigDecimal(7, application.getAmount());
            stmt.setString(8, application.getPaymentStatus());
            
            if (application.getPaymentDate() != null) {
                stmt.setTimestamp(9, new Timestamp(application.getPaymentDate().getTime()));
            } else {
                stmt.setNull(9, Types.TIMESTAMP);
            }
            
            stmt.setInt(10, application.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating application: " + e.getMessage());
        }
        return false;
    }
    
    @Override
    public boolean delete(int id) {
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);  // Start transaction
            
            // First delete associated activity logs
            String deleteLogsSql = "DELETE FROM activity_logs WHERE application_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteLogsSql)) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
            }
            
            // Then delete the certificate application
            String deleteAppSql = "DELETE FROM certificate_applications WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteAppSql)) {
                stmt.setInt(1, id);
                int result = stmt.executeUpdate();
                
                conn.commit();  // Commit transaction
                return result > 0;
            }
        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback();  // Rollback transaction on error
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.err.println("Error deleting application: " + e.getMessage());
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);  // Reset auto-commit
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    @Override
    public CertificateApplication findByApplicationNumber(String applicationNumber) {
        String sql = "SELECT * FROM certificate_applications WHERE application_number = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, applicationNumber);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToApplication(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error finding application by number: " + e.getMessage());
        }
        return null;
    }
    
    @Override
    public List<CertificateApplication> findByUserId(int userId) {
        List<CertificateApplication> applications = new ArrayList<>();
        String sql = "SELECT * FROM certificate_applications WHERE user_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                applications.add(mapResultSetToApplication(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding applications by user ID: " + e.getMessage());
        }
        return applications;
    }
    
    @Override
    public List<CertificateApplication> findByStatus(String status) {
        List<CertificateApplication> applications = new ArrayList<>();
        String sql = "SELECT * FROM certificate_applications WHERE status = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                applications.add(mapResultSetToApplication(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return applications;
    }
    
    @Override
    public List<CertificateApplication> findByAdminId(int adminId) {
        List<CertificateApplication> applications = new ArrayList<>();
        String sql = "SELECT * FROM certificate_applications WHERE admin_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, adminId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                applications.add(mapResultSetToApplication(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding applications by admin ID: " + e.getMessage());
        }
        return applications;
    }
    
    @Override
    public boolean updateStatus(int id, String status, String description) {
        String sql = "UPDATE certificate_applications SET status = ?, description = ?, last_updated = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setString(2, description);
            stmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            stmt.setInt(4, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating application status: " + e.getMessage());
        }
        return false;
    }
    
    @Override
    public List<CertificateApplication> findByCertificateType(String certificateType) {
        List<CertificateApplication> applications = new ArrayList<>();
        String sql = "SELECT * FROM certificate_applications WHERE certificate_type = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, certificateType);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                applications.add(mapResultSetToApplication(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding applications by certificate type: " + e.getMessage());
        }
        return applications;
    }
    
    @Override
    public List<CertificateApplication> findByApplicantName(String applicantName) {
        List<CertificateApplication> applications = new ArrayList<>();
        String sql = "SELECT * FROM certificate_applications WHERE applicant_name LIKE ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + applicantName + "%");
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                applications.add(mapResultSetToApplication(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return applications;
    }

    @Override
    public List<CertificateApplication> findByDateRange(Date startDate, Date endDate) {
        List<CertificateApplication> applications = new ArrayList<>();
        String sql = "SELECT * FROM certificate_applications WHERE application_date BETWEEN ? AND ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setTimestamp(1, new Timestamp(startDate.getTime()));
            stmt.setTimestamp(2, new Timestamp(endDate.getTime()));
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                applications.add(mapResultSetToApplication(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding applications by date range: " + e.getMessage());
        }
        return applications;
    }
    
    private CertificateApplication mapResultSetToApplication(ResultSet rs) throws SQLException {
        CertificateApplication application = new CertificateApplication();
        application.setId(rs.getInt("id"));
        application.setUserId(rs.getInt("user_id"));
        application.setCertificateType(rs.getString("certificate_type"));
        application.setStatus(rs.getString("status"));
        application.setApplicationNumber(rs.getString("application_number"));
        application.setDescription(rs.getString("description"));
        application.setApplicationDate(rs.getTimestamp("application_date"));
        application.setAdminId(rs.getInt("admin_id"));
        return application;
    }
} 
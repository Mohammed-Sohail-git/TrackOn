package com.trackon.dao.impl;

import com.trackon.config.DatabaseConfig;
import com.trackon.dao.GrievanceDAO;
import com.trackon.model.Grievance;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GrievanceDAOImpl implements GrievanceDAO {
    
    @Override
    public Grievance findById(int id) {
        String sql = "SELECT * FROM grievances WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToGrievance(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public List<Grievance> findAll() {
        List<Grievance> grievances = new ArrayList<>();
        String sql = "SELECT * FROM grievances";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                grievances.add(mapResultSetToGrievance(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return grievances;
    }
    
    @Override
    public boolean save(Grievance grievance) {
        String sql = "INSERT INTO grievances (user_id, application_id, subject, description, status, created_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, grievance.getUserId());
            stmt.setInt(2, grievance.getApplicationId());
            stmt.setString(3, grievance.getSubject());
            stmt.setString(4, grievance.getDescription());
            stmt.setString(5, grievance.getStatus());
            stmt.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error saving grievance: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean update(Grievance grievance) {
        String sql = "UPDATE grievances SET user_id = ?, application_id = ?, subject = ?, " +
                    "description = ?, status = ?, admin_response = ?, admin_id = ?, updated_at = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, grievance.getUserId());
            stmt.setInt(2, grievance.getApplicationId());
            stmt.setString(3, grievance.getSubject());
            stmt.setString(4, grievance.getDescription());
            stmt.setString(5, grievance.getStatus());
            stmt.setString(6, grievance.getAdminResponse());
            stmt.setInt(7, grievance.getAdminId());
            stmt.setTimestamp(8, new Timestamp(System.currentTimeMillis()));
            stmt.setInt(9, grievance.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM grievances WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public List<Grievance> findByUserId(int userId) {
        List<Grievance> grievances = new ArrayList<>();
        String sql = "SELECT * FROM grievances WHERE user_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                grievances.add(mapResultSetToGrievance(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return grievances;
    }
    
    @Override
    public List<Grievance> findByApplicationId(int applicationId) {
        List<Grievance> grievances = new ArrayList<>();
        String sql = "SELECT * FROM grievances WHERE application_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, applicationId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                grievances.add(mapResultSetToGrievance(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return grievances;
    }
    
    @Override
    public List<Grievance> findByStatus(String status) {
        List<Grievance> grievances = new ArrayList<>();
        String sql = "SELECT * FROM grievances WHERE status = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                grievances.add(mapResultSetToGrievance(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return grievances;
    }
    
    @Override
    public List<Grievance> findByAdminId(int adminId) {
        List<Grievance> grievances = new ArrayList<>();
        // Find grievances related to applications created by this admin
        String sql = "SELECT g.* FROM grievances g " +
                    "JOIN certificate_applications ca ON g.application_id = ca.id " +
                    "WHERE ca.admin_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, adminId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                grievances.add(mapResultSetToGrievance(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return grievances;
    }
    
    @Override
    public boolean updateStatus(int id, String status, String adminResponse) {
        String sql = "UPDATE grievances SET status = ?, admin_response = ?, updated_at = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setString(2, adminResponse);
            stmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            stmt.setInt(4, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private Grievance mapResultSetToGrievance(ResultSet rs) throws SQLException {
        Grievance grievance = new Grievance();
        grievance.setId(rs.getInt("id"));
        grievance.setUserId(rs.getInt("user_id"));
        grievance.setApplicationId(rs.getInt("application_id"));
        grievance.setSubject(rs.getString("subject"));
        grievance.setDescription(rs.getString("description"));
        grievance.setStatus(rs.getString("status"));
        grievance.setAdminResponse(rs.getString("admin_response"));
        
        // Convert Timestamp to Date for createdAt and updatedAt
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            grievance.setCreatedAt(new Date(createdAt.getTime()));
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            grievance.setUpdatedAt(new Date(updatedAt.getTime()));
        }
        
        // Handle NULL admin_id
        int adminId = rs.getInt("admin_id");
        if (rs.wasNull()) {
            grievance.setAdminId(0);
        } else {
            grievance.setAdminId(adminId);
        }
        
        return grievance;
    }
} 
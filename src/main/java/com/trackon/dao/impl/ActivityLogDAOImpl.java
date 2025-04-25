package com.trackon.dao.impl;

import com.trackon.config.DatabaseConfig;
import com.trackon.dao.ActivityLogDAO;
import com.trackon.model.ActivityLog;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ActivityLogDAOImpl implements ActivityLogDAO {
    
    @Override
    public ActivityLog findById(int id) {
        String sql = "SELECT * FROM activity_logs WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToActivityLog(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public List<ActivityLog> findAll() {
        List<ActivityLog> logs = new ArrayList<>();
        String sql = "SELECT * FROM activity_logs";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                logs.add(mapResultSetToActivityLog(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return logs;
    }
    
    @Override
    public boolean save(ActivityLog log) {
        String sql = "INSERT INTO activity_logs (admin_id, application_id, action, description, created_at) " +
                    "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, log.getAdminId());
            
            // Handle null or zero applicationId
            if (log.getApplicationId() > 0) {
                stmt.setInt(2, log.getApplicationId());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }
            
            stmt.setString(3, log.getAction());
            stmt.setString(4, log.getDetails());
            stmt.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error saving activity log: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean update(ActivityLog log) {
        String sql = "UPDATE activity_logs SET admin_id = ?, application_id = ?, " +
                    "action = ?, details = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, log.getAdminId());
            
            // Handle null or zero applicationId
            if (log.getApplicationId() > 0) {
                stmt.setInt(2, log.getApplicationId());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }
            
            stmt.setString(3, log.getAction());
            stmt.setString(4, log.getDetails());
            stmt.setInt(5, log.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM activity_logs WHERE id = ?";
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
    public List<ActivityLog> findByAdminId(int adminId) {
        List<ActivityLog> logs = new ArrayList<>();
        String sql = "SELECT * FROM activity_logs WHERE admin_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, adminId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                logs.add(mapResultSetToActivityLog(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding activity logs by admin ID: " + e.getMessage());
        }
        return logs;
    }
    
    @Override
    public List<ActivityLog> findByApplicationId(int applicationId) {
        List<ActivityLog> logs = new ArrayList<>();
        String sql = "SELECT * FROM activity_logs WHERE application_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, applicationId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                logs.add(mapResultSetToActivityLog(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return logs;
    }
    
    @Override
    public List<ActivityLog> findByAction(String action) {
        List<ActivityLog> logs = new ArrayList<>();
        String sql = "SELECT * FROM activity_logs WHERE action = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, action);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                logs.add(mapResultSetToActivityLog(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return logs;
    }
    
    private ActivityLog mapResultSetToActivityLog(ResultSet rs) throws SQLException {
        ActivityLog log = new ActivityLog();
        log.setId(rs.getInt("id"));
        log.setAdminId(rs.getInt("admin_id"));
        log.setApplicationId(rs.getInt("application_id"));
        log.setAction(rs.getString("action"));
        log.setDetails(rs.getString("description"));
        log.setTimestamp(rs.getTimestamp("created_at"));
        return log;
    }
} 
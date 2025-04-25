package com.trackon.dao;

import com.trackon.model.Grievance;
import java.util.List;

public interface GrievanceDAO extends BaseDAO<Grievance> {
    List<Grievance> findByUserId(int userId);
    List<Grievance> findByApplicationId(int applicationId);
    List<Grievance> findByStatus(String status);
    List<Grievance> findByAdminId(int adminId);
    boolean updateStatus(int id, String status, String adminResponse);
} 
package com.trackon.dao;

import com.trackon.model.ActivityLog;
import java.util.List;

public interface ActivityLogDAO extends BaseDAO<ActivityLog> {
    List<ActivityLog> findByAdminId(int adminId);
    List<ActivityLog> findByApplicationId(int applicationId);
    List<ActivityLog> findByAction(String action);
} 
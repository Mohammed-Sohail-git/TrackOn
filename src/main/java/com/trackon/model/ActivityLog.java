package com.trackon.model;

import java.util.Date;

public class ActivityLog {
    private int id;
    private int adminId;
    private int applicationId = 0; // Default to 0 to indicate no application
    private String action; // ADD, UPDATE, DELETE, STATUS_CHANGE
    private String details;
    private Date timestamp;
    
    public ActivityLog() {}
    
    public ActivityLog(int adminId, int applicationId, String action, String details) {
        this.adminId = adminId;
        this.applicationId = applicationId;
        this.action = action;
        this.details = details;
        this.timestamp = new Date();
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getAdminId() {
        return adminId;
    }
    
    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }
    
    public int getApplicationId() {
        return applicationId;
    }
    
    public void setApplicationId(int applicationId) {
        this.applicationId = applicationId;
    }
    
    public String getAction() {
        return action;
    }
    
    public void setAction(String action) {
        this.action = action;
    }
    
    public String getDetails() {
        return details;
    }
    
    public void setDetails(String details) {
        this.details = details;
    }
    
    public Date getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
} 
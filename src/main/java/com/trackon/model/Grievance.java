package com.trackon.model;

import java.util.Date;

public class Grievance {
    private int id;
    private int userId;
    private int applicationId;
    private String subject;
    private String description;
    private String status;
    private String adminResponse;
    private Date createdAt;
    private Date updatedAt;
    private int adminId;
    
    public Grievance() {}
    
    public Grievance(int userId, int applicationId, String subject, String description, String status) {
        this.userId = userId;
        this.applicationId = applicationId;
        this.subject = subject;
        this.description = description;
        this.status = status;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public int getApplicationId() { return applicationId; }
    public void setApplicationId(int applicationId) { this.applicationId = applicationId; }
    
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getAdminResponse() { return adminResponse; }
    public void setAdminResponse(String adminResponse) { this.adminResponse = adminResponse; }
    
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    
    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
    
    public int getAdminId() { return adminId; }
    public void setAdminId(int adminId) { this.adminId = adminId; }
} 
package com.trackon.model;

import java.util.Date;
import java.math.BigDecimal;

public class CertificateApplication {
    private int id;
    private String applicationNumber;
    private String applicantName;
    private String certificateType; // CASTE, INCOME, MARRIAGE, etc.
    private String status; // PENDING, APPROVED, REJECTED
    private String description;
    private BigDecimal amount;
    private String paymentStatus;
    private Date paymentDate;
    private Date applicationDate;
    private Date submissionDate;
    private Date lastUpdated;
    private int userId;
    private int adminId;
    
    public CertificateApplication() {}
    
    public CertificateApplication(int id, String applicationNumber, String applicantName, 
                                String certificateType, String status, String description,
                                BigDecimal amount, String paymentStatus, Date paymentDate,
                                Date applicationDate, Date submissionDate, Date lastUpdated, 
                                int userId, int adminId) {
        this.id = id;
        this.applicationNumber = applicationNumber;
        this.applicantName = applicantName;
        this.certificateType = certificateType;
        this.status = status;
        this.description = description;
        this.amount = amount;
        this.paymentStatus = paymentStatus;
        this.paymentDate = paymentDate;
        this.applicationDate = applicationDate;
        this.submissionDate = submissionDate;
        this.lastUpdated = lastUpdated;
        this.userId = userId;
        this.adminId = adminId;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getApplicationNumber() {
        return applicationNumber;
    }
    
    public void setApplicationNumber(String applicationNumber) {
        this.applicationNumber = applicationNumber;
    }
    
    public String getApplicantName() {
        return applicantName;
    }
    
    public void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }
    
    public String getCertificateType() {
        return certificateType;
    }
    
    public void setCertificateType(String certificateType) {
        this.certificateType = certificateType;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public String getPaymentStatus() {
        return paymentStatus;
    }
    
    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
    
    public Date getPaymentDate() {
        return paymentDate;
    }
    
    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }
    
    public Date getApplicationDate() {
        return applicationDate;
    }
    
    public void setApplicationDate(Date applicationDate) {
        this.applicationDate = applicationDate;
    }
    
    public Date getSubmissionDate() {
        return submissionDate;
    }
    
    public void setSubmissionDate(Date submissionDate) {
        this.submissionDate = submissionDate;
    }
    
    public Date getLastUpdated() {
        return lastUpdated;
    }
    
    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public int getAdminId() {
        return adminId;
    }
    
    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }
} 
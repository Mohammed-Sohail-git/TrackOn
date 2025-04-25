package com.trackon.dao;

import com.trackon.model.CertificateApplication;
import java.util.Date;
import java.util.List;

public interface CertificateApplicationDAO extends BaseDAO<CertificateApplication> {
    CertificateApplication findByApplicationNumber(String applicationNumber);
    List<CertificateApplication> findByUserId(int userId);
    List<CertificateApplication> findByStatus(String status);
    List<CertificateApplication> findByAdminId(int adminId);
    List<CertificateApplication> findByCertificateType(String certificateType);
    boolean updateStatus(int id, String status, String description);
    List<CertificateApplication> findByDateRange(Date startDate, Date endDate);
    List<CertificateApplication> findByApplicantName(String applicantName);
} 
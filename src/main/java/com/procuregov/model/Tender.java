package com.procuregov.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Tender {
    private int tenderId;
    private String referenceNumber;
    private String title;
    private String category;
    private String description;
    private BigDecimal estimatedValue;
    private Timestamp closingDatetime;
    private String status;
    private String noticePdfPath;
    private int createdBy;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    // Constructors
    public Tender() {}
    
    public Tender(String referenceNumber, String title, String category, String description, 
                  BigDecimal estimatedValue, Timestamp closingDatetime, String noticePdfPath, int createdBy) {
        this.referenceNumber = referenceNumber;
        this.title = title;
        this.category = category;
        this.description = description;
        this.estimatedValue = estimatedValue;
        this.closingDatetime = closingDatetime;
        this.noticePdfPath = noticePdfPath;
        this.createdBy = createdBy;
        this.status = "DRAFT";
    }
    
    // Getters and Setters
    public int getTenderId() { return tenderId; }
    public void setTenderId(int tenderId) { this.tenderId = tenderId; }
    
    public String getReferenceNumber() { return referenceNumber; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public BigDecimal getEstimatedValue() { return estimatedValue; }
    public void setEstimatedValue(BigDecimal estimatedValue) { this.estimatedValue = estimatedValue; }
    
    public Timestamp getClosingDatetime() { return closingDatetime; }
    public void setClosingDatetime(Timestamp closingDatetime) { this.closingDatetime = closingDatetime; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getNoticePdfPath() { return noticePdfPath; }
    public void setNoticePdfPath(String noticePdfPath) { this.noticePdfPath = noticePdfPath; }
    
    public int getCreatedBy() { return createdBy; }
    public void setCreatedBy(int createdBy) { this.createdBy = createdBy; }
    
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    
    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
}

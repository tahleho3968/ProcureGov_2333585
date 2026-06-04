package com.procuregov.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Bid {
    private int bidId;
    private int tenderId;
    private int supplierId;
    private BigDecimal bidAmount;
    private String technicalCompliance;
    private int deliveryTimeline;
    private String supportingDocPath;
    private Timestamp submissionDatetime;
    private boolean isWinningBid;
    
    // Additional fields for display
    private int technicalScore; // Temporary storage for evaluation
    private String supplierName;
    private String registrationNumber;
    
    // Constructors
    public Bid() {}
    
    public Bid(int tenderId, int supplierId, BigDecimal bidAmount, 
               String technicalCompliance, int deliveryTimeline, String supportingDocPath) {
        this.tenderId = tenderId;
        this.supplierId = supplierId;
        this.bidAmount = bidAmount;
        this.technicalCompliance = technicalCompliance;
        this.deliveryTimeline = deliveryTimeline;
        this.supportingDocPath = supportingDocPath;
        this.isWinningBid = false;
    }
    
    // Getters and Setters
    public int getBidId() { return bidId; }
    public void setBidId(int bidId) { this.bidId = bidId; }
    
    public int getTenderId() { return tenderId; }
    public void setTenderId(int tenderId) { this.tenderId = tenderId; }
    
    public int getSupplierId() { return supplierId; }
    public void setSupplierId(int supplierId) { this.supplierId = supplierId; }
    
    public BigDecimal getBidAmount() { return bidAmount; }
    public void setBidAmount(BigDecimal bidAmount) { this.bidAmount = bidAmount; }
    
    public String getTechnicalCompliance() { return technicalCompliance; }
    public void setTechnicalCompliance(String technicalCompliance) { this.technicalCompliance = technicalCompliance; }
    
    public int getDeliveryTimeline() { return deliveryTimeline; }
    public void setDeliveryTimeline(int deliveryTimeline) { this.deliveryTimeline = deliveryTimeline; }
    
    public String getSupportingDocPath() { return supportingDocPath; }
    public void setSupportingDocPath(String supportingDocPath) { this.supportingDocPath = supportingDocPath; }
    
    public Timestamp getSubmissionDatetime() { return submissionDatetime; }
    public void setSubmissionDatetime(Timestamp submissionDatetime) { this.submissionDatetime = submissionDatetime; }
    
    public boolean isWinningBid() { return isWinningBid; }
    public void setWinningBid(boolean winningBid) { isWinningBid = winningBid; }
    
    public int getTechnicalScore() { return technicalScore; }
    public void setTechnicalScore(int technicalScore) { this.technicalScore = technicalScore; }
    
    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }
    
    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }
}

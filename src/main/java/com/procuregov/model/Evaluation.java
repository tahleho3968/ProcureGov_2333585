package com.procuregov.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Evaluation {
    private int evaluationId;
    private int bidId;
    private int evaluatorId;
    private BigDecimal priceScore;
    private BigDecimal technicalScore;
    private BigDecimal deliveryScore;
    private BigDecimal weightedTotal;
    private Timestamp submittedAt;
    
    // Constructors
    public Evaluation() {}
    
    // Getters and Setters
    public int getEvaluationId() { return evaluationId; }
    public void setEvaluationId(int evaluationId) { this.evaluationId = evaluationId; }
    
    public int getBidId() { return bidId; }
    public void setBidId(int bidId) { this.bidId = bidId; }
    
    public int getEvaluatorId() { return evaluatorId; }
    public void setEvaluatorId(int evaluatorId) { this.evaluatorId = evaluatorId; }
    
    public BigDecimal getPriceScore() { return priceScore; }
    public void setPriceScore(BigDecimal priceScore) { this.priceScore = priceScore; }
    
    public BigDecimal getTechnicalScore() { return technicalScore; }
    public void setTechnicalScore(BigDecimal technicalScore) { this.technicalScore = technicalScore; }
    
    public BigDecimal getDeliveryScore() { return deliveryScore; }
    public void setDeliveryScore(BigDecimal deliveryScore) { this.deliveryScore = deliveryScore; }
    
    public BigDecimal getWeightedTotal() { return weightedTotal; }
    public void setWeightedTotal(BigDecimal weightedTotal) { this.weightedTotal = weightedTotal; }
    
    public Timestamp getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(Timestamp submittedAt) { this.submittedAt = submittedAt; }
}

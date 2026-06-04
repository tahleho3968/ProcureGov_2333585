package com.procuregov.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Award {
    private int awardId;
    private int tenderId;
    private int winningBidId;
    private BigDecimal awardedValue;
    private String justification;
    private Timestamp awardDate;
    private int awardedBy;
    
    public Award() {}
    
    public Award(int tenderId, int winningBidId, BigDecimal awardedValue, String justification, int awardedBy) {
        this.tenderId = tenderId;
        this.winningBidId = winningBidId;
        this.awardedValue = awardedValue;
        this.justification = justification;
        this.awardedBy = awardedBy;
    }
    
    // Getters and Setters
    public int getAwardId() { return awardId; }
    public void setAwardId(int awardId) { this.awardId = awardId; }
    
    public int getTenderId() { return tenderId; }
    public void setTenderId(int tenderId) { this.tenderId = tenderId; }
    
    public int getWinningBidId() { return winningBidId; }
    public void setWinningBidId(int winningBidId) { this.winningBidId = winningBidId; }
    
    public BigDecimal getAwardedValue() { return awardedValue; }
    public void setAwardedValue(BigDecimal awardedValue) { this.awardedValue = awardedValue; }
    
    public String getJustification() { return justification; }
    public void setJustification(String justification) { this.justification = justification; }
    
    public Timestamp getAwardDate() { return awardDate; }
    public void setAwardDate(Timestamp awardDate) { this.awardDate = awardDate; }
    
    public int getAwardedBy() { return awardedBy; }
    public void setAwardedBy(int awardedBy) { this.awardedBy = awardedBy; }
}

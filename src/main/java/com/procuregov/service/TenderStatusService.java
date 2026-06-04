package com.procuregov.service;

import com.procuregov.dao.TenderDAO;
import com.procuregov.dao.TenderDAOImpl;
import com.procuregov.model.Tender;

import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;

public class TenderStatusService {
    
    private static final Logger logger = Logger.getLogger(TenderStatusService.class.getName());
    private TenderDAO tenderDAO;
    
    public TenderStatusService() {
        this.tenderDAO = new TenderDAOImpl();
    }
    
    /**
     * Check all OPEN tenders and close those whose closing date has passed
     */
    public void checkAndCloseExpiredTenders() {
        List<Tender> openTenders = tenderDAO.findOpenTenders();
        LocalDateTime now = LocalDateTime.now();
        int closedCount = 0;
        
        for (Tender tender : openTenders) {
            LocalDateTime closingDate = tender.getClosingDatetime().toLocalDateTime();
            
            if (now.isAfter(closingDate)) {
                boolean updated = tenderDAO.updateTenderStatus(tender.getTenderId(), "CLOSED");
                if (updated) {
                    closedCount++;
                    logger.info("Tender " + tender.getReferenceNumber() + " automatically closed (deadline passed)");
                }
            }
        }
        
        if (closedCount > 0) {
            logger.info("Auto-closed " + closedCount + " expired tenders");
        }
    }
    
    /**
     * Check if a specific tender is expired and close it if needed
     */
    public boolean checkAndCloseTender(int tenderId) {
        Tender tender = tenderDAO.findById(tenderId);
        if (tender != null && "OPEN".equals(tender.getStatus())) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime closingDate = tender.getClosingDatetime().toLocalDateTime();
            
            if (now.isAfter(closingDate)) {
                return tenderDAO.updateTenderStatus(tenderId, "CLOSED");
            }
        }
        return false;
    }
}

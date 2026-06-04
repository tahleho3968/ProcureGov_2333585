package com.procuregov.service;

import com.procuregov.dao.TenderDAO;
import com.procuregov.dao.TenderDAOImpl;
import java.time.Year;

public class ReferenceGenerator {
    
    private static TenderDAO tenderDAO = new TenderDAOImpl();
    
    public static String generateReferenceNumber() {
        int year = Year.now().getValue();
        
        // Get the latest reference number for this year from database
        String latestRef = tenderDAO.getLatestReferenceNumberForYear(year);
        
        int sequence = 1;
        if (latestRef != null && latestRef.length() >= 12) {
            try {
                // Extract sequence number from MPW-YYYY-NNNN format
                String seqStr = latestRef.substring(9); // Get last 4 digits
                sequence = Integer.parseInt(seqStr) + 1;
            } catch (NumberFormatException e) {
                sequence = 1;
            }
        }
        
        return String.format("MPW-%d-%04d", year, sequence);
    }
}

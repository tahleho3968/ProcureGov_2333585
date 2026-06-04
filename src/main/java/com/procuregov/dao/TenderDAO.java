package com.procuregov.dao;

import com.procuregov.model.Tender;
import java.util.List;

public interface TenderDAO {
    boolean saveTender(Tender tender);
    Tender findById(int tenderId);
    Tender findByReferenceNumber(String referenceNumber);
    List<Tender> findAllTenders();
    List<Tender> findTendersByStatus(String status);
    List<Tender> findTendersByCategory(String category);
    List<Tender> findTendersByOfficer(int officerId);
    boolean updateTender(Tender tender);
    boolean updateTenderStatus(int tenderId, String status);
    boolean deleteTender(int tenderId);
    boolean isClosingDatePassed(int tenderId);
    List<Tender> findOpenTenders();
    String getLatestReferenceNumberForYear(int year);
}

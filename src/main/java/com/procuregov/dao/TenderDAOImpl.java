package com.procuregov.dao;

import com.procuregov.model.Tender;
import com.procuregov.db.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class TenderDAOImpl implements TenderDAO {
    
    private static final Logger logger = Logger.getLogger(TenderDAOImpl.class.getName());
    
    @Override
    public boolean saveTender(Tender tender) {
        String sql = "INSERT INTO tenders (reference_number, title, category, description, " +
                     "estimated_value, closing_datetime, status, notice_pdf_path, created_by) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, tender.getReferenceNumber());
            pstmt.setString(2, tender.getTitle());
            pstmt.setString(3, tender.getCategory());
            pstmt.setString(4, tender.getDescription());
            pstmt.setBigDecimal(5, tender.getEstimatedValue());
            pstmt.setTimestamp(6, tender.getClosingDatetime());
            pstmt.setString(7, tender.getStatus());
            pstmt.setString(8, tender.getNoticePdfPath());
            pstmt.setInt(9, tender.getCreatedBy());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    tender.setTenderId(generatedKeys.getInt(1));
                }
                return true;
            }
            return false;
            
        } catch (SQLException e) {
            logger.severe("Error saving tender: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public Tender findById(int tenderId) {
        String sql = "SELECT * FROM tenders WHERE tender_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, tenderId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractTenderFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            logger.severe("Error finding tender by ID: " + e.getMessage());
        }
        return null;
    }
    
    @Override
    public Tender findByReferenceNumber(String referenceNumber) {
        String sql = "SELECT * FROM tenders WHERE reference_number = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, referenceNumber);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractTenderFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            logger.severe("Error finding tender by reference: " + e.getMessage());
        }
        return null;
    }
    
    @Override
    public List<Tender> findAllTenders() {
        List<Tender> tenders = new ArrayList<>();
        String sql = "SELECT * FROM tenders ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                tenders.add(extractTenderFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            logger.severe("Error finding all tenders: " + e.getMessage());
        }
        return tenders;
    }
    
    @Override
    public List<Tender> findTendersByStatus(String status) {
        List<Tender> tenders = new ArrayList<>();
        String sql = "SELECT * FROM tenders WHERE status = ? ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                tenders.add(extractTenderFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            logger.severe("Error finding tenders by status: " + e.getMessage());
        }
        return tenders;
    }
    
    @Override
    public List<Tender> findTendersByCategory(String category) {
        List<Tender> tenders = new ArrayList<>();
        String sql = "SELECT * FROM tenders WHERE category = ? ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, category);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                tenders.add(extractTenderFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            logger.severe("Error finding tenders by category: " + e.getMessage());
        }
        return tenders;
    }
    
    @Override
    public List<Tender> findTendersByOfficer(int officerId) {
        List<Tender> tenders = new ArrayList<>();
        String sql = "SELECT * FROM tenders WHERE created_by = ? ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, officerId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                tenders.add(extractTenderFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            logger.severe("Error finding tenders by officer: " + e.getMessage());
        }
        return tenders;
    }
    
    @Override
    public boolean updateTender(Tender tender) {
        String sql = "UPDATE tenders SET title=?, category=?, description=?, estimated_value=?, " +
                     "closing_datetime=?, notice_pdf_path=? WHERE tender_id=? AND status='DRAFT'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, tender.getTitle());
            pstmt.setString(2, tender.getCategory());
            pstmt.setString(3, tender.getDescription());
            pstmt.setBigDecimal(4, tender.getEstimatedValue());
            pstmt.setTimestamp(5, tender.getClosingDatetime());
            pstmt.setString(6, tender.getNoticePdfPath());
            pstmt.setInt(7, tender.getTenderId());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            logger.severe("Error updating tender: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean updateTenderStatus(int tenderId, String status) {
        String sql = "UPDATE tenders SET status = ? WHERE tender_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            pstmt.setInt(2, tenderId);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            logger.severe("Error updating tender status: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean deleteTender(int tenderId) {
        String sql = "DELETE FROM tenders WHERE tender_id = ? AND status = 'DRAFT'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, tenderId);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            logger.severe("Error deleting tender: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean isClosingDatePassed(int tenderId) {
        String sql = "SELECT closing_datetime < NOW() as is_passed FROM tenders WHERE tender_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, tenderId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getBoolean("is_passed");
            }
            
        } catch (SQLException e) {
            logger.severe("Error checking closing date: " + e.getMessage());
        }
        return false;
    }
    
    @Override
    public List<Tender> findOpenTenders() {
        List<Tender> tenders = new ArrayList<>();
        String sql = "SELECT * FROM tenders WHERE status = 'OPEN' AND closing_datetime > NOW() ORDER BY closing_datetime ASC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                tenders.add(extractTenderFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            logger.severe("Error finding open tenders: " + e.getMessage());
        }
        return tenders;
    }

   @Override
   public String getLatestReferenceNumberForYear(int year) {
       String sql = "SELECT reference_number FROM tenders WHERE reference_number LIKE ? ORDER BY tender_id DESC LIMIT 1";
    
       try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
           pstmt.setString(1, "MPW-" + year + "-%");
           ResultSet rs = pstmt.executeQuery();
        
           if (rs.next()) {
               return rs.getString("reference_number");
           }
        
       } catch (SQLException e) {
           logger.severe("Error getting latest reference number: " + e.getMessage());
       }
       return null;
   } 
    
    private Tender extractTenderFromResultSet(ResultSet rs) throws SQLException {
        Tender tender = new Tender();
        tender.setTenderId(rs.getInt("tender_id"));
        tender.setReferenceNumber(rs.getString("reference_number"));
        tender.setTitle(rs.getString("title"));
        tender.setCategory(rs.getString("category"));
        tender.setDescription(rs.getString("description"));
        tender.setEstimatedValue(rs.getBigDecimal("estimated_value"));
        tender.setClosingDatetime(rs.getTimestamp("closing_datetime"));
        tender.setStatus(rs.getString("status"));
        tender.setNoticePdfPath(rs.getString("notice_pdf_path"));
        tender.setCreatedBy(rs.getInt("created_by"));
        tender.setCreatedAt(rs.getTimestamp("created_at"));
        tender.setUpdatedAt(rs.getTimestamp("updated_at"));
        return tender;
    }
}

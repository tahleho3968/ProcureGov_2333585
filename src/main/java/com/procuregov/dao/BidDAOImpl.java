package com.procuregov.dao;

import com.procuregov.model.Bid;
import com.procuregov.db.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class BidDAOImpl implements BidDAO {
    
    private static final Logger logger = Logger.getLogger(BidDAOImpl.class.getName());
    
    @Override
    public boolean saveBid(Bid bid) {
        String sql = "INSERT INTO bids (tender_id, supplier_id, bid_amount, technical_compliance, " +
                     "delivery_timeline, supporting_doc_path) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, bid.getTenderId());
            pstmt.setInt(2, bid.getSupplierId());
            pstmt.setBigDecimal(3, bid.getBidAmount());
            pstmt.setString(4, bid.getTechnicalCompliance());
            pstmt.setInt(5, bid.getDeliveryTimeline());
            pstmt.setString(6, bid.getSupportingDocPath());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    bid.setBidId(generatedKeys.getInt(1));
                }
                return true;
            }
            return false;
            
        } catch (SQLException e) {
            logger.severe("Error saving bid: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public Bid findById(int bidId) {
        String sql = "SELECT * FROM bids WHERE bid_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, bidId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractBidFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            logger.severe("Error finding bid by ID: " + e.getMessage());
        }
        return null;
    }
    
    @Override
    public List<Bid> findBidsByTender(int tenderId) {
        List<Bid> bids = new ArrayList<>();
        String sql = "SELECT * FROM bids WHERE tender_id = ? ORDER BY bid_amount ASC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, tenderId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                bids.add(extractBidFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            logger.severe("Error finding bids by tender: " + e.getMessage());
        }
        return bids;
    }
    
    @Override
    public List<Bid> findBidsBySupplier(int supplierId) {
        List<Bid> bids = new ArrayList<>();
        String sql = "SELECT b.*, t.title, t.reference_number, t.status as tender_status " +
                     "FROM bids b JOIN tenders t ON b.tender_id = t.tender_id " +
                     "WHERE b.supplier_id = ? ORDER BY b.submission_datetime DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, supplierId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Bid bid = extractBidFromResultSet(rs);
                bids.add(bid);
            }
            
        } catch (SQLException e) {
            logger.severe("Error finding bids by supplier: " + e.getMessage());
        }
        return bids;
    }
    
    @Override
    public boolean hasSupplierBidOnTender(int tenderId, int supplierId) {
        String sql = "SELECT COUNT(*) FROM bids WHERE tender_id = ? AND supplier_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, tenderId);
            pstmt.setInt(2, supplierId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            logger.severe("Error checking existing bid: " + e.getMessage());
        }
        return false;
    }
    
    @Override
    public boolean updateBid(Bid bid) {
        String sql = "UPDATE bids SET bid_amount=?, technical_compliance=?, delivery_timeline=?, " +
                     "supporting_doc_path=?, is_winning_bid=? WHERE bid_id=?";
    
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
            pstmt.setBigDecimal(1, bid.getBidAmount());
            pstmt.setString(2, bid.getTechnicalCompliance());
            pstmt.setInt(3, bid.getDeliveryTimeline());
            pstmt.setString(4, bid.getSupportingDocPath());
            pstmt.setBoolean(5, bid.isWinningBid());
            pstmt.setInt(6, bid.getBidId());
        
            return pstmt.executeUpdate() > 0;
        
        } catch (SQLException e) {
            logger.severe("Error updating bid: " + e.getMessage());
            return false;
        }
    }    
    @Override
    public boolean deleteBid(int bidId) {
        String sql = "DELETE FROM bids WHERE bid_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, bidId);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            logger.severe("Error deleting bid: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public int getBidCountForTender(int tenderId) {
        String sql = "SELECT COUNT(*) FROM bids WHERE tender_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, tenderId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            logger.severe("Error counting bids: " + e.getMessage());
        }
        return 0;
    }
    
    @Override
    public List<Bid> findBidsByTenderWithSupplierDetails(int tenderId) {
        List<Bid> bids = new ArrayList<>();
        String sql = "SELECT b.*, u.full_name as supplier_name, u.registration_number " +
                     "FROM bids b JOIN users u ON b.supplier_id = u.user_id " +
                     "WHERE b.tender_id = ? ORDER BY b.bid_amount ASC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, tenderId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Bid bid = extractBidFromResultSet(rs);
                bid.setSupplierName(rs.getString("supplier_name"));
                bid.setRegistrationNumber(rs.getString("registration_number"));
                bids.add(bid);
            }
            
        } catch (SQLException e) {
            logger.severe("Error finding bids with supplier details: " + e.getMessage());
        }
        return bids;
    }
    
    private Bid extractBidFromResultSet(ResultSet rs) throws SQLException {
        Bid bid = new Bid();
        bid.setBidId(rs.getInt("bid_id"));
        bid.setTenderId(rs.getInt("tender_id"));
        bid.setSupplierId(rs.getInt("supplier_id"));
        bid.setBidAmount(rs.getBigDecimal("bid_amount"));
        bid.setTechnicalCompliance(rs.getString("technical_compliance"));
        bid.setDeliveryTimeline(rs.getInt("delivery_timeline"));
        bid.setSupportingDocPath(rs.getString("supporting_doc_path"));
        bid.setSubmissionDatetime(rs.getTimestamp("submission_datetime"));
        bid.setWinningBid(rs.getBoolean("is_winning_bid"));
        return bid;
    }
}

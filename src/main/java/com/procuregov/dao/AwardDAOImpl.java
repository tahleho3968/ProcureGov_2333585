package com.procuregov.dao;

import com.procuregov.model.Award;
import com.procuregov.db.DatabaseConnection;
import java.sql.*;
import java.util.logging.Logger;

public class AwardDAOImpl implements AwardDAO {
    
    private static final Logger logger = Logger.getLogger(AwardDAOImpl.class.getName());
    
    @Override
    public boolean saveAward(Award award) {
        String sql = "INSERT INTO awards (tender_id, winning_bid_id, awarded_value, justification, awarded_by) " +
                     "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, award.getTenderId());
            pstmt.setInt(2, award.getWinningBidId());
            pstmt.setBigDecimal(3, award.getAwardedValue());
            pstmt.setString(4, award.getJustification());
            pstmt.setInt(5, award.getAwardedBy());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    award.setAwardId(generatedKeys.getInt(1));
                }
                return true;
            }
            return false;
            
        } catch (SQLException e) {
            logger.severe("Error saving award: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public Award findByTenderId(int tenderId) {
        String sql = "SELECT * FROM awards WHERE tender_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, tenderId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractAwardFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            logger.severe("Error finding award by tender ID: " + e.getMessage());
        }
        return null;
    }
    
    @Override
    public Award findByWinningBidId(int winningBidId) {
        String sql = "SELECT * FROM awards WHERE winning_bid_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, winningBidId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractAwardFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            logger.severe("Error finding award by winning bid ID: " + e.getMessage());
        }
        return null;
    }
    
    @Override
    public boolean updateAward(Award award) {
        String sql = "UPDATE awards SET awarded_value=?, justification=? WHERE award_id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setBigDecimal(1, award.getAwardedValue());
            pstmt.setString(2, award.getJustification());
            pstmt.setInt(3, award.getAwardId());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            logger.severe("Error updating award: " + e.getMessage());
            return false;
        }
    }
    
    private Award extractAwardFromResultSet(ResultSet rs) throws SQLException {
        Award award = new Award();
        award.setAwardId(rs.getInt("award_id"));
        award.setTenderId(rs.getInt("tender_id"));
        award.setWinningBidId(rs.getInt("winning_bid_id"));
        award.setAwardedValue(rs.getBigDecimal("awarded_value"));
        award.setJustification(rs.getString("justification"));
        award.setAwardDate(rs.getTimestamp("award_date"));
        award.setAwardedBy(rs.getInt("awarded_by"));
        return award;
    }
}

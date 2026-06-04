package com.procuregov.dao;

import com.procuregov.model.Evaluation;
import com.procuregov.db.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class EvaluationDAOImpl implements EvaluationDAO {
    
    private static final Logger logger = Logger.getLogger(EvaluationDAOImpl.class.getName());
    
    @Override
    public boolean saveEvaluation(Evaluation evaluation) {
        String sql = "INSERT INTO evaluations (bid_id, evaluator_id, price_score, technical_score, delivery_score, weighted_total) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, evaluation.getBidId());
            pstmt.setInt(2, evaluation.getEvaluatorId());
            pstmt.setBigDecimal(3, evaluation.getPriceScore());
            pstmt.setBigDecimal(4, evaluation.getTechnicalScore());
            pstmt.setBigDecimal(5, evaluation.getDeliveryScore());
            pstmt.setBigDecimal(6, evaluation.getWeightedTotal());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            logger.severe("Error saving evaluation: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean saveOrUpdate(Evaluation evaluation) {
        Evaluation existing = findByBidAndEvaluator(evaluation.getBidId(), evaluation.getEvaluatorId());
        if (existing != null) {
            String sql = "UPDATE evaluations SET price_score=?, technical_score=?, delivery_score=?, weighted_total=? " +
                         "WHERE bid_id=? AND evaluator_id=?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                
                pstmt.setBigDecimal(1, evaluation.getPriceScore());
                pstmt.setBigDecimal(2, evaluation.getTechnicalScore());
                pstmt.setBigDecimal(3, evaluation.getDeliveryScore());
                pstmt.setBigDecimal(4, evaluation.getWeightedTotal());
                pstmt.setInt(5, evaluation.getBidId());
                pstmt.setInt(6, evaluation.getEvaluatorId());
                
                return pstmt.executeUpdate() > 0;
                
            } catch (SQLException e) {
                logger.severe("Error updating evaluation: " + e.getMessage());
                return false;
            }
        } else {
            return saveEvaluation(evaluation);
        }
    }
    
    @Override
    public Evaluation findByBidAndEvaluator(int bidId, int evaluatorId) {
        String sql = "SELECT * FROM evaluations WHERE bid_id = ? AND evaluator_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, bidId);
            pstmt.setInt(2, evaluatorId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractEvaluationFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            logger.severe("Error finding evaluation: " + e.getMessage());
        }
        return null;
    }
    
    @Override
    public List<Evaluation> findByBid(int bidId) {
        List<Evaluation> evaluations = new ArrayList<>();
        String sql = "SELECT * FROM evaluations WHERE bid_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, bidId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                evaluations.add(extractEvaluationFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            logger.severe("Error finding evaluations by bid: " + e.getMessage());
        }
        return evaluations;
    }
    
    @Override
    public List<Evaluation> findByEvaluator(int evaluatorId) {
        List<Evaluation> evaluations = new ArrayList<>();
        String sql = "SELECT * FROM evaluations WHERE evaluator_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, evaluatorId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                evaluations.add(extractEvaluationFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            logger.severe("Error finding evaluations by evaluator: " + e.getMessage());
        }
        return evaluations;
    }
    
    @Override
    public List<Evaluation> findByTender(int tenderId) {
        List<Evaluation> evaluations = new ArrayList<>();
        String sql = "SELECT e.* FROM evaluations e " +
                     "JOIN bids b ON e.bid_id = b.bid_id " +
                     "WHERE b.tender_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, tenderId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                evaluations.add(extractEvaluationFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            logger.severe("Error finding evaluations by tender: " + e.getMessage());
        }
        return evaluations;
    }
    
    @Override
    public double getAverageWeightedScoreForBid(int bidId) {
        String sql = "SELECT AVG(weighted_total) as avg_score FROM evaluations WHERE bid_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, bidId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble("avg_score");
            }
            
        } catch (SQLException e) {
            logger.severe("Error calculating average score: " + e.getMessage());
        }
        return 0;
    }
    
    @Override
    public boolean allEvaluatorsHaveScored(int tenderId) {
        int totalEvaluators = getEvaluatorCount();
        int completedEvaluators = getCompletedEvaluatorCountForTender(tenderId);
        return completedEvaluators >= totalEvaluators && totalEvaluators > 0;
    }
    
    @Override
    public boolean hasEvaluatorScored(int tenderId, int evaluatorId) {
        String sql = "SELECT COUNT(*) FROM evaluations e " +
                     "JOIN bids b ON e.bid_id = b.bid_id " +
                     "WHERE b.tender_id = ? AND e.evaluator_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, tenderId);
            pstmt.setInt(2, evaluatorId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            logger.severe("Error checking evaluator scored: " + e.getMessage());
        }
        return false;
    }
    
    @Override
    public int getEvaluatorCount() {
        String sql = "SELECT COUNT(*) FROM users WHERE role = 'EVALUATION_COMMITTEE'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            logger.severe("Error getting evaluator count: " + e.getMessage());
        }
        return 0;
    }
    
    @Override
    public int getCompletedEvaluatorCountForTender(int tenderId) {
        String sql = "SELECT COUNT(DISTINCT e.evaluator_id) as completed_count " +
                     "FROM bids b " +
                     "LEFT JOIN evaluations e ON b.bid_id = e.bid_id " +
                     "WHERE b.tender_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, tenderId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("completed_count");
            }
            
        } catch (SQLException e) {
            logger.severe("Error getting completed evaluator count: " + e.getMessage());
        }
        return 0;
    }
    
    @Override
    public Map<Integer, Double> getAverageScoresPerBid(int tenderId) {
        Map<Integer, Double> averageScores = new HashMap<>();
        String sql = "SELECT b.bid_id, AVG(e.weighted_total) as avg_score " +
                     "FROM bids b " +
                     "LEFT JOIN evaluations e ON b.bid_id = e.bid_id " +
                     "WHERE b.tender_id = ? " +
                     "GROUP BY b.bid_id";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, tenderId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                averageScores.put(rs.getInt("bid_id"), rs.getDouble("avg_score"));
            }
            
        } catch (SQLException e) {
            logger.severe("Error getting average scores: " + e.getMessage());
        }
        return averageScores;
    }
    
    @Override
    public double getAverageTechnicalScoreForBid(int bidId) {
        String sql = "SELECT AVG(technical_score) as avg_technical FROM evaluations WHERE bid_id = ?";
    
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
            pstmt.setInt(1, bidId);
            ResultSet rs = pstmt.executeQuery();
        
            if (rs.next()) {
                return rs.getDouble("avg_technical");
             }
        
        } catch (SQLException e) {
            logger.severe("Error calculating average technical score: " + e.getMessage());
        }
        return 0;
    } 

    private Evaluation extractEvaluationFromResultSet(ResultSet rs) throws SQLException {
        Evaluation evaluation = new Evaluation();
        evaluation.setEvaluationId(rs.getInt("evaluation_id"));
        evaluation.setBidId(rs.getInt("bid_id"));
        evaluation.setEvaluatorId(rs.getInt("evaluator_id"));
        evaluation.setPriceScore(rs.getBigDecimal("price_score"));
        evaluation.setTechnicalScore(rs.getBigDecimal("technical_score"));
        evaluation.setDeliveryScore(rs.getBigDecimal("delivery_score"));
        evaluation.setWeightedTotal(rs.getBigDecimal("weighted_total"));
        evaluation.setSubmittedAt(rs.getTimestamp("submitted_at"));
        return evaluation;
    }
}

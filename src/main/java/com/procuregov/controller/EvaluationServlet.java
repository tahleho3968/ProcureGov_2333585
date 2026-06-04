package com.procuregov.controller;

import com.procuregov.dao.BidDAO;
import com.procuregov.dao.BidDAOImpl;
import com.procuregov.dao.EvaluationDAO;
import com.procuregov.dao.EvaluationDAOImpl;
import com.procuregov.dao.TenderDAO;
import com.procuregov.dao.TenderDAOImpl;
import com.procuregov.model.Bid;
import com.procuregov.model.Evaluation;
import com.procuregov.model.Tender;
import com.procuregov.model.User;
import com.procuregov.util.SessionValidator;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class EvaluationServlet extends HttpServlet {
    
    private BidDAO bidDAO;
    private TenderDAO tenderDAO;
    private EvaluationDAO evaluationDAO;
    
    @Override
    public void init() {
        bidDAO = new BidDAOImpl();
        tenderDAO = new TenderDAOImpl();
        evaluationDAO = new EvaluationDAOImpl();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        if (!SessionValidator.hasRole(request, response, "EVALUATION_COMMITTEE")) {
            return;
        }
        
        int tenderId = Integer.parseInt(request.getParameter("tenderId"));
        Tender tender = tenderDAO.findById(tenderId);
        User evaluator = SessionValidator.getCurrentUser(request);
        
        if (tender == null || !"UNDER_EVALUATION".equals(tender.getStatus())) {
            response.sendRedirect(request.getContextPath() + "/evaluator/dashboard?error=Tender not available for evaluation");
            return;
        }
        
        // Get all bids for this tender
        List<Bid> bids = bidDAO.findBidsByTenderWithSupplierDetails(tenderId);
        
        // Check which bids the evaluator has already scored
        for (Bid bid : bids) {
            Evaluation existingEval = evaluationDAO.findByBidAndEvaluator(bid.getBidId(), evaluator.getUserId());
            bid.setTechnicalScore(existingEval != null ? existingEval.getTechnicalScore().intValue() : -1);
        }
        
        // Get progress information
        int totalEvaluators = evaluationDAO.getEvaluatorCount();
        int completedEvaluators = evaluationDAO.getCompletedEvaluatorCountForTender(tenderId);
        boolean hasEvaluatorScored = evaluationDAO.hasEvaluatorScored(tenderId, evaluator.getUserId());
        
        request.setAttribute("tender", tender);
        request.setAttribute("bids", bids);
        request.setAttribute("totalEvaluators", totalEvaluators);
        request.setAttribute("completedEvaluators", completedEvaluators);
        request.setAttribute("hasEvaluatorScored", hasEvaluatorScored);
        request.getRequestDispatcher("/views/evaluator/evaluateBids.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        if (!SessionValidator.hasRole(request, response, "EVALUATION_COMMITTEE")) {
            return;
        }
        
        try {
            int tenderId = Integer.parseInt(request.getParameter("tenderId"));
            User evaluator = SessionValidator.getCurrentUser(request);
            
            // Get all bids for this tender
            List<Bid> bids = bidDAO.findBidsByTender(tenderId);
            
            // Calculate lowest bid amount and shortest timeline
            BigDecimal lowestBid = bids.stream()
                    .map(Bid::getBidAmount)
                    .min(BigDecimal::compareTo)
                    .orElse(BigDecimal.ZERO);
            
            int shortestTimeline = bids.stream()
                    .mapToInt(Bid::getDeliveryTimeline)
                    .min()
                    .orElse(1);
            
            for (Bid bid : bids) {
                String technicalScoreParam = request.getParameter("technical_score_" + bid.getBidId());
                if (technicalScoreParam != null && !technicalScoreParam.isEmpty()) {
                    int technicalScore = Integer.parseInt(technicalScoreParam);
                    
                    // Calculate price score
                    double priceScore = lowestBid.divide(bid.getBidAmount(), 4, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100)).doubleValue();
                    
                    // Calculate delivery score
                    double deliveryScore = ((double) shortestTimeline / bid.getDeliveryTimeline()) * 100;
                    
                    // Calculate weighted total
                    double weightedTotal = (priceScore * 0.40) + (technicalScore * 0.35) + (deliveryScore * 0.25);
                    
                    // Save evaluation
                    Evaluation evaluation = new Evaluation();
                    evaluation.setBidId(bid.getBidId());
                    evaluation.setEvaluatorId(evaluator.getUserId());
                    evaluation.setPriceScore(BigDecimal.valueOf(priceScore));
                    evaluation.setTechnicalScore(BigDecimal.valueOf(technicalScore));
                    evaluation.setDeliveryScore(BigDecimal.valueOf(deliveryScore));
                    evaluation.setWeightedTotal(BigDecimal.valueOf(weightedTotal));
                    
                    evaluationDAO.saveOrUpdate(evaluation);
                }
            }
            
            // Check if all evaluators have scored
            int totalEvaluators = evaluationDAO.getEvaluatorCount();
            int completedEvaluators = evaluationDAO.getCompletedEvaluatorCountForTender(tenderId);
            
            if (completedEvaluators >= totalEvaluators) {
                // All evaluators have scored - calculate final averages and update status
                Map<Integer, Double> averageScores = evaluationDAO.getAverageScoresPerBid(tenderId);
                
                // Store average scores somewhere or just update tender status
                tenderDAO.updateTenderStatus(tenderId, "EVALUATED");
                request.setAttribute("success", "All " + totalEvaluators + " evaluators have completed scoring! Tender has been moved to EVALUATED status.");
            } else {
                request.setAttribute("success", "Your scores have been saved! (" + completedEvaluators + "/" + totalEvaluators + " evaluators completed)");
            }
            
            response.sendRedirect(request.getContextPath() + "/evaluator/dashboard");
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/evaluator/dashboard?error=" + e.getMessage());
        }
    }
}

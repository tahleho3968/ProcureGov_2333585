package com.procuregov.controller;

import com.procuregov.dao.BidDAO;
import com.procuregov.dao.BidDAOImpl;
import com.procuregov.dao.EvaluationDAO;
import com.procuregov.dao.EvaluationDAOImpl;
import com.procuregov.dao.TenderDAO;
import com.procuregov.dao.TenderDAOImpl;
import com.procuregov.model.Bid;
import com.procuregov.model.Tender;
import com.procuregov.util.SessionValidator;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class ViewResultsServlet extends HttpServlet {
    
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
        
        // Check if user is logged in
        if (!SessionValidator.isAuthenticated(request, response)) {
            return;
        }
        
        int tenderId = Integer.parseInt(request.getParameter("tenderId"));
        Tender tender = tenderDAO.findById(tenderId);
        
        if (tender == null) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }
        
        // Get all bids for this tender with supplier details
        List<Bid> bids = bidDAO.findBidsByTenderWithSupplierDetails(tenderId);
        
        // Calculate final scores for each bid (average of all evaluators)
        List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
        
        // Calculate global lowest bid and shortest timeline for reference
        BigDecimal lowestBid = bids.stream()
                .map(Bid::getBidAmount)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
        
        int shortestTimeline = bids.stream()
                .mapToInt(Bid::getDeliveryTimeline)
                .min()
                .orElse(1);
        
        for (Bid bid : bids) {
            Map<String, Object> result = new HashMap<String, Object>();
            result.put("supplierName", bid.getSupplierName());
            result.put("registrationNumber", bid.getRegistrationNumber());
            result.put("bidAmount", bid.getBidAmount());
            result.put("technicalCompliance", bid.getTechnicalCompliance());
            result.put("deliveryTimeline", bid.getDeliveryTimeline());
            
            // Calculate automatic scores
            double priceScore = lowestBid.divide(bid.getBidAmount(), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100)).doubleValue();
            double deliveryScore = ((double) shortestTimeline / bid.getDeliveryTimeline()) * 100;
            
            result.put("priceScore", priceScore);
            result.put("deliveryScore", deliveryScore);
            
            // Get average technical score from evaluators
            double avgTechnicalScore = evaluationDAO.getAverageTechnicalScoreForBid(bid.getBidId());
            result.put("technicalScore", avgTechnicalScore);
            
            // Calculate final weighted total
            double finalScore = (priceScore * 0.40) + (avgTechnicalScore * 0.35) + (deliveryScore * 0.25);
            result.put("finalScore", finalScore);
            
            results.add(result);
        }
        
        // Sort results by final score descending
        results.sort((a, b) -> Double.compare(
            (Double) b.get("finalScore"), 
            (Double) a.get("finalScore")
        ));
        
        // Determine winner
        if (!results.isEmpty()) {
            request.setAttribute("winner", results.get(0));
        }
        
        request.setAttribute("tender", tender);
        request.setAttribute("results", results);
        request.setAttribute("totalEvaluators", evaluationDAO.getEvaluatorCount());
        
        request.getRequestDispatcher("/views/evaluator/viewResults.jsp").forward(request, response);
    }
}

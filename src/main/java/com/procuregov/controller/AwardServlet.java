package com.procuregov.controller;

import com.procuregov.dao.BidDAO;
import com.procuregov.dao.BidDAOImpl;
import com.procuregov.dao.EvaluationDAO;
import com.procuregov.dao.EvaluationDAOImpl;
import com.procuregov.dao.TenderDAO;
import com.procuregov.dao.TenderDAOImpl;
import com.procuregov.dao.UserDAO;
import com.procuregov.dao.UserDAOImpl;
import com.procuregov.model.Bid;
import com.procuregov.model.Tender;
import com.procuregov.model.User;
import com.procuregov.service.EmailService;
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

public class AwardServlet extends HttpServlet {
    
    private BidDAO bidDAO;
    private TenderDAO tenderDAO;
    private UserDAO userDAO;
    private EvaluationDAO evaluationDAO;
    
    @Override
    public void init() {
        bidDAO = new BidDAOImpl();
        tenderDAO = new TenderDAOImpl();
        userDAO = new UserDAOImpl();
        evaluationDAO = new EvaluationDAOImpl();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        if (!SessionValidator.hasRole(request, response, "PROCUREMENT_OFFICER")) {
            return;
        }
        
        int tenderId = Integer.parseInt(request.getParameter("tenderId"));
        Tender tender = tenderDAO.findById(tenderId);
        
        if (tender == null || !"EVALUATED".equals(tender.getStatus())) {
            response.sendRedirect(request.getContextPath() + "/officer/tender?error=Tender not ready for award");
            return;
        }
        
        // Get all bids with their average scores
        List<Bid> bids = bidDAO.findBidsByTenderWithSupplierDetails(tenderId);
        
        // Calculate final scores for ranking
        List<Map<String, Object>> rankedBids = new ArrayList<>();
        
        // Calculate global lowest bid and shortest timeline
        BigDecimal lowestBid = bids.stream()
                .map(Bid::getBidAmount)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
        
        int shortestTimeline = bids.stream()
                .mapToInt(Bid::getDeliveryTimeline)
                .min()
                .orElse(1);
        
        for (Bid bid : bids) {
            Map<String, Object> rankedBid = new HashMap<>();
            rankedBid.put("bidId", bid.getBidId());
            rankedBid.put("supplierName", bid.getSupplierName());
            rankedBid.put("supplierId", bid.getSupplierId());
            rankedBid.put("bidAmount", bid.getBidAmount());
            rankedBid.put("deliveryTimeline", bid.getDeliveryTimeline());
            
            // Calculate scores
            double priceScore = lowestBid.divide(bid.getBidAmount(), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100)).doubleValue();
            double deliveryScore = ((double) shortestTimeline / bid.getDeliveryTimeline()) * 100;
            double avgTechnicalScore = evaluationDAO.getAverageTechnicalScoreForBid(bid.getBidId());
            double finalScore = (priceScore * 0.40) + (avgTechnicalScore * 0.35) + (deliveryScore * 0.25);
            
            rankedBid.put("finalScore", finalScore);
            rankedBids.add(rankedBid);
        }
        
        // Sort by final score descending
        rankedBids.sort((a, b) -> Double.compare((Double) b.get("finalScore"), (Double) a.get("finalScore")));
        
        request.setAttribute("tender", tender);
        request.setAttribute("rankedBids", rankedBids);
        request.getRequestDispatcher("/views/officer/awardContract.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        if (!SessionValidator.hasRole(request, response, "PROCUREMENT_OFFICER")) {
            return;
        }
        
        try {
            int tenderId = Integer.parseInt(request.getParameter("tenderId"));
            int winningBidId = Integer.parseInt(request.getParameter("winningBidId"));
            BigDecimal awardedValue = new BigDecimal(request.getParameter("awardedValue"));
            String justification = request.getParameter("justification");
            User officer = SessionValidator.getCurrentUser(request);
            
            Tender tender = tenderDAO.findById(tenderId);
            Bid winningBid = bidDAO.findById(winningBidId);
            User winningSupplier = userDAO.findById(winningBid.getSupplierId());
            
            // Update winning bid flag
            winningBid.setWinningBid(true);
            bidDAO.updateBid(winningBid);
            
            // Update tender status to AWARDED
            tenderDAO.updateTenderStatus(tenderId, "AWARDED");
            
            // Get all bids for this tender to send notifications
            List<Bid> allBids = bidDAO.findBidsByTender(tenderId);
            for (Bid bid : allBids) {
                User supplier = userDAO.findById(bid.getSupplierId());
                bid.setSupplierName(supplier.getFullName());
            }
            
            // Send email notifications
            EmailService.sendAwardNotification(tender, winningBid, winningSupplier, allBids, justification);
            
            request.getSession().setAttribute("success", "Contract awarded successfully! Email notifications have been sent to all suppliers.");
            response.sendRedirect(request.getContextPath() + "/officer/tender");
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/officer/tender?error=" + e.getMessage());
        }
    }
}

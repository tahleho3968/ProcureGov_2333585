package com.procuregov.controller;

import com.procuregov.dao.BidDAO;
import com.procuregov.dao.BidDAOImpl;
import com.procuregov.dao.TenderDAO;
import com.procuregov.dao.TenderDAOImpl;
import com.procuregov.model.Bid;
import com.procuregov.model.Tender;
import com.procuregov.model.User;
import com.procuregov.util.SessionValidator;
import com.procuregov.service.TenderStatusService;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import javax.servlet.annotation.MultipartConfig;

@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2,
    maxFileSize = 1024 * 1024 * 10,
    maxRequestSize = 1024 * 1024 * 20
)
public class BidSubmissionServlet extends HttpServlet {
    
    private BidDAO bidDAO;
    private TenderDAO tenderDAO;
    private TenderStatusService statusService;
    private static final String UPLOAD_DIR = "uploads/bids";
    
    @Override
    public void init() {
        bidDAO = new BidDAOImpl();
        tenderDAO = new TenderDAOImpl();
        statusService = new TenderStatusService();
        
        String uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIR;
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        if (!SessionValidator.hasRole(request, response, "SUPPLIER")) {
            return;
        }
        
        String action = request.getParameter("action");
        int tenderId = Integer.parseInt(request.getParameter("tenderId"));
        
        // Check and close expired tender first
        statusService.checkAndCloseTender(tenderId);
        
        Tender tender = tenderDAO.findById(tenderId);
        
        if (tender == null) {
            response.sendRedirect(request.getContextPath() + "/supplier/dashboard");
            return;
        }
        
        // Check if tender is still OPEN after auto-close check
        if (!"OPEN".equals(tender.getStatus())) {
            response.sendRedirect(request.getContextPath() + "/supplier/dashboard?error=Tender is no longer open");
            return;
        }
        
        request.setAttribute("tender", tender);
        request.getRequestDispatcher("/views/supplier/submitBid.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        if (!SessionValidator.hasRole(request, response, "SUPPLIER")) {
            return;
        }
        
        try {
            int tenderId = Integer.parseInt(request.getParameter("tenderId"));
            
            // Check and close expired tender first
            statusService.checkAndCloseTender(tenderId);
            
            Tender tender = tenderDAO.findById(tenderId);
            
            // Check if tender is still OPEN after auto-close check
            if (tender == null || !"OPEN".equals(tender.getStatus())) {
                request.setAttribute("error", "This tender is no longer open for bidding");
                request.getRequestDispatcher("/views/supplier/dashboard.jsp").forward(request, response);
                return;
            }
            
            // Check closing date/time
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime closingDate = tender.getClosingDatetime().toLocalDateTime();
            
            if (now.isAfter(closingDate)) {
                request.setAttribute("error", "Bidding deadline has passed");
                request.getRequestDispatcher("/views/supplier/dashboard.jsp").forward(request, response);
                return;
            }
            
            User user = SessionValidator.getCurrentUser(request);
            
            // Check if supplier already bid on this tender
            if (bidDAO.hasSupplierBidOnTender(tenderId, user.getUserId())) {
                request.setAttribute("error", "You have already submitted a bid for this tender");
                request.getRequestDispatcher("/views/supplier/dashboard.jsp").forward(request, response);
                return;
            }
            
            // Get form parameters
            BigDecimal bidAmount = new BigDecimal(request.getParameter("bidAmount"));
            String technicalCompliance = request.getParameter("technicalCompliance");
            int deliveryTimeline = Integer.parseInt(request.getParameter("deliveryTimeline"));
            
            // File upload
            Part filePart = request.getPart("supportingDoc");
            String fileName = extractFileName(filePart);
            
            // Save file
            String uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIR;
            String uniqueFileName = "bid_" + System.currentTimeMillis() + "_" + fileName;
            String filePath = uploadPath + File.separator + uniqueFileName;
            filePart.write(filePath);
            
            // Create bid object
            Bid bid = new Bid();
            bid.setTenderId(tenderId);
            bid.setSupplierId(user.getUserId());
            bid.setBidAmount(bidAmount);
            bid.setTechnicalCompliance(technicalCompliance);
            bid.setDeliveryTimeline(deliveryTimeline);
            bid.setSupportingDocPath(UPLOAD_DIR + "/" + uniqueFileName);
            
            // Save to database
            boolean saved = bidDAO.saveBid(bid);
            
            if (saved) {
                request.setAttribute("success", "Bid submitted successfully!");
            } else {
                request.setAttribute("error", "Failed to submit bid");
            }
            
            response.sendRedirect(request.getContextPath() + "/supplier/dashboard");
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error submitting bid: " + e.getMessage());
            request.getRequestDispatcher("/views/supplier/dashboard.jsp").forward(request, response);
        }
    }
    
    private String extractFileName(Part part) {
        String contentDisposition = part.getHeader("content-disposition");
        for (String token : contentDisposition.split(";")) {
            if (token.trim().startsWith("filename")) {
                return token.substring(token.indexOf("=") + 2, token.length() - 1);
            }
        }
        return "unknown.pdf";
    }
}

package com.procuregov.controller;

import com.procuregov.dao.AwardDAO;
import com.procuregov.dao.AwardDAOImpl;
import com.procuregov.dao.TenderDAO;
import com.procuregov.dao.TenderDAOImpl;
import com.procuregov.dao.BidDAO;
import com.procuregov.dao.BidDAOImpl;
import com.procuregov.dao.UserDAO;
import com.procuregov.dao.UserDAOImpl;
import com.procuregov.model.Award;
import com.procuregov.model.Tender;
import com.procuregov.model.Bid;
import com.procuregov.model.User;
import com.procuregov.util.SessionValidator;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class AwardNoticeServlet extends HttpServlet {
    
    private AwardDAO awardDAO;
    private TenderDAO tenderDAO;
    private BidDAO bidDAO;
    private UserDAO userDAO;
    
    @Override
    public void init() {
        awardDAO = new AwardDAOImpl();
        tenderDAO = new TenderDAOImpl();
        bidDAO = new BidDAOImpl();
        userDAO = new UserDAOImpl();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        if (!SessionValidator.isAuthenticated(request, response)) {
            return;
        }
        
        String tenderIdParam = request.getParameter("tenderId");
        
        // Check if tenderId is null or empty
        if (tenderIdParam == null || tenderIdParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/supplier/dashboard?error=Invalid tender ID");
            return;
        }
        
        int tenderId;
        try {
            tenderId = Integer.parseInt(tenderIdParam);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/supplier/dashboard?error=Invalid tender ID format");
            return;
        }
        
        Tender tender = tenderDAO.findById(tenderId);
        
        if (tender == null) {
            response.sendRedirect(request.getContextPath() + "/supplier/dashboard?error=Tender not found");
            return;
        }
        
        // Get award information
        Award award = awardDAO.findByTenderId(tenderId);
        
        if (award == null) {
            response.sendRedirect(request.getContextPath() + "/supplier/dashboard?error=Award not found for this tender");
            return;
        }
        
        // Get winning bid and supplier
        Bid winningBid = bidDAO.findById(award.getWinningBidId());
        User winningSupplier = userDAO.findById(winningBid.getSupplierId());
        
        // Get current user to check if they are the winner
        User currentUser = SessionValidator.getCurrentUser(request);
        boolean isWinner = (currentUser.getUserId() == winningBid.getSupplierId());
        
        request.setAttribute("tender", tender);
        request.setAttribute("award", award);
        request.setAttribute("winningBid", winningBid);
        request.setAttribute("winningSupplier", winningSupplier);
        request.setAttribute("isWinner", isWinner);
        
        request.getRequestDispatcher("/views/supplier/awardNotice.jsp").forward(request, response);
    }
}

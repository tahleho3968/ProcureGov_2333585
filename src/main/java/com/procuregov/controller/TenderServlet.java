package com.procuregov.controller;

import com.procuregov.dao.TenderDAO;
import com.procuregov.dao.TenderDAOImpl;
import com.procuregov.model.Tender;
import com.procuregov.model.User;
import com.procuregov.service.ReferenceGenerator;
import com.procuregov.util.SessionValidator;

import java.io.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import javax.servlet.annotation.MultipartConfig;

@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2,
    maxFileSize = 1024 * 1024 * 5,
    maxRequestSize = 1024 * 1024 * 10
)
public class TenderServlet extends HttpServlet {
    
    private TenderDAO tenderDAO;
    private static final String UPLOAD_DIR = "uploads/tenders";
    
    @Override
    public void init() {
        tenderDAO = new TenderDAOImpl();
        
        String uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIR;
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        if (!SessionValidator.hasRole(request, response, "PROCUREMENT_OFFICER")) {
            return;
        }
        
        String action = request.getParameter("action");
        
        if (action == null) {
            action = "list";
        }
        
        switch (action) {
            case "create":
                showCreateForm(request, response);
                break;
            case "edit":
                showEditForm(request, response);
                break;
            case "delete":
                deleteTender(request, response);
                break;
            case "changeStatus":
                changeStatus(request, response);
                break;
            case "view":
                viewTender(request, response);
                break;
            default:
                listTenders(request, response);
                break;
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        if (!SessionValidator.hasRole(request, response, "PROCUREMENT_OFFICER")) {
            return;
        }
        
        String action = request.getParameter("action");
        
        if ("create".equals(action)) {
            createTender(request, response);
        } else if ("edit".equals(action)) {
            updateTender(request, response);
        } else if ("award".equals(action)) {
            awardTender(request, response);
        } else {
            listTenders(request, response);
        }
    }
    
    private void showCreateForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.getRequestDispatcher("/views/officer/createTender.jsp").forward(request, response);
    }
    
    private void createTender(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            String title = request.getParameter("title");
            String category = request.getParameter("category");
            String description = request.getParameter("description");
            String estimatedValueStr = request.getParameter("estimatedValue");
            String closingDateStr = request.getParameter("closingDatetime");
            
            Part filePart = request.getPart("noticeFile");
            String fileName = extractFileName(filePart);
            
            if (title == null || title.trim().isEmpty()) {
                request.setAttribute("error", "Title is required");
                showCreateForm(request, response);
                return;
            }
            
            if (closingDateStr == null || closingDateStr.trim().isEmpty()) {
                request.setAttribute("error", "Closing date and time is required");
                showCreateForm(request, response);
                return;
            }
            
            BigDecimal estimatedValue;
            try {
                estimatedValue = new BigDecimal(estimatedValueStr);
            } catch (NumberFormatException e) {
                request.setAttribute("error", "Invalid estimated value");
                showCreateForm(request, response);
                return;
            }
            
            LocalDateTime closingDateTime;
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
                closingDateTime = LocalDateTime.parse(closingDateStr, formatter);
            } catch (Exception e) {
                request.setAttribute("error", "Invalid date format. Please use the date picker to select a valid date and time.");
                showCreateForm(request, response);
                return;
            }
            
            LocalDateTime now = LocalDateTime.now();
            if (closingDateTime.isBefore(now)) {
                request.setAttribute("error", "Closing date and time cannot be in the past! Please select a future date/time.");
                showCreateForm(request, response);
                return;
            }
            
            Timestamp closingTimestamp = Timestamp.valueOf(closingDateTime);
            
            String uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIR;
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            
            User user = SessionValidator.getCurrentUser(request);
            
            boolean saved = false;
            String referenceNumber = null;
            int maxRetries = 5;
            
            for (int retry = 0; retry < maxRetries; retry++) {
                referenceNumber = ReferenceGenerator.generateReferenceNumber();
                
                String uniqueFileName = referenceNumber + "_" + fileName;
                String filePath = uploadPath + File.separator + uniqueFileName;
                filePart.write(filePath);
                
                Tender tender = new Tender();
                tender.setReferenceNumber(referenceNumber);
                tender.setTitle(title);
                tender.setCategory(category);
                tender.setDescription(description);
                tender.setEstimatedValue(estimatedValue);
                tender.setClosingDatetime(closingTimestamp);
                tender.setNoticePdfPath(UPLOAD_DIR + "/" + uniqueFileName);
                tender.setCreatedBy(user.getUserId());
                tender.setStatus("DRAFT");
                
                saved = tenderDAO.saveTender(tender);
                
                if (saved) {
                    break;
                }
            }
            
            if (saved) {
                request.setAttribute("success", "Tender created successfully! Reference: " + referenceNumber);
                listTenders(request, response);
            } else {
                request.setAttribute("error", "Failed to create tender. Please try again.");
                showCreateForm(request, response);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error creating tender: " + e.getMessage());
            showCreateForm(request, response);
        }
    }
    
    private void showEditForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        int tenderId = Integer.parseInt(request.getParameter("id"));
        Tender tender = tenderDAO.findById(tenderId);
        
        if (tender == null || !"DRAFT".equals(tender.getStatus())) {
            request.setAttribute("error", "Tender cannot be edited (only DRAFT status)");
            listTenders(request, response);
            return;
        }
        
        request.setAttribute("tender", tender);
        request.getRequestDispatcher("/views/officer/editTender.jsp").forward(request, response);
    }
    
    private void updateTender(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            int tenderId = Integer.parseInt(request.getParameter("tenderId"));
            Tender existingTender = tenderDAO.findById(tenderId);
            
            if (existingTender == null || !"DRAFT".equals(existingTender.getStatus())) {
                request.setAttribute("error", "Tender cannot be edited");
                listTenders(request, response);
                return;
            }
            
            existingTender.setTitle(request.getParameter("title"));
            existingTender.setCategory(request.getParameter("category"));
            existingTender.setDescription(request.getParameter("description"));
            existingTender.setEstimatedValue(new BigDecimal(request.getParameter("estimatedValue")));
            
            String closingDateStr = request.getParameter("closingDatetime");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
            LocalDateTime closingDateTime = LocalDateTime.parse(closingDateStr, formatter);
            
            LocalDateTime now = LocalDateTime.now();
            if (closingDateTime.isBefore(now)) {
                request.setAttribute("error", "Closing date and time cannot be in the past!");
                listTenders(request, response);
                return;
            }
            
            existingTender.setClosingDatetime(Timestamp.valueOf(closingDateTime));
            
            Part filePart = request.getPart("noticeFile");
            if (filePart != null && filePart.getSize() > 0) {
                String fileName = extractFileName(filePart);
                String uniqueFileName = existingTender.getReferenceNumber() + "_" + fileName;
                String uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIR;
                String filePath = uploadPath + File.separator + uniqueFileName;
                filePart.write(filePath);
                existingTender.setNoticePdfPath(UPLOAD_DIR + "/" + uniqueFileName);
            }
            
            boolean updated = tenderDAO.updateTender(existingTender);
            
            if (updated) {
                request.setAttribute("success", "Tender updated successfully");
            } else {
                request.setAttribute("error", "Failed to update tender");
            }
            
            listTenders(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error updating tender: " + e.getMessage());
            listTenders(request, response);
        }
    }
    
    private void deleteTender(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        int tenderId = Integer.parseInt(request.getParameter("id"));
        Tender tender = tenderDAO.findById(tenderId);
        
        if (tender != null && "DRAFT".equals(tender.getStatus())) {
            boolean deleted = tenderDAO.deleteTender(tenderId);
            if (deleted) {
                request.setAttribute("success", "Tender deleted successfully");
            } else {
                request.setAttribute("error", "Failed to delete tender");
            }
        } else {
            request.setAttribute("error", "Only DRAFT tenders can be deleted");
        }
        
        listTenders(request, response);
    }
    
    private void changeStatus(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        int tenderId = Integer.parseInt(request.getParameter("id"));
        String newStatus = request.getParameter("status");
        Tender tender = tenderDAO.findById(tenderId);
        
        if (tender != null) {
            if ("DRAFT".equals(tender.getStatus()) && "OPEN".equals(newStatus)) {
                tenderDAO.updateTenderStatus(tenderId, newStatus);
                request.setAttribute("success", "Tender published successfully");
            } else if ("OPEN".equals(tender.getStatus()) && "CLOSED".equals(newStatus)) {
                tenderDAO.updateTenderStatus(tenderId, newStatus);
                request.setAttribute("success", "Tender closed successfully");
            } else if ("CLOSED".equals(tender.getStatus()) && "UNDER_EVALUATION".equals(newStatus)) {
                tenderDAO.updateTenderStatus(tenderId, newStatus);
                request.setAttribute("success", "Tender moved to evaluation");
            } else if ("EVALUATED".equals(tender.getStatus()) && "AWARDED".equals(newStatus)) {
                // Redirect to AwardServlet instead of using old JSP
                response.sendRedirect(request.getContextPath() + "/officer/award?tenderId=" + tenderId);
                return;
            } else {
                request.setAttribute("error", "Invalid status transition");
            }
        }
        
        listTenders(request, response);
    }
    
    private void awardTender(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        int tenderId = Integer.parseInt(request.getParameter("tenderId"));
        String justification = request.getParameter("justification");
        
        request.setAttribute("success", "Award processing - will be completed with evaluation module");
        listTenders(request, response);
    }
    
    private void viewTender(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        int tenderId = Integer.parseInt(request.getParameter("id"));
        Tender tender = tenderDAO.findById(tenderId);
        request.setAttribute("tender", tender);
        request.getRequestDispatcher("/views/officer/viewTender.jsp").forward(request, response);
    }
    
    private void listTenders(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String statusFilter = request.getParameter("status");
        String categoryFilter = request.getParameter("category");
        
        User user = SessionValidator.getCurrentUser(request);
        java.util.List<Tender> tenders;
        
        if (statusFilter != null && !statusFilter.isEmpty()) {
            tenders = tenderDAO.findTendersByStatus(statusFilter);
        } else if (categoryFilter != null && !categoryFilter.isEmpty()) {
            tenders = tenderDAO.findTendersByCategory(categoryFilter);
        } else {
            tenders = tenderDAO.findTendersByOfficer(user.getUserId());
        }
        
        request.setAttribute("tenders", tenders);
        request.setAttribute("statusFilter", statusFilter);
        request.setAttribute("categoryFilter", categoryFilter);
        request.getRequestDispatcher("/views/officer/tenderList.jsp").forward(request, response);
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

package com.procuregov.controller;

import com.procuregov.dao.TenderDAO;
import com.procuregov.dao.TenderDAOImpl;
import com.procuregov.model.Tender;
import com.procuregov.util.SessionValidator;

import java.io.*;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;

public class DownloadServlet extends HttpServlet {
    
    private TenderDAO tenderDAO;
    
    @Override
    public void init() {
        tenderDAO = new TenderDAOImpl();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Check if user is logged in
        if (!SessionValidator.isAuthenticated(request, response)) {
            return;
        }
        
        String tenderIdParam = request.getParameter("tenderId");
        
        if (tenderIdParam == null || tenderIdParam.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        
        int tenderId = Integer.parseInt(tenderIdParam);
        Tender tender = tenderDAO.findById(tenderId);
        
        if (tender == null || tender.getNoticePdfPath() == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        
        String filePath = getServletContext().getRealPath("") + File.separator + tender.getNoticePdfPath();
        File file = new File(filePath);
        
        if (!file.exists()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        
        // Set response headers
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + tender.getReferenceNumber() + "_notice.pdf\"");
        response.setContentLength((int) file.length());
        
        // Stream the file
        try (FileInputStream in = new FileInputStream(file);
             OutputStream out = response.getOutputStream()) {
            
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
    }
}

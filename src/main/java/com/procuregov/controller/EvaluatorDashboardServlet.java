package com.procuregov.controller;

import com.procuregov.util.SessionValidator;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class EvaluatorDashboardServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        if (!SessionValidator.hasRole(request, response, "EVALUATION_COMMITTEE")) {
            return;
        }
        
        request.getRequestDispatcher("/views/evaluator/dashboard.jsp").forward(request, response);
    }
}

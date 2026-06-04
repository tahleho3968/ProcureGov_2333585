package com.procuregov.controller;

import com.procuregov.dao.UserDAO;
import com.procuregov.dao.UserDAOImpl;
import com.procuregov.model.User;
import com.procuregov.service.PasswordHasher;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;

public class LoginServlet extends HttpServlet {
    
    private UserDAO userDAO;
    
    @Override
    public void init() {
        userDAO = new UserDAOImpl();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Check if user is already logged in
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            // Redirect to appropriate dashboard
            User user = (User) session.getAttribute("user");
            redirectToDashboard(request, response, user);
            return;
        }
        
        // Forward to login page
        request.getRequestDispatcher("/views/auth/login.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        
        // Validation
        if (email == null || password == null || email.trim().isEmpty() || password.trim().isEmpty()) {
            request.setAttribute("error", "Email and password are required");
            request.getRequestDispatcher("/views/auth/login.jsp").forward(request, response);
            return;
        }
        
        // Find user by email
        User user = userDAO.findByEmail(email);
        
        if (user == null) {
            request.setAttribute("error", "Invalid email or password");
            request.getRequestDispatcher("/views/auth/login.jsp").forward(request, response);
            return;
        }
        
        // Check if account is locked
        if (user.isAccountLocked()) {
            request.setAttribute("error", "Account is locked. Please contact administrator.");
            request.getRequestDispatcher("/views/auth/login.jsp").forward(request, response);
            return;
        }
        
        // Verify password
        if (PasswordHasher.verifyPassword(password, user.getPasswordHash())) {
            // Successful login - reset failed attempts
            userDAO.updateFailedAttempts(email, 0);
            
            // Create session
            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            session.setAttribute("userId", user.getUserId());
            session.setAttribute("role", user.getRole());
            session.setAttribute("username", user.getUsername());
            
            // Set session timeout to 30 minutes
            session.setMaxInactiveInterval(30 * 60);
            
            // Redirect based on role
            redirectToDashboard(request, response, user);
            
        } else {
            // Failed login - increment attempts
            int failedAttempts = user.getFailedAttempts() + 1;
            userDAO.updateFailedAttempts(email, failedAttempts);
            
            if (failedAttempts >= 3) {
                // Lock account after 3 failed attempts
                userDAO.lockAccount(email);
                request.setAttribute("error", "Account locked due to 3 failed login attempts.");
            } else {
                request.setAttribute("error", "Invalid email or password. Attempts: " + failedAttempts + "/3");
            }
            
            request.getRequestDispatcher("/views/auth/login.jsp").forward(request, response);
        }
    }
    
    private void redirectToDashboard(HttpServletRequest request, HttpServletResponse response, User user) throws IOException {
        String role = user.getRole();
        String contextPath = request.getContextPath();
        
        switch (role) {
            case "PROCUREMENT_OFFICER":
                response.sendRedirect(contextPath + "/views/officer/dashboard.jsp");
                break;
            case "EVALUATION_COMMITTEE":
                response.sendRedirect(contextPath + "/views/evaluator/dashboard.jsp");
                break;
            case "SUPPLIER":
                response.sendRedirect(contextPath + "/views/supplier/dashboard.jsp");
                break;
            default:
                response.sendRedirect(contextPath + "/index.jsp");
        }
    }
}

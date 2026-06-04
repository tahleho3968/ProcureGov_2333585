package com.procuregov.util;

import com.procuregov.model.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class SessionValidator {
    
    public static boolean isAuthenticated(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        HttpSession session = request.getSession(false);
        
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login?error=session_expired");
            return false;
        }
        
        return true;
    }
    
    public static boolean hasRole(HttpServletRequest request, HttpServletResponse response, String role) 
            throws IOException {
        
        if (!isAuthenticated(request, response)) {
            return false;
        }
        
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("user");
        
        if (!user.getRole().equals(role)) {
            response.sendRedirect(request.getContextPath() + "/login?error=access_denied");
            return false;
        }
        
        return true;
    }
    
    public static User getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return (User) session.getAttribute("user");
        }
        return null;
    }
}

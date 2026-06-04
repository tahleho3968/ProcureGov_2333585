package com.procuregov.controller;

import com.procuregov.dao.UserDAO;
import com.procuregov.dao.UserDAOImpl;
import com.procuregov.model.User;
import com.procuregov.service.PasswordHasher;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;

public class TestPasswordServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        UserDAO userDAO = new UserDAOImpl();
        User user = userDAO.findByEmail("thabo.molapo@publicworks.gov.ls");
        
        out.println("<html><body>");
        out.println("<h1>Password Debug</h1>");
        
        if (user != null) {
            out.println("<p>User found: " + user.getEmail() + "</p>");
            out.println("<p>Stored hash: " + user.getPasswordHash() + "</p>");
            out.println("<p>Hash length: " + user.getPasswordHash().length() + "</p>");
            
            String testPassword = "Password123";
            String computedHash = PasswordHasher.hashPassword(testPassword);
            out.println("<p>Computed hash for 'Password123': " + computedHash + "</p>");
            out.println("<p>Computed hash length: " + computedHash.length() + "</p>");
            
            if (user.getPasswordHash().equals(computedHash)) {
                out.println("<p style='color:green'>✓ Passwords match!</p>");
                out.println("<p>Login should work with: " + user.getEmail() + " / Password123</p>");
            } else {
                out.println("<p style='color:red'>✗ Passwords do NOT match!</p>");
            }
        } else {
            out.println("<p>User not found!</p>");
        }
        
        out.println("</body></html>");
    }
}

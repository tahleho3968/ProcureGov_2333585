package com.procuregov.controller;

import com.procuregov.service.PasswordHasher;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;

public class FindPasswordServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        String storedHash = "5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5";
        
        out.println("<html><body>");
        out.println("<h1>Password Finder</h1>");
        
        // Test common passwords
        String[] testPasswords = {"password", "admin", "123456", "Password123", "pass123", "admin123", "welcome", "letmein"};
        
        for (String testPwd : testPasswords) {
            String hash = PasswordHasher.hashPassword(testPwd);
            if (hash.equals(storedHash)) {
                out.println("<p style='color:green'>✓ Found! Password is: <strong>" + testPwd + "</strong></p>");
                out.println("<p>Hash: " + hash + "</p>");
                break;
            }
        }
        
        out.println("</body></html>");
    }
}

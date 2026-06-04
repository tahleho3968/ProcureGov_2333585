package com.procuregov.controller;

import com.procuregov.service.EmailService;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class TestEmailServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        out.println("<html><body>");
        out.println("<h1>Email Configuration Test</h1>");
        out.println("<pre>");
        
        // Capture console output
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        java.io.PrintStream ps = new java.io.PrintStream(baos);
        java.io.PrintStream old = System.out;
        System.setOut(ps);
        
        EmailService.testEmailConfiguration();
        
        System.out.flush();
        System.setOut(old);
        
        out.println(baos.toString());
        out.println("</pre>");
        out.println("<a href='/' class='btn'>Back to Home</a>");
        out.println("</body></html>");
    }
}

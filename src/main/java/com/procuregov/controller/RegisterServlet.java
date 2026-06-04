package com.procuregov.controller;

import com.procuregov.dao.UserDAO;
import com.procuregov.dao.UserDAOImpl;
import com.procuregov.model.User;
import com.procuregov.service.PasswordHasher;

import java.io.*;
import java.time.LocalDateTime;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;

public class RegisterServlet extends HttpServlet {
    
    private UserDAO userDAO;
    
    @Override
    public void init() {
        userDAO = new UserDAOImpl();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Forward to registration page
        request.getRequestDispatcher("/views/auth/register.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Get form parameters
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String fullName = request.getParameter("fullName");
        String contactNumber = request.getParameter("contactNumber");
        String physicalAddress = request.getParameter("physicalAddress");
        
        // Validation
        if (username == null || email == null || password == null || fullName == null) {
            request.setAttribute("error", "All fields are required");
            request.getRequestDispatcher("/views/auth/register.jsp").forward(request, response);
            return;
        }
        
        // Check if passwords match
        if (!password.equals(confirmPassword)) {
            request.setAttribute("error", "Passwords do not match");
            request.getRequestDispatcher("/views/auth/register.jsp").forward(request, response);
            return;
        }
        
        // Check if email already exists
        User existingUser = userDAO.findByEmail(email);
        if (existingUser != null) {
            request.setAttribute("error", "Email already registered");
            request.getRequestDispatcher("/views/auth/register.jsp").forward(request, response);
            return;
        }
        
        // Check if username already exists
        existingUser = userDAO.findByUsername(username);
        if (existingUser != null) {
            request.setAttribute("error", "Username already taken");
            request.getRequestDispatcher("/views/auth/register.jsp").forward(request, response);
            return;
        }
        
        // Generate registration number
        String registrationNumber = generateRegistrationNumber();
        
        // Hash password using SHA-256
        String hashedPassword = PasswordHasher.hashPassword(password);
        
        // Create user object
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(hashedPassword);
        user.setFullName(fullName);
        user.setRegistrationNumber(registrationNumber);
        user.setContactNumber(contactNumber);
        user.setPhysicalAddress(physicalAddress);
        user.setRole("SUPPLIER"); // Only suppliers can register
        user.setAccountLocked(false);
        user.setFailedAttempts(0);
        
        // Save to database
        boolean saved = userDAO.saveUser(user);
        
        if (saved) {
            request.setAttribute("success", "Registration successful! Please login.");
            request.getRequestDispatcher("/views/auth/login.jsp").forward(request, response);
        } else {
            request.setAttribute("error", "Registration failed. Please try again.");
            request.getRequestDispatcher("/views/auth/register.jsp").forward(request, response);
        }
    }
    
    private String generateRegistrationNumber() {
        String year = String.valueOf(LocalDateTime.now().getYear());
        String timestamp = String.valueOf(System.currentTimeMillis()).substring(8);
        return "REG-" + year + "-" + timestamp;
    }
}

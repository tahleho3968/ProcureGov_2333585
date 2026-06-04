<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>ProcureGov - Login</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="auth-container">
        <div class="auth-card">
            <h2>ProcureGov Login</h2>
            <p style="text-align: center; margin-bottom: 20px;">Ministry of Public Works - Lesotho</p>
            
            <% if(request.getAttribute("error") != null) { %>
                <div class="error-message">
                    <%= request.getAttribute("error") %>
                </div>
            <% } %>
            
            <% if(request.getParameter("error") != null) { 
                if(request.getParameter("error").equals("session_expired")) { %>
                    <div class="error-message">Session expired. Please login again.</div>
                <% } else if(request.getParameter("error").equals("access_denied")) { %>
                    <div class="error-message">Access denied. You don't have permission to access that page.</div>
                <% } %>
            <% } %>
            
            <% if(request.getAttribute("success") != null) { %>
                <div class="success-message">
                    <%= request.getAttribute("success") %>
                </div>
            <% } %>
            
            <form action="${pageContext.request.contextPath}/login" method="post">
                <div class="form-group">
                    <label>Email Address</label>
                    <input type="email" name="email" required>
                </div>
                <div class="form-group">
                    <label>Password</label>
                    <input type="password" name="password" required>
                </div>
                <button type="submit" class="btn-submit">Login</button>
            </form>
            
            <div class="auth-footer">
                <p>Don't have an account? <a href="${pageContext.request.contextPath}/register">Register as Supplier</a></p>
                <p><a href="${pageContext.request.contextPath}/">← Back to Home</a></p>
            </div>
            
            <hr style="margin: 20px 0;">
            
            <div style="font-size: 12px; text-align: center;">
                <p><strong>Demo Credentials:</strong></p>
                <p><strong>Procurement Officer:</strong> thabo.molapo@publicworks.gov.ls / Password123</p>
                <p><strong>Evaluation Committee:</strong> lineo.makotoko@publicworks.gov.ls / Password123</p>
                <p><strong>Supplier:</strong> info@lesothoconstruction.co.ls / Password123</p>
            </div>
        </div>
    </div>
</body>
</html>

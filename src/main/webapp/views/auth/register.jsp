<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>ProcureGov - Supplier Registration</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="auth-container">
        <div class="auth-card">
            <h2>Supplier Registration</h2>
            <p style="text-align: center; margin-bottom: 20px;">Ministry of Public Works - Lesotho</p>
            
            <% if(request.getAttribute("error") != null) { %>
                <div class="error-message">
                    <%= request.getAttribute("error") %>
                </div>
            <% } %>
            
            <form action="${pageContext.request.contextPath}/register" method="post">
                <div class="form-group">
                    <label>Username *</label>
                    <input type="text" name="username" required>
                </div>
                
                <div class="form-group">
                    <label>Email *</label>
                    <input type="email" name="email" required>
                </div>
                
                <div class="form-group">
                    <label>Full Name *</label>
                    <input type="text" name="fullName" required>
                </div>
                
                <div class="form-group">
                    <label>Contact Number</label>
                    <input type="tel" name="contactNumber">
                </div>
                
                <div class="form-group">
                    <label>Physical Address</label>
                    <textarea name="physicalAddress" rows="3"></textarea>
                </div>
                
                <div class="form-group">
                    <label>Password *</label>
                    <input type="password" name="password" required>
                    <small style="color: #666;">Minimum 6 characters</small>
                </div>
                
                <div class="form-group">
                    <label>Confirm Password *</label>
                    <input type="password" name="confirmPassword" required>
                </div>
                
                <button type="submit" class="btn-submit">Register as Supplier</button>
            </form>
            
            <div class="auth-footer">
                <p>Already have an account? <a href="${pageContext.request.contextPath}/login">Login here</a></p>
                <p><a href="${pageContext.request.contextPath}/">← Back to Home</a></p>
            </div>
        </div>
    </div>
</body>
</html>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Server Error</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="auth-container">
        <div class="auth-card">
            <h2>500 - Internal Server Error</h2>
            <p>Something went wrong on our end. Please try again later.</p>
            <p><a href="${pageContext.request.contextPath}/">Return to Home</a></p>
        </div>
    </div>
</body>
</html>

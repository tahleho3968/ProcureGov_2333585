<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="com.procuregov.model.Tender" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>View Tender</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .container {
            max-width: 900px;
            margin: 20px auto;
            padding: 20px;
            background: white;
            border-radius: 5px;
            box-shadow: 0 0 10px rgba(0,0,0,0.1);
        }
        .detail-row {
            margin-bottom: 15px;
            padding: 10px;
            border-bottom: 1px solid #eee;
        }
        .label {
            font-weight: bold;
            width: 200px;
            display: inline-block;
        }
        .btn {
            display: inline-block;
            padding: 10px 20px;
            margin-top: 20px;
            background-color: #1a472a;
            color: white;
            text-decoration: none;
            border-radius: 3px;
        }
    </style>
</head>
<body>
    <div class="container">
        <h2>Tender Details</h2>
        
        <%
            Tender tender = (Tender) request.getAttribute("tender");
            if (tender != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        %>
        
        <div class="detail-row">
            <span class="label">Reference Number:</span>
            <span><%= tender.getReferenceNumber() %></span>
        </div>
        
        <div class="detail-row">
            <span class="label">Title:</span>
            <span><%= tender.getTitle() %></span>
        </div>
        
        <div class="detail-row">
            <span class="label">Category:</span>
            <span><%= tender.getCategory() %></span>
        </div>
        
        <div class="detail-row">
            <span class="label">Description:</span>
            <div><%= tender.getDescription() %></div>
        </div>
        
        <div class="detail-row">
            <span class="label">Estimated Value:</span>
            <span>M <%= String.format("%,.2f", tender.getEstimatedValue()) %></span>
        </div>
        
        <div class="detail-row">
            <span class="label">Closing Date & Time:</span>
            <span><%= sdf.format(tender.getClosingDatetime()) %></span>
        </div>
        
        <div class="detail-row">
            <span class="label">Status:</span>
            <span><%= tender.getStatus() %></span>
        </div>
        
        <% } else { %>
            <div class="error">Tender not found</div>
        <% } %>
        
        <a href="${pageContext.request.contextPath}/officer/tender" class="btn">Back to Dashboard</a>
    </div>
</body>
</html>

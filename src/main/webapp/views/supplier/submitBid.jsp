<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Submit Bid</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .form-container {
            max-width: 800px;
            margin: 20px auto;
            padding: 20px;
            background: white;
            border-radius: 5px;
            box-shadow: 0 0 10px rgba(0,0,0,0.1);
        }
        .form-group {
            margin-bottom: 15px;
        }
        label {
            display: block;
            margin-bottom: 5px;
            font-weight: bold;
        }
        input, select, textarea {
            width: 100%;
            padding: 8px;
            border: 1px solid #ddd;
            border-radius: 3px;
        }
        textarea {
            height: 100px;
        }
        .btn-submit {
            background-color: #1a472a;
            color: white;
            padding: 10px 20px;
            border: none;
            border-radius: 3px;
            cursor: pointer;
        }
        .btn-cancel {
            background-color: #6c757d;
            color: white;
            padding: 10px 20px;
            text-decoration: none;
            border-radius: 3px;
            margin-left: 10px;
        }
        .tender-info {
            background-color: #f4f4f4;
            padding: 15px;
            border-radius: 5px;
            margin-bottom: 20px;
        }
        .message {
            padding: 10px;
            margin-bottom: 20px;
            border-radius: 5px;
        }
        .error {
            background-color: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }
    </style>
</head>
<body>
    <div class="form-container">
        <h2>Submit Bid</h2>
        
        <% if(request.getAttribute("error") != null) { %>
            <div class="message error">
                <%= request.getAttribute("error") %>
            </div>
        <% } %>
        
        <div class="tender-info">
            <h3>Tender Information</h3>
            <p><strong>Reference:</strong> ${tender.referenceNumber}</p>
            <p><strong>Title:</strong> ${tender.title}</p>
            <p><strong>Category:</strong> ${tender.category}</p>
            <p><strong>Closing Date:</strong> <fmt:formatDate value="${tender.closingDatetime}" pattern="yyyy-MM-dd HH:mm:ss"/></p>
        </div>
        
        <form action="${pageContext.request.contextPath}/supplier/bid" method="post" enctype="multipart/form-data">
            <input type="hidden" name="tenderId" value="${tender.tenderId}">
            
            <div class="form-group">
                <label>Bid Amount (Maloti) *</label>
                <input type="number" name="bidAmount" step="0.01" required>
                <small>Your proposed price for this tender</small>
            </div>
            
            <div class="form-group">
                <label>Technical Compliance Statement *</label>
                <textarea name="technicalCompliance" maxlength="600" required></textarea>
                <small>Maximum 600 characters. Describe how you meet the technical requirements.</small>
            </div>
            
            <div class="form-group">
                <label>Proposed Delivery Timeline (Days) *</label>
                <input type="number" name="deliveryTimeline" min="1" required>
                <small>Number of days from contract award to completion</small>
            </div>
            
            <div class="form-group">
                <label>Supporting Document (PDF or DOCX, max 10MB) *</label>
                <input type="file" name="supportingDoc" accept=".pdf,.docx" required>
                <small>Upload technical proposal, company profile, or other supporting documents</small>
            </div>
            
            <div class="form-group">
                <button type="submit" class="btn-submit">Submit Bid</button>
                <a href="${pageContext.request.contextPath}/supplier/dashboard" class="btn-cancel">Cancel</a>
            </div>
        </form>
    </div>
</body>
</html>

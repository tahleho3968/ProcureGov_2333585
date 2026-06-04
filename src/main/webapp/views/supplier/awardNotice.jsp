<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="/views/fragments/header.jsp" />
<style>
    .container {
        max-width: 800px;
        margin: 80px auto 20px;
        padding: 20px;
    }
    .award-card {
        background: white;
        border-radius: 10px;
        box-shadow: 0 5px 20px rgba(0,0,0,0.1);
        overflow: hidden;
    }
    .winner-header {
        background: linear-gradient(135deg, #1a472a 0%, #0d2818 100%);
        color: white;
        padding: 30px;
        text-align: center;
    }
    .loser-header {
        background: #6c757d;
        color: white;
        padding: 30px;
        text-align: center;
    }
    .trophy {
        font-size: 60px;
        margin-bottom: 10px;
    }
    .content {
        padding: 30px;
    }
    .info-row {
        margin-bottom: 15px;
        padding-bottom: 10px;
        border-bottom: 1px solid #eee;
    }
    .label {
        font-weight: bold;
        width: 150px;
        display: inline-block;
    }
    .winner-badge {
        background-color: #28a745;
        color: white;
        padding: 5px 15px;
        border-radius: 20px;
        display: inline-block;
        margin-bottom: 20px;
    }
    .loser-badge {
        background-color: #dc3545;
        color: white;
        padding: 5px 15px;
        border-radius: 20px;
        display: inline-block;
        margin-bottom: 20px;
    }
    .btn-back {
        display: inline-block;
        background-color: #1a472a;
        color: white;
        padding: 10px 20px;
        text-decoration: none;
        border-radius: 5px;
        margin-top: 20px;
    }
    .justification {
        background-color: #f8f9fa;
        padding: 15px;
        border-radius: 5px;
        margin-top: 15px;
        border-left: 4px solid #1a472a;
    }
</style>
</head>
<body>
<div class="container">
    <div class="award-card">
        <c:choose>
            <c:when test="${isWinner}">
                <div class="winner-header">
                    <div class="trophy">🏆</div>
                    <h1>CONGRATULATIONS!</h1>
                    <p>You have been awarded the tender</p>
                </div>
                <div class="content">
                    <div class="winner-badge">WINNING BID</div>
                    
                    <div class="info-row">
                        <span class="label">Tender Reference:</span>
                        <span>${tender.referenceNumber}</span>
                    </div>
                    <div class="info-row">
                        <span class="label">Tender Title:</span>
                        <span>${tender.title}</span>
                    </div>
                    <div class="info-row">
                        <span class="label">Your Bid Amount:</span>
                        <span>M <fmt:formatNumber value="${winningBid.bidAmount}" type="number" maxFractionDigits="0"/></span>
                    </div>
                    <div class="info-row">
                        <span class="label">Awarded Value:</span>
                        <span>M <fmt:formatNumber value="${award.awardedValue}" type="number" maxFractionDigits="0"/></span>
                    </div>
                    <div class="info-row">
                        <span class="label">Award Date:</span>
                        <span><fmt:formatDate value="${award.awardDate}" pattern="yyyy-MM-dd HH:mm:ss"/></span>
                    </div>
                    
                    <div class="justification">
                        <strong>Award Justification:</strong><br>
                        ${award.justification}
                    </div>
                    
                    <h3 style="margin-top: 20px;">Next Steps:</h3>
                    <ul>
                        <li>A formal contract will be sent within 7 working days</li>
                        <li>Prepare your project implementation plan</li>
                        <li>Contact the Procurement Officer to schedule a kick-off meeting</li>
                    </ul>
                </div>
            </c:when>
            <c:otherwise>
                <div class="loser-header">
                    <div class="trophy">📋</div>
                    <h1>Tender Award Notice</h1>
                    <p>Thank you for your participation</p>
                </div>
                <div class="content">
                    <div class="loser-badge">NOT WON</div>
                    
                    <div class="info-row">
                        <span class="label">Tender Reference:</span>
                        <span>${tender.referenceNumber}</span>
                    </div>
                    <div class="info-row">
                        <span class="label">Tender Title:</span>
                        <span>${tender.title}</span>
                    </div>
                    <div class="info-row">
                        <span class="label">Award Date:</span>
                        <span><fmt:formatDate value="${award.awardDate}" pattern="yyyy-MM-dd HH:mm:ss"/></span>
                    </div>
                    
                    <div class="justification">
                        <strong>Award Justification:</strong><br>
                        ${award.justification}
                    </div>
                    
                    <p style="margin-top: 20px;">The winning supplier was selected based on the evaluation criteria.<br>
                    We encourage you to continue participating in future tender opportunities.</p>
                </div>
            </c:otherwise>
        </c:choose>
        
        <div style="text-align: center; padding: 20px;">
            <a href="${pageContext.request.contextPath}/supplier/dashboard" class="btn-back">Back to Dashboard</a>
        </div>
    </div>
</div>
</body>
</html>

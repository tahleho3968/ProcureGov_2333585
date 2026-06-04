<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="/views/fragments/header.jsp" />
<style>
    .container {
        max-width: 1200px;
        margin: 80px auto 20px;
        padding: 20px;
    }
    .winner-card {
        background: linear-gradient(135deg, #1a472a 0%, #0d2818 100%);
        color: white;
        border-radius: 10px;
        padding: 20px;
        margin-bottom: 30px;
        text-align: center;
    }
    .winner-card h2 {
        margin: 0 0 10px 0;
        font-size: 28px;
    }
    .winner-card .trophy {
        font-size: 50px;
        margin-bottom: 10px;
    }
    .results-table {
        width: 100%;
        border-collapse: collapse;
        margin-top: 20px;
    }
    .results-table th, .results-table td {
        border: 1px solid #ddd;
        padding: 12px;
        text-align: left;
    }
    .results-table th {
        background-color: #1a472a;
        color: white;
    }
    .results-table tr:nth-child(even) {
        background-color: #f2f2f2;
    }
    .winner-row {
        background-color: #d4edda !important;
        font-weight: bold;
    }
    .rank-1 {
        background-color: #ffd700;
        color: #333;
    }
    .btn-back {
        display: inline-block;
        padding: 10px 20px;
        background-color: #1a472a;
        color: white;
        text-decoration: none;
        border-radius: 5px;
        margin-top: 20px;
    }
    .score-highlight {
        font-size: 18px;
        font-weight: bold;
        color: #1a472a;
    }
    .info-box {
        background-color: #d1ecf1;
        color: #0c5460;
        padding: 15px;
        border-radius: 5px;
        margin-bottom: 20px;
    }
</style>
</head>
<body>
<div class="container">
    <h1>Evaluation Results</h1>
    <h2>Tender: ${tender.referenceNumber} - ${tender.title}</h2>
    
    <div class="info-box">
        <strong>📊 Evaluation Summary:</strong>
        <ul>
            <li>Total Evaluators: ${totalEvaluators}</li>
            <li>Number of Bids Received: ${results.size()}</li>
            <li>Evaluation Date: <fmt:formatDate value="${tender.updatedAt}" pattern="yyyy-MM-dd HH:mm:ss"/></li>
        </ul>
    </div>
    
    <!-- Winner Announcement -->
    <c:if test="${not empty winner}">
        <div class="winner-card">
            <div class="trophy">🏆</div>
            <h2>WINNING BID</h2>
            <h3>${winner.supplierName}</h3>
            <p>Registration: ${winner.registrationNumber}</p>
            <p>Final Score: <fmt:formatNumber value="${winner.finalScore}" maxFractionDigits="2"/>%</p>
            <p>Bid Amount: M <fmt:formatNumber value="${winner.bidAmount}" type="number" maxFractionDigits="0"/></p>
        </div>
    </c:if>
    
    <!-- All Results Table -->
    <h3>Detailed Results (Ranked by Score)</h3>
    <table class="results-table">
        <thead>
            <tr>
                <th>Rank</th>
                <th>Supplier Name</th>
                <th>Registration Number</th>
                <th>Bid Amount (M)</th>
                <th>Price Score (40%)</th>
                <th>Tech Score (35%)</th>
                <th>Delivery Score (25%)</th>
                <th>Final Score</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach items="${results}" var="result" varStatus="status">
                <tr class="${status.index == 0 ? 'winner-row' : ''}">
                    <td class="${status.index == 0 ? 'rank-1' : ''}">
                        <c:choose>
                            <c:when test="${status.index == 0}">🥇 1st</c:when>
                            <c:when test="${status.index == 1}">🥈 2nd</c:when>
                            <c:when test="${status.index == 2}">🥉 3rd</c:when>
                            <c:otherwise>${status.index + 1}th</c:otherwise>
                        </c:choose>
                    </td>
                    <td>${result.supplierName}</td>
                    <td>${result.registrationNumber}</td>
                    <td><fmt:formatNumber value="${result.bidAmount}" type="number" maxFractionDigits="0"/></td>
                    <td><fmt:formatNumber value="${result.priceScore}" maxFractionDigits="2"/>%</td>
                    <td><fmt:formatNumber value="${result.technicalScore}" maxFractionDigits="2"/>%</td>
                    <td><fmt:formatNumber value="${result.deliveryScore}" maxFractionDigits="2"/>%</td>
                    <td class="score-highlight">
                        <fmt:formatNumber value="${result.finalScore}" maxFractionDigits="2"/>%
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
    
    <div style="margin-top: 30px; text-align: center;">
        <a href="${pageContext.request.contextPath}/evaluator/dashboard" class="btn-back">Back to Dashboard</a>
    </div>
</div>
</body>
</html>

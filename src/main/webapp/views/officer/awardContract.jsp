<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="/views/fragments/header.jsp" />
<style>
    .container {
        max-width: 1000px;
        margin: 80px auto 20px;
        padding: 20px;
    }
    .ranked-list {
        margin: 20px 0;
    }
    .rank-card {
        background: white;
        border-radius: 8px;
        box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        margin-bottom: 15px;
        padding: 15px;
        cursor: pointer;
        transition: all 0.3s ease;
    }
    .rank-card:hover {
        transform: translateX(5px);
        box-shadow: 0 5px 20px rgba(0,0,0,0.15);
    }
    .rank-card.selected {
        border: 3px solid #1a472a;
        background-color: #e8f5e9;
    }
    .rank-1 {
        border-left: 5px solid #ffd700;
    }
    .rank-2 {
        border-left: 5px solid #c0c0c0;
    }
    .rank-3 {
        border-left: 5px solid #cd7f32;
    }
    .rank-number {
        font-size: 24px;
        font-weight: bold;
        display: inline-block;
        width: 60px;
    }
    .supplier-name {
        font-size: 18px;
        font-weight: bold;
        color: #1a472a;
    }
    .bid-amount {
        float: right;
        font-size: 18px;
    }
    .final-score {
        color: #1a472a;
        font-weight: bold;
    }
    .form-group {
        margin-bottom: 15px;
    }
    label {
        font-weight: bold;
        display: block;
        margin-bottom: 5px;
    }
    input, textarea {
        width: 100%;
        padding: 10px;
        border: 1px solid #ddd;
        border-radius: 5px;
    }
    textarea {
        height: 100px;
    }
    .btn-submit {
        background-color: #1a472a;
        color: white;
        padding: 12px 30px;
        border: none;
        border-radius: 5px;
        cursor: pointer;
        font-size: 16px;
    }
    .btn-submit:disabled {
        background-color: #ccc;
        cursor: not-allowed;
    }
    .info-box {
        background-color: #d1ecf1;
        color: #0c5460;
        padding: 15px;
        border-radius: 5px;
        margin-bottom: 20px;
    }
    .selected-info {
        background-color: #e8f5e9;
        padding: 15px;
        border-radius: 5px;
        margin: 20px 0;
        border-left: 4px solid #1a472a;
    }
    .btn-cancel {
        display: inline-block;
        background-color: #6c757d;
        color: white;
        padding: 12px 30px;
        text-decoration: none;
        border-radius: 5px;
        margin-left: 10px;
    }
</style>
</head>
<body>
<div class="container">
    <h1>Award Contract</h1>
    <h2>Tender: ${tender.referenceNumber} - ${tender.title}</h2>
    
    <div class="info-box">
        <strong>📋 Award Guidelines:</strong>
        <ul>
            <li>The winning supplier is determined by the highest final evaluation score</li>
            <li>Click on a supplier to select them as the winner</li>
            <li>Provide a brief justification for the award decision</li>
            <li>Email notifications will be sent to ALL suppliers automatically</li>
        </ul>
    </div>
    
    <h3>Ranked Suppliers (by Evaluation Score)</h3>
    <div class="ranked-list">
        <c:forEach items="${rankedBids}" var="bid" varStatus="status">
            <div class="rank-card rank-${status.index + 1}" onclick="selectWinner(${bid.bidId}, '${bid.supplierName}', ${bid.bidAmount})">
                <div class="rank-number">${status.index + 1}${status.index == 0 ? 'st' : status.index == 1 ? 'nd' : status.index == 2 ? 'rd' : 'th'}</div>
                <span class="supplier-name">${bid.supplierName}</span>
                <span class="bid-amount">
                    Score: <span class="final-score"><fmt:formatNumber value="${bid.finalScore}" maxFractionDigits="2"/>%</span> |
                    Bid: M <fmt:formatNumber value="${bid.bidAmount}" type="number" maxFractionDigits="0"/>
                </span>
            </div>
        </c:forEach>
    </div>
    
    <form id="awardForm" action="${pageContext.request.contextPath}/officer/award" method="post">
        <input type="hidden" name="tenderId" value="${tender.tenderId}">
        <input type="hidden" name="winningBidId" id="winningBidId">
        
        <div class="selected-info" id="selectedInfo" style="display: none;">
            <strong>🏆 Selected Winner:</strong> <span id="selectedSupplier"></span><br>
            <strong>💰 Bid Amount:</strong> M <span id="selectedAmount"></span>
        </div>
        
        <div class="form-group">
            <label>Awarded Value (Maloti) *</label>
            <input type="number" name="awardedValue" id="awardedValue" step="0.01" required>
            <small>The final contract value (may be negotiated from the bid amount)</small>
        </div>
        
        <div class="form-group">
            <label>Award Justification *</label>
            <textarea name="justification" id="justification" required placeholder="Explain why this supplier was selected..."></textarea>
            <small>This justification will be included in the email notifications to all suppliers</small>
        </div>
        
        <button type="submit" class="btn-submit" id="submitBtn" disabled>Award Contract & Send Notifications</button>
        <a href="${pageContext.request.contextPath}/officer/tender" class="btn-cancel">Cancel</a>
    </form>
</div>

<script>
    let selectedBidId = null;
    
    function selectWinner(bidId, supplierName, bidAmount) {
        document.querySelectorAll('.rank-card').forEach(card => {
            card.classList.remove('selected');
        });
        
        event.currentTarget.classList.add('selected');
        
        selectedBidId = bidId;
        document.getElementById('winningBidId').value = bidId;
        
        document.getElementById('selectedSupplier').innerHTML = supplierName;
        document.getElementById('selectedAmount').innerHTML = bidAmount.toLocaleString();
        document.getElementById('selectedInfo').style.display = 'block';
        
        document.getElementById('awardedValue').value = bidAmount;
        
        document.getElementById('submitBtn').disabled = false;
    }
</script>
</body>
</html>

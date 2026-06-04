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
    .bid-card {
        background: white;
        border-radius: 8px;
        box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        margin-bottom: 20px;
        padding: 20px;
    }
    .bid-header {
        background: #1a472a;
        color: white;
        padding: 10px;
        margin: -20px -20px 20px -20px;
        border-radius: 8px 8px 0 0;
    }
    .form-group {
        margin-bottom: 15px;
    }
    label {
        font-weight: bold;
        display: inline-block;
        width: 200px;
    }
    input[type="number"] {
        width: 100px;
        padding: 5px;
    }
    .score-display {
        font-size: 18px;
        font-weight: bold;
        color: #1a472a;
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
    .info {
        background-color: #d1ecf1;
        color: #0c5460;
        padding: 10px;
        border-radius: 5px;
        margin-bottom: 20px;
    }
    .progress-container {
        background-color: #e0e0e0;
        border-radius: 10px;
        margin: 20px 0;
        height: 30px;
        position: relative;
    }
    .progress-bar {
        background-color: #1a472a;
        height: 30px;
        border-radius: 10px;
        width: 0%;
        transition: width 0.5s ease;
        display: flex;
        align-items: center;
        justify-content: center;
        color: white;
        font-weight: bold;
    }
    .warning {
        background-color: #fff3cd;
        color: #856404;
        padding: 10px;
        border-radius: 5px;
        margin-bottom: 20px;
        border-left: 4px solid #ffc107;
    }
    .success {
        background-color: #d4edda;
        color: #155724;
        padding: 10px;
        border-radius: 5px;
        margin-bottom: 20px;
        border-left: 4px solid #28a745;
    }
</style>
</head>
<body>
<div class="container">
    <h2>Evaluate Bids for Tender: ${tender.referenceNumber}</h2>
    <h3>${tender.title}</h3>
    
    <!-- Progress Bar -->
    <div class="progress-container">
        <div class="progress-bar" style="width: ${(completedEvaluators / totalEvaluators) * 100}%;">
            ${completedEvaluators}/${totalEvaluators} Evaluators Completed
        </div>
    </div>
    
    <c:choose>
        <c:when test="${hasEvaluatorScored}">
            <div class="warning">
                <strong>⚠️ Note:</strong> You have already submitted your scores for this tender. 
                You can modify your scores below and resubmit.
            </div>
        </c:when>
        <c:otherwise>
            <div class="info">
                <strong>📋 Evaluation Guidelines:</strong>
                <ul>
                    <li>Price Score is calculated automatically based on the lowest bid (40% weight)</li>
                    <li>Technical Score: Enter a score from 0 to 100 (35% weight)</li>
                    <li>Delivery Score is calculated automatically based on the shortest timeline (25% weight)</li>
                    <li><strong>${totalEvaluators} evaluators</strong> need to score before tender is finalized</li>
                </ul>
            </div>
        </c:otherwise>
    </c:choose>
    
    <form action="${pageContext.request.contextPath}/evaluator/evaluate" method="post">
        <input type="hidden" name="tenderId" value="${tender.tenderId}">
        
        <c:forEach items="${bids}" var="bid">
            <div class="bid-card">
                <div class="bid-header">
                    <h3>Bid from: ${bid.supplierName} (${bid.registrationNumber})</h3>
                </div>
                
                <div class="form-group">
                    <label>Bid Amount (M):</label>
                    <fmt:formatNumber value="${bid.bidAmount}" type="number" maxFractionDigits="0"/>
                </div>
                
                <div class="form-group">
                    <label>Technical Compliance:</label>
                    ${bid.technicalCompliance}
                </div>
                
                <div class="form-group">
                    <label>Proposed Timeline (Days):</label>
                    ${bid.deliveryTimeline}
                </div>
                
                <div class="form-group">
                    <label>Price Score (Auto - 40%):</label>
                    <span class="score-display" id="priceScore_${bid.bidId}">Calculating...</span>
                </div>
                
                <div class="form-group">
                    <label>Technical Score (0-100 - 35%):</label>
                    <input type="number" name="technical_score_${bid.bidId}" min="0" max="100" step="1" 
                           value="${bid.technicalScore >= 0 ? bid.technicalScore : ''}" required>
                </div>
                
                <div class="form-group">
                    <label>Delivery Score (Auto - 25%):</label>
                    <span class="score-display" id="deliveryScore_${bid.bidId}">Calculating...</span>
                </div>
                
                <div class="form-group">
                    <label>Weighted Total:</label>
                    <span class="score-display" id="weightedTotal_${bid.bidId}">-</span>
                </div>
            </div>
        </c:forEach>
        
        <div class="form-group">
            <button type="submit" class="btn-submit">Submit All Scores</button>
            <a href="${pageContext.request.contextPath}/evaluator/dashboard" class="btn-cancel">Cancel</a>
        </div>
    </form>
</div>

<script>
    // Get all bids data from server
    const bidsData = [
        <c:forEach items="${bids}" var="bid" varStatus="status">
        {
            bidId: ${bid.bidId},
            bidAmount: ${bid.bidAmount},
            deliveryTimeline: ${bid.deliveryTimeline}
        }${not status.last ? ',' : ''}
        </c:forEach>
    ];
    
    // Calculate lowest bid amount and shortest timeline
    let lowestBid = Infinity;
    let shortestTimeline = Infinity;
    
    bidsData.forEach(bid => {
        if (bid.bidAmount < lowestBid) lowestBid = bid.bidAmount;
        if (bid.deliveryTimeline < shortestTimeline) shortestTimeline = bid.deliveryTimeline;
    });
    
    // Calculate and display scores for each bid
    bidsData.forEach(bid => {
        const priceScore = (lowestBid / bid.bidAmount) * 100;
        const deliveryScore = (shortestTimeline / bid.deliveryTimeline) * 100;
        
        document.getElementById(`priceScore_${bid.bidId}`).innerHTML = priceScore.toFixed(2) + '%';
        document.getElementById(`deliveryScore_${bid.bidId}`).innerHTML = deliveryScore.toFixed(2) + '%';
        
        // Add event listener for technical score input
        const technicalInput = document.querySelector(`input[name="technical_score_${bid.bidId}"]`);
        if (technicalInput) {
            technicalInput.addEventListener('input', function() {
                const technicalScore = parseFloat(this.value) || 0;
                const weightedTotal = (priceScore * 0.40) + (technicalScore * 0.35) + (deliveryScore * 0.25);
                document.getElementById(`weightedTotal_${bid.bidId}`).innerHTML = weightedTotal.toFixed(2) + '%';
            });
            
            // Trigger initial calculation
            technicalInput.dispatchEvent(new Event('input'));
        }
    });
</script>
</body>
</html>

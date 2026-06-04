<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page import="com.procuregov.dao.TenderDAO" %>
<%@ page import="com.procuregov.dao.TenderDAOImpl" %>
<%@ page import="com.procuregov.dao.BidDAO" %>
<%@ page import="com.procuregov.dao.BidDAOImpl" %>
<%@ page import="com.procuregov.model.Tender" %>
<%@ page import="com.procuregov.model.Bid" %>
<%@ page import="com.procuregov.model.User" %>
<%@ page import="com.procuregov.service.TenderStatusService" %>
<%@ page import="java.util.List" %>
<jsp:include page="/views/fragments/header.jsp" />
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>ProcureGov - Supplier Dashboard</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .dashboard-container {
            max-width: 1200px;
            margin: 0 auto;
            padding: 20px;
        }
        .header {
            background: #1a472a;
            color: white;
            padding: 15px;
            margin-bottom: 20px;
            border-radius: 5px;
        }
        .nav {
            background: #333;
            overflow: hidden;
            margin-bottom: 20px;
        }
        .nav a {
            float: left;
            color: white;
            text-align: center;
            padding: 14px 16px;
            text-decoration: none;
        }
        .nav a:hover {
            background-color: #1a472a;
        }
        .nav .right {
            float: right;
        }
        .btn {
            display: inline-block;
            padding: 8px 15px;
            background-color: #1a472a;
            color: white;
            text-decoration: none;
            border-radius: 3px;
            margin: 5px;
            font-size: 12px;
        }
        .btn-small {
            padding: 5px 10px;
            font-size: 11px;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 10px;
        }
        th, td {
            border: 1px solid #ddd;
            padding: 10px;
            text-align: left;
        }
        th {
            background-color: #1a472a;
            color: white;
        }
        .status-badge {
            display: inline-block;
            padding: 3px 8px;
            border-radius: 3px;
            font-size: 12px;
            font-weight: bold;
        }
        .status-OPEN { background-color: #28a745; color: white; }
        .status-CLOSED { background-color: #17a2b8; color: white; }
        .status-AWARDED { background-color: #1a472a; color: white; }
        .status-UNDER_EVALUATION { background-color: #ffc107; color: #333; }
        .status-EVALUATED { background-color: #fd7e14; color: white; }
        .message {
            padding: 10px;
            margin-bottom: 20px;
            border-radius: 5px;
        }
        .success {
            background-color: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
        }
        .error {
            background-color: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }
        .section-title {
            margin-top: 30px;
            margin-bottom: 15px;
            padding-bottom: 10px;
            border-bottom: 2px solid #1a472a;
            color: #1a472a;
        }
        .bid-status {
            font-size: 11px;
            padding: 2px 6px;
            border-radius: 3px;
        }
        .bid-pending { background-color: #ffc107; color: #333; }
        .bid-winning { background-color: #28a745; color: white; }
        .bid-lost { background-color: #dc3545; color: white; }
    </style>
</head>
<body>
    <div class="dashboard-container">
        <div class="header">
            <h1>Supplier Dashboard</h1>
            <p>Welcome, ${sessionScope.username} | ${sessionScope.user.fullName}</p>
        </div>
        
        <div class="nav">
            <a href="${pageContext.request.contextPath}/supplier/dashboard">Dashboard</a>
            <a href="${pageContext.request.contextPath}/logout" class="right">Logout</a>
        </div>
        
        <% if(request.getAttribute("success") != null) { %>
            <div class="message success">
                <%= request.getAttribute("success") %>
            </div>
        <% } %>
        
        <% if(request.getAttribute("error") != null) { %>
            <div class="message error">
                <%= request.getAttribute("error") %>
            </div>
        <% } %>
        
        <%
            // Auto-close expired tenders before displaying
            TenderStatusService statusService = new TenderStatusService();
            statusService.checkAndCloseExpiredTenders();
            
            // Get data for dashboard
            TenderDAO tenderDAO = new TenderDAOImpl();
            BidDAO bidDAO = new BidDAOImpl();
            List<Tender> openTenders = tenderDAO.findOpenTenders();
            
            User user = (User) session.getAttribute("user");
            List<Bid> myBids = bidDAO.findBidsBySupplier(user.getUserId());
            
            request.setAttribute("openTenders", openTenders);
            request.setAttribute("myBids", myBids);
        %>
        
        <!-- Open Tenders Section -->
        <h2 class="section-title">Open Tenders for Bidding</h2>
        <c:choose>
            <c:when test="${empty openTenders}">
                <p>No open tenders available at this time.</p>
            </c:when>
            <c:otherwise>
                <table>
                    <thead>
                        <tr>
                            <th>Reference</th>
                            <th>Title</th>
                            <th>Category</th>
                            <th>Estimated Value (M)</th>
                            <th>Closing Date</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${openTenders}" var="tender">
                            <tr>
                                <td>${tender.referenceNumber}</td>
                                <td>${tender.title}</td>
                                <td>${tender.category}</td>
                                <td><fmt:formatNumber value="${tender.estimatedValue}" type="number" maxFractionDigits="0"/></td>
                                <td><fmt:formatDate value="${tender.closingDatetime}" pattern="yyyy-MM-dd HH:mm"/></td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/supplier/bid?action=submit&tenderId=${tender.tenderId}" class="btn btn-small">Submit Bid</a>
                                    <a href="${pageContext.request.contextPath}/download?tenderId=${tender.tenderId}" class="btn btn-small">Download Notice</a>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>
        
	<!-- My Bids Section -->
	<h2 class="section-title">My Submitted Bids</h2>
	<c:choose>
	    <c:when test="${empty myBids}">
		<p>You haven't submitted any bids yet.</p>
	    </c:when>
	    <c:otherwise>
		<table>
		    <thead>
		        <tr>
		            <th>Reference</th>
		            <th>Tender Title</th>
		            <th>Bid Amount (M)</th>
		            <th>Delivery Timeline (Days)</th>
		            <th>Submission Date</th>
		            <th>Tender Status</th>
		            <th>Bid Status</th>
		            <th>Award Notice</th>
		        </tr>
		    </thead>
		    <tbody>
		        <c:forEach items="${myBids}" var="bid">
		            <%
		                Bid bidItem = (Bid) pageContext.getAttribute("bid");
		                Tender tender = tenderDAO.findById(bidItem.getTenderId());
		                pageContext.setAttribute("tenderStatus", tender != null ? tender.getStatus() : "");
		                pageContext.setAttribute("isWinning", bidItem.isWinningBid());
		                pageContext.setAttribute("tenderId", bidItem.getTenderId());
		                pageContext.setAttribute("tenderRef", tender != null ? tender.getReferenceNumber() : "");
		                pageContext.setAttribute("tenderTitle", tender != null ? tender.getTitle() : "");
		            %>
		            <tr>
		                <td>${tenderRef}</td>
		                <td>${tenderTitle}</td>
		                <td><fmt:formatNumber value="${bid.bidAmount}" type="number" maxFractionDigits="0"/></td>
		                <td>${bid.deliveryTimeline}</td>
		                <td><fmt:formatDate value="${bid.submissionDatetime}" pattern="yyyy-MM-dd HH:mm"/></td>
		                <td>
		                    <span class="status-badge status-${tenderStatus}">${tenderStatus}</span>
		                </td>
		                <td>
		                    <c:choose>
		                        <c:when test="${tenderStatus == 'AWARDED' and isWinning}">
		                            <span class="bid-status bid-winning">WON</span>
		                        </c:when>
		                        <c:when test="${tenderStatus == 'AWARDED' and not isWinning}">
		                            <span class="bid-status bid-lost">NOT WON</span>
		                        </c:when>
		                        <c:otherwise>
		                            <span class="bid-status bid-pending">PENDING</span>
		                        </c:otherwise>
		                    </c:choose>
		                </td>
		                <td>
		                    <c:if test="${tenderStatus == 'AWARDED'}">
		                        <a href="${pageContext.request.contextPath}/supplier/awardNotice?tenderId=${tenderId}" class="btn btn-small">View Notice</a>
		                    </c:if>
		                </td>
		            </tr>
		        </c:forEach>
		    </tbody>
		</table>
	    </c:otherwise>
	</c:choose>
    </div>
</body>
</html>

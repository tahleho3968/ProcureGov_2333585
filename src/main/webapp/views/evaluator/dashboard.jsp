<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page import="com.procuregov.dao.TenderDAO" %>
<%@ page import="com.procuregov.dao.TenderDAOImpl" %>
<%@ page import="com.procuregov.dao.EvaluationDAO" %>
<%@ page import="com.procuregov.dao.EvaluationDAOImpl" %>
<%@ page import="com.procuregov.model.Tender" %>
<%@ page import="com.procuregov.model.User" %>
<%@ page import="com.procuregov.service.TenderStatusService" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %>
<jsp:include page="/views/fragments/header.jsp" />
<style>
    .dashboard-container {
        max-width: 1200px;
        margin: 0 auto;
        padding: 20px;
        margin-top: 60px;
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
    .btn-warning {
        background-color: #ffc107;
        color: #333;
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
    .status-UNDER_EVALUATION { background-color: #ffc107; color: #333; }
    .status-EVALUATED { background-color: #28a745; color: white; }
    .status-AWARDED { background-color: #1a472a; color: white; }
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
    .progress-container {
        background-color: #e0e0e0;
        border-radius: 10px;
        height: 20px;
        width: 100%;
        overflow: hidden;
    }
    .progress-bar {
        background-color: #1a472a;
        height: 20px;
        border-radius: 10px;
        width: 0%;
        transition: width 0.5s ease;
        display: flex;
        align-items: center;
        justify-content: center;
        color: white;
        font-size: 11px;
        font-weight: bold;
    }
    .completed-badge {
        display: inline-block;
        background-color: #28a745;
        color: white;
        padding: 2px 8px;
        border-radius: 10px;
        font-size: 11px;
    }
    .pending-badge {
        display: inline-block;
        background-color: #ffc107;
        color: #333;
        padding: 2px 8px;
        border-radius: 10px;
        font-size: 11px;
    }
</style>
</head>
<body>
<div class="dashboard-container">
    <div class="header">
        <h1>Evaluation Committee Dashboard</h1>
        <p>Welcome, ${sessionScope.username} | Ministry of Public Works</p>
    </div>
    
    <div class="nav">
        <a href="${pageContext.request.contextPath}/evaluator/dashboard">Dashboard</a>
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
        // Auto-close expired tenders
        TenderStatusService statusService = new TenderStatusService();
        statusService.checkAndCloseExpiredTenders();
        
        // Get tenders under evaluation
        TenderDAO tenderDAO = new TenderDAOImpl();
        EvaluationDAO evaluationDAO = new EvaluationDAOImpl();
        
        List<Tender> underEvaluationTenders = tenderDAO.findTendersByStatus("UNDER_EVALUATION");
        List<Tender> evaluatedTenders = tenderDAO.findTendersByStatus("EVALUATED");
        
        int totalEvaluators = evaluationDAO.getEvaluatorCount();
        
        // Create a map of progress for each tender (Java 7 compatible)
        Map<Integer, Integer> tenderProgress = new HashMap<Integer, Integer>();
        for (Tender tender : underEvaluationTenders) {
            int completed = evaluationDAO.getCompletedEvaluatorCountForTender(tender.getTenderId());
            tenderProgress.put(tender.getTenderId(), completed);
        }
        
        request.setAttribute("underEvaluationTenders", underEvaluationTenders);
        request.setAttribute("evaluatedTenders", evaluatedTenders);
        request.setAttribute("totalEvaluators", totalEvaluators);
        
        // Get current user to check if they've scored
        User currentUser = (User) session.getAttribute("user");
        int currentUserId = currentUser != null ? currentUser.getUserId() : 0;
        request.setAttribute("currentUserId", currentUserId);
    %>
    
    <!-- Tenders Under Evaluation Section -->
    <h2 class="section-title">Tenders Ready for Evaluation</h2>
    <c:choose>
        <c:when test="${empty underEvaluationTenders}">
            <p>No tenders are currently under evaluation.</p>
        </c:when>
        <c:otherwise>
            <table>
                <thead>
                    <tr>
                        <th>Reference</th>
                        <th>Title</th>
                        <th>Category</th>
                        <th>Closing Date</th>
                        <th>Progress</th>
                        <th>Your Status</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${underEvaluationTenders}" var="tender">
                        <%
                            int tenderId = ((Tender) pageContext.getAttribute("tender")).getTenderId();
                            int completed = tenderProgress.containsKey(tenderId) ? tenderProgress.get(tenderId) : 0;
                            boolean hasScored = evaluationDAO.hasEvaluatorScored(tenderId, currentUserId);
                            pageContext.setAttribute("completedCount", completed);
                            pageContext.setAttribute("hasScored", hasScored);
                        %>
                        <tr>
                            <td>${tender.referenceNumber}</td>
                            <td>${tender.title}</td>
                            <td>${tender.category}</td>
                            <td><fmt:formatDate value="${tender.closingDatetime}" pattern="yyyy-MM-dd HH:mm"/></td>
                            <td>
                                <div class="progress-container">
                                    <div class="progress-bar" style="width: ${(completedCount / totalEvaluators) * 100}%;">
                                        ${completedCount}/${totalEvaluators}
                                    </div>
                                </div>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${hasScored}">
                                        <span class="completed-badge">✓ You have scored</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="pending-badge">⏳ Pending</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <a href="${pageContext.request.contextPath}/evaluator/evaluate?tenderId=${tender.tenderId}" class="btn btn-small">
                                    ${hasScored ? 'Update Scores' : 'Evaluate Bids'}
                                </a>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>
    
    <!-- Evaluated Tenders Section -->
    <h2 class="section-title">Evaluated Tenders</h2>
    <c:choose>
        <c:when test="${empty evaluatedTenders}">
            <p>No tenders have been fully evaluated yet.</p>
        </c:when>
        <c:otherwise>
            <table>
                <thead>
                    <tr>
                        <th>Reference</th>
                        <th>Title</th>
                        <th>Category</th>
                        <th>Closing Date</th>
                        <th>Status</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${evaluatedTenders}" var="tender">
                        <tr>
                            <td>${tender.referenceNumber}</td>
                            <td>${tender.title}</td>
                            <td>${tender.category}</td>
                            <td><fmt:formatDate value="${tender.closingDatetime}" pattern="yyyy-MM-dd HH:mm"/></td>
                            <td>
                                <span class="status-badge status-${tender.status}">${tender.status}</span>
                            </td>
                            <td>
                                <a href="${pageContext.request.contextPath}/evaluator/viewResults?tenderId=${tender.tenderId}" class="btn btn-small">View Results</a>
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

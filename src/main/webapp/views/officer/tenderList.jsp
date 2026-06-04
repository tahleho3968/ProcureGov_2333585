<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
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
            padding: 10px 15px;
            background-color: #1a472a;
            color: white;
            text-decoration: none;
            border-radius: 3px;
            margin: 5px;
        }
        .btn-danger {
            background-color: #dc3545;
        }
        .btn-warning {
            background-color: #ffc107;
            color: #333;
        }
        .btn-info {
            background-color: #17a2b8;
        }
        table {
            width: 100%;
            border-collapse: collapse;
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
        .filter-bar {
            margin-bottom: 20px;
            padding: 15px;
            background: #f4f4f4;
            border-radius: 5px;
        }
        .status-badge {
            display: inline-block;
            padding: 3px 8px;
            border-radius: 3px;
            font-size: 12px;
            font-weight: bold;
        }
        .status-DRAFT { background-color: #6c757d; color: white; }
        .status-OPEN { background-color: #28a745; color: white; }
        .status-CLOSED { background-color: #17a2b8; color: white; }
        .status-UNDER_EVALUATION { background-color: #ffc107; color: #333; }
        .status-EVALUATED { background-color: #fd7e14; color: white; }
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
    </style>
</head>
<body>
    <div class="dashboard-container">
        <div class="header">
            <h1>Procurement Officer Dashboard</h1>
            <p>Welcome, ${sessionScope.username} | Ministry of Public Works</p>
        </div>
        
        <div class="nav">
            <a href="${pageContext.request.contextPath}/officer/tender">Dashboard</a>
            <a href="${pageContext.request.contextPath}/officer/tender?action=create">Create New Tender</a>
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
        
        <div class="filter-bar">
            <h3>Filter Tenders</h3>
            <form method="get" action="${pageContext.request.contextPath}/officer/tender">
                <label>Status:</label>
                <select name="status">
                    <option value="">All</option>
                    <option value="DRAFT" ${statusFilter == 'DRAFT' ? 'selected' : ''}>Draft</option>
                    <option value="OPEN" ${statusFilter == 'OPEN' ? 'selected' : ''}>Open</option>
                    <option value="CLOSED" ${statusFilter == 'CLOSED' ? 'selected' : ''}>Closed</option>
                    <option value="UNDER_EVALUATION" ${statusFilter == 'UNDER_EVALUATION' ? 'selected' : ''}>Under Evaluation</option>
                    <option value="EVALUATED" ${statusFilter == 'EVALUATED' ? 'selected' : ''}>Evaluated</option>
                    <option value="AWARDED" ${statusFilter == 'AWARDED' ? 'selected' : ''}>Awarded</option>
                </select>
                
                <label>Category:</label>
                <select name="category">
                    <option value="">All</option>
                    <option value="Construction" ${categoryFilter == 'Construction' ? 'selected' : ''}>Construction</option>
                    <option value="Roads" ${categoryFilter == 'Roads' ? 'selected' : ''}>Roads</option>
                    <option value="Electrical" ${categoryFilter == 'Electrical' ? 'selected' : ''}>Electrical</option>
                    <option value="Plumbing" ${categoryFilter == 'Plumbing' ? 'selected' : ''}>Plumbing</option>
                    <option value="General Services" ${categoryFilter == 'General Services' ? 'selected' : ''}>General Services</option>
                </select>
                
                <button type="submit" class="btn">Filter</button>
                <a href="${pageContext.request.contextPath}/officer/tender" class="btn">Clear</a>
            </form>
        </div>
        
        <h2>Tender List</h2>
        <table>
            <thead>
                <tr>
                    <th>Reference</th>
                    <th>Title</th>
                    <th>Category</th>
                    <th>Estimated Value (M)</th>
                    <th>Closing Date</th>
                    <th>Status</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${tenders}" var="tender">
                    <tr>
                        <td>${tender.referenceNumber}</td>
                        <td>${tender.title}</td>
                        <td>${tender.category}</td>
                        <td><fmt:formatNumber value="${tender.estimatedValue}" type="number" maxFractionDigits="0"/></td>
                        <td><fmt:formatDate value="${tender.closingDatetime}" pattern="yyyy-MM-dd HH:mm"/></td>
                        <td>
                            <span class="status-badge status-${tender.status}">
                                ${tender.status}
                            </span>
                        </td>
                        <td>
                            <a href="${pageContext.request.contextPath}/download?tenderId=${tender.tenderId}" class="btn btn-info">Download</a>
                            <c:if test="${tender.status == 'DRAFT'}">
                                <a href="${pageContext.request.contextPath}/officer/tender?action=edit&id=${tender.tenderId}" class="btn">Edit</a>
                                <a href="${pageContext.request.contextPath}/officer/tender?action=delete&id=${tender.tenderId}" class="btn btn-danger" onclick="return confirm('Are you sure?')">Delete</a>
                                <a href="${pageContext.request.contextPath}/officer/tender?action=changeStatus&id=${tender.tenderId}&status=OPEN" class="btn">Publish</a>
                            </c:if>
                            <c:if test="${tender.status == 'OPEN'}">
                                <a href="${pageContext.request.contextPath}/officer/tender?action=changeStatus&id=${tender.tenderId}&status=CLOSED" class="btn btn-warning">Close</a>
                            </c:if>
                            <c:if test="${tender.status == 'CLOSED'}">
                                <a href="${pageContext.request.contextPath}/officer/tender?action=changeStatus&id=${tender.tenderId}&status=UNDER_EVALUATION" class="btn">Start Evaluation</a>
                            </c:if>
                            <c:if test="${tender.status == 'EVALUATED'}">
                                <a href="${pageContext.request.contextPath}/officer/tender?action=changeStatus&id=${tender.tenderId}&status=AWARDED" class="btn">Award Contract</a>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty tenders}">
                    <tr>
                        <td colspan="7" style="text-align: center;">No tenders found</td>
                    </tr>
                </c:if>
            </tbody>
        </table>
    </div>
</body>
</html>

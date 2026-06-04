<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="com.procuregov.model.Tender" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Edit Tender</title>
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
        .info {
            background-color: #d1ecf1;
            color: #0c5460;
            border: 1px solid #bee5eb;
        }
        .readonly-field {
            background-color: #e9ecef;
        }
    </style>
</head>
<body>
    <div class="form-container">
        <h2>Edit Tender</h2>
        
        <% if(request.getAttribute("error") != null) { %>
            <div class="message error">
                <%= request.getAttribute("error") %>
            </div>
        <% } %>
        
        <div class="message info">
            <strong>Note:</strong> Only DRAFT tenders can be edited. Once published, the tender is locked.
        </div>
        
        <%
            Tender tender = (Tender) request.getAttribute("tender");
            if (tender != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
                String formattedDate = sdf.format(tender.getClosingDatetime());
        %>
        
        <form action="${pageContext.request.contextPath}/officer/tender" method="post" enctype="multipart/form-data">
            <input type="hidden" name="action" value="edit">
            <input type="hidden" name="tenderId" value="<%= tender.getTenderId() %>">
            
            <div class="form-group">
                <label>Reference Number</label>
                <input type="text" value="<%= tender.getReferenceNumber() %>" class="readonly-field" readonly disabled>
                <small>Reference number cannot be changed</small>
            </div>
            
            <div class="form-group">
                <label>Title *</label>
                <input type="text" name="title" value="<%= tender.getTitle() %>" required>
            </div>
            
            <div class="form-group">
                <label>Category *</label>
                <select name="category" required>
                    <option value="">Select Category</option>
                    <option value="Construction" <%= tender.getCategory().equals("Construction") ? "selected" : "" %>>Construction</option>
                    <option value="Roads" <%= tender.getCategory().equals("Roads") ? "selected" : "" %>>Roads</option>
                    <option value="Electrical" <%= tender.getCategory().equals("Electrical") ? "selected" : "" %>>Electrical</option>
                    <option value="Plumbing" <%= tender.getCategory().equals("Plumbing") ? "selected" : "" %>>Plumbing</option>
                    <option value="General Services" <%= tender.getCategory().equals("General Services") ? "selected" : "" %>>General Services</option>
                </select>
            </div>
            
            <div class="form-group">
                <label>Description *</label>
                <textarea name="description" required><%= tender.getDescription() %></textarea>
            </div>
            
            <div class="form-group">
                <label>Estimated Value (Maloti) *</label>
                <input type="number" name="estimatedValue" step="0.01" value="<%= tender.getEstimatedValue() %>" required>
            </div>
            
            <div class="form-group">
                <label>Closing Date & Time *</label>
                <input type="datetime-local" name="closingDatetime" value="<%= formattedDate %>" required>
            </div>
            
            <div class="form-group">
                <label>Current Tender Notice</label>
                <p><a href="${pageContext.request.contextPath}/download?tenderId=<%= tender.getTenderId() %>" target="_blank">Download Current Document</a></p>
            </div>
            
            <div class="form-group">
                <label>Replace Tender Notice (PDF, max 5MB) - Optional</label>
                <input type="file" name="noticeFile" accept=".pdf">
                <small>Leave empty to keep existing document</small>
            </div>
            
            <div class="form-group">
                <button type="submit" class="btn-submit">Update Tender</button>
                <a href="${pageContext.request.contextPath}/officer/tender" class="btn-cancel">Cancel</a>
            </div>
        </form>
        
        <% } else { %>
            <div class="message error">Tender not found</div>
        <% } %>
    </div>
</body>
</html>

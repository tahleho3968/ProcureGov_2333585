<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<jsp:include page="/views/fragments/header.jsp" />
<style>
    .form-container {
        max-width: 800px;
        margin: 80px auto 20px;
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
</style>
</head>
<body>
<div class="form-container">
    <h2>Create New Tender</h2>
    
    <% if(request.getAttribute("error") != null) { %>
        <div class="message error">
            <%= request.getAttribute("error") %>
        </div>
    <% } %>
    
    <form action="${pageContext.request.contextPath}/officer/tender" method="post" enctype="multipart/form-data">
        <input type="hidden" name="action" value="create">
        
        <div class="form-group">
            <label>Title *</label>
            <input type="text" name="title" required>
        </div>
        
        <div class="form-group">
            <label>Category *</label>
            <select name="category" required>
                <option value="">Select Category</option>
                <option value="Construction">Construction</option>
                <option value="Roads">Roads</option>
                <option value="Electrical">Electrical</option>
                <option value="Plumbing">Plumbing</option>
                <option value="General Services">General Services</option>
            </select>
        </div>
        
        <div class="form-group">
            <label>Description *</label>
            <textarea name="description" required></textarea>
        </div>
        
        <div class="form-group">
            <label>Estimated Value (Maloti) *</label>
            <input type="number" name="estimatedValue" step="0.01" required>
        </div>
        
        <div class="form-group">
            <label>Closing Date & Time *</label>
            <input type="datetime-local" name="closingDatetime" id="closingDatetime" required>
        </div>
        
        <div class="form-group">
            <label>Tender Notice Document (PDF, max 5MB) *</label>
            <input type="file" name="noticeFile" accept=".pdf" required>
        </div>
        
        <div class="form-group">
            <button type="submit" class="btn-submit">Create Tender</button>
            <a href="${pageContext.request.contextPath}/officer/tender" class="btn-cancel">Cancel</a>
        </div>
    </form>
</div>

<script>
    // Set min date to current date/time and set default to tomorrow
    const datetimeInput = document.getElementById('closingDatetime');
    if (datetimeInput) {
        const now = new Date();
        const year = now.getFullYear();
        const month = String(now.getMonth() + 1).padStart(2, '0');
        const day = String(now.getDate()).padStart(2, '0');
        const hours = String(now.getHours()).padStart(2, '0');
        const minutes = String(now.getMinutes()).padStart(2, '0');
        const minDateTime = `${year}-${month}-${day}T${hours}:${minutes}`;
        datetimeInput.min = minDateTime;
        
        // Set default value to tomorrow at 12:00 PM
        const tomorrow = new Date();
        tomorrow.setDate(tomorrow.getDate() + 1);
        tomorrow.setHours(12, 0, 0);
        const defaultYear = tomorrow.getFullYear();
        const defaultMonth = String(tomorrow.getMonth() + 1).padStart(2, '0');
        const defaultDay = String(tomorrow.getDate()).padStart(2, '0');
        const defaultHours = String(tomorrow.getHours()).padStart(2, '0');
        const defaultMinutes = String(tomorrow.getMinutes()).padStart(2, '0');
        datetimeInput.value = `${defaultYear}-${defaultMonth}-${defaultDay}T${defaultHours}:${defaultMinutes}`;
        
        // Add validation on change
        datetimeInput.addEventListener('change', function() {
            if (this.value) {
                const selectedDate = new Date(this.value);
                if (selectedDate < now) {
                    alert('Closing date cannot be in the past! Please select a future date/time.');
                    this.value = '';
                }
            }
        });
    }
</script>
</body>
</html>

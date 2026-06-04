<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .live-clock {
            position: fixed;
            top: 10px;
            right: 20px;
            background: #1a472a;
            color: white;
            padding: 8px 15px;
            border-radius: 5px;
            font-size: 14px;
            font-weight: bold;
            z-index: 1000;
            box-shadow: 0 2px 5px rgba(0,0,0,0.2);
        }
        .live-clock span {
            font-family: monospace;
            font-size: 16px;
        }
    </style>
    <script>
        function updateClock() {
            var now = new Date();
            var year = now.getFullYear();
            var month = String(now.getMonth() + 1).padStart(2, '0');
            var day = String(now.getDate()).padStart(2, '0');
            var hours = String(now.getHours()).padStart(2, '0');
            var minutes = String(now.getMinutes()).padStart(2, '0');
            var seconds = String(now.getSeconds()).padStart(2, '0');
            var dateTime = year + '-' + month + '-' + day + ' ' + hours + ':' + minutes + ':' + seconds;
            document.getElementById('clock').innerHTML = dateTime;
        }
        setInterval(updateClock, 1000);
        window.onload = updateClock;
    </script>
</head>
<body>
    <div class="live-clock">
        🕐 System Time: <span id="clock">Loading...</span>
    </div>

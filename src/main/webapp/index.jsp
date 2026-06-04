<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ProcureGov | Tender Management System</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
    <div class="landing-container">
        <!-- Header -->
        <header class="landing-header">
            <div class="logo-container">
                <h1>ProcureGov</h1>
                <p class="tagline">Kingdom of Lesotho - Ministry of Public Works</p>
            </div>
        </header>

        <!-- Hero Section -->
        <section class="hero">
            <div class="hero-content">
                <h2>Digital Tender Management System</h2>
                <p>Transparent, Efficient, Accountable Government Procurement</p>
                <div class="hero-buttons">
                    <a href="views/auth/login.jsp" class="btn btn-primary">Login to Portal</a>
                    <a href="views/auth/register.jsp" class="btn btn-secondary">Register as Supplier</a>
                </div>
            </div>
        </section>

        <!-- Features Section -->
        <section class="features">
            <h3>System Features</h3>
            <div class="feature-grid">
                <div class="feature-card">
                    <div class="feature-icon">📄</div>
                    <h4>Digital Tenders</h4>
                    <p>Publish and manage tender notices electronically</p>
                </div>
                <div class="feature-card">
                    <div class="feature-icon">🔒</div>
                    <h4>Secure Bidding</h4>
                    <p>Submit sealed bids with document attachments</p>
                </div>
                <div class="feature-card">
                    <div class="feature-icon">⚖️</div>
                    <h4>Fair Evaluation</h4>
                    <p>Weighted scoring system for transparent evaluation</p>
                </div>
                <div class="feature-card">
                    <div class="feature-icon">📊</div>
                    <h4>Real-time Tracking</h4>
                    <p>Track tender lifecycle from draft to award</p>
                </div>
            </div>
        </section>

        <!-- Tender Lifecycle Section -->
        <section class="lifecycle">
            <h3>Tender Lifecycle</h3>
            <div class="lifecycle-steps">
                <div class="step">Draft</div>
                <div class="step-arrow">→</div>
                <div class="step">Open</div>
                <div class="step-arrow">→</div>
                <div class="step">Closed</div>
                <div class="step-arrow">→</div>
                <div class="step">Under Evaluation</div>
                <div class="step-arrow">→</div>
                <div class="step">Evaluated</div>
                <div class="step-arrow">→</div>
                <div class="step">Awarded</div>
            </div>
        </section>

        <!-- Footer -->
        <footer class="landing-footer">
            <p>&copy; 2026 Ministry of Public Works, Kingdom of Lesotho</p>
            <p>ProcureGov - Government Tender Management System</p>
        </footer>
    </div>
</body>
</html>

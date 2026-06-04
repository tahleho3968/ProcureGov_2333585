package com.procuregov.service;

import com.procuregov.model.Bid;
import com.procuregov.model.Tender;
import com.procuregov.model.User;
import com.procuregov.dao.UserDAO;
import com.procuregov.dao.UserDAOImpl;

import java.util.Properties;
import java.util.List;
import javax.mail.*;
import javax.mail.internet.*;

public class EmailService {
    
    // Email configuration - Update with your actual app password
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String FROM_EMAIL = "globex3968@gmail.com";
    private static final String FROM_PASSWORD = "orxh owje xrju vpvg"; 
    
    public static void sendAwardNotification(Tender tender, Bid winningBid, User winningSupplier, 
                                              List<Bid> allBids, String justification) {
        
        System.out.println("=== Starting Email Notifications ===");
        System.out.println("Tender: " + tender.getReferenceNumber());
        System.out.println("Winner: " + winningSupplier.getEmail());
        
        // Send email to winning supplier
        sendWinningEmail(tender, winningBid, winningSupplier, justification);
        
        // Send emails to losing suppliers
        UserDAO userDAO = new UserDAOImpl();
        for (Bid bid : allBids) {
            if (bid.getBidId() != winningBid.getBidId()) {
                User supplier = userDAO.findById(bid.getSupplierId());
                if (supplier != null) {
                    sendLosingEmail(tender, bid, winningSupplier.getFullName(), supplier.getEmail());
                }
            }
        }
        
        System.out.println("=== Email Notifications Complete ===");
    }
    
    private static void sendWinningEmail(Tender tender, Bid bid, User supplier, String justification) {
        String subject = "CONGRATULATIONS! You have WON the tender: " + tender.getReferenceNumber();
        
        StringBuilder body = new StringBuilder();
        body.append("<html><body style='font-family: Arial, sans-serif;'>");
        body.append("<div style='background-color: #1a472a; padding: 20px; color: white; text-align: center;'>");
        body.append("<h1>🏆 PROCUREGOV - AWARD NOTIFICATION 🏆</h1>");
        body.append("</div>");
        body.append("<div style='padding: 20px;'>");
        body.append("<h2 style='color: #1a472a;'>Congratulations ").append(supplier.getFullName()).append("!</h2>");
        body.append("<p>We are pleased to inform you that your bid for the following tender has been <strong>SUCCESSFUL</strong>:</p>");
        body.append("<div style='background-color: #f0f0f0; padding: 15px; border-radius: 5px; margin: 15px 0;'>");
        body.append("<p><strong>Tender Reference:</strong> ").append(tender.getReferenceNumber()).append("</p>");
        body.append("<p><strong>Tender Title:</strong> ").append(tender.getTitle()).append("</p>");
        body.append("<p><strong>Your Bid Amount:</strong> M ").append(String.format("%,.2f", bid.getBidAmount())).append("</p>");
        body.append("<p><strong>Proposed Timeline:</strong> ").append(bid.getDeliveryTimeline()).append(" days</p>");
        body.append("</div>");
        body.append("<h3>Award Justification:</h3>");
        body.append("<p style='background-color: #e8f5e9; padding: 10px; border-left: 4px solid #1a472a;'>").append(justification).append("</p>");
        body.append("<p><strong>Award Date:</strong> ").append(new java.util.Date()).append("</p>");
        body.append("<h3>Next Steps:</h3>");
        body.append("<ul>");
        body.append("<li>You will receive a formal contract within 7 working days</li>");
        body.append("<li>Please prepare your project implementation plan</li>");
        body.append("<li>Contact the Procurement Officer to schedule a kick-off meeting</li>");
        body.append("</ul>");
        body.append("<hr>");
        body.append("<p style='font-size: 12px; color: #666;'>This is an automated message from ProcureGov - Ministry of Public Works, Kingdom of Lesotho</p>");
        body.append("<p style='font-size: 12px; color: #666;'>You can view full details by logging into your ProcureGov account.</p>");
        body.append("</div>");
        body.append("</body></html>");
        
        sendEmail(supplier.getEmail(), subject, body.toString());
    }
    
    private static void sendLosingEmail(Tender tender, Bid bid, String winnerName, String supplierEmail) {
        String subject = "UPDATE on Tender: " + tender.getReferenceNumber();
        
        StringBuilder body = new StringBuilder();
        body.append("<html><body style='font-family: Arial, sans-serif;'>");
        body.append("<div style='background-color: #1a472a; padding: 20px; color: white; text-align: center;'>");
        body.append("<h1>📋 PROCUREGOV - TENDER UPDATE</h1>");
        body.append("</div>");
        body.append("<div style='padding: 20px;'>");
        body.append("<h2 style='color: #1a472a;'>Dear Supplier,</h2>");
        body.append("<p>Thank you for your participation in the tender process. We appreciate the time and effort you invested in submitting your bid.</p>");
        body.append("<p>We regret to inform you that your bid for the following tender was <strong>NOT SUCCESSFUL</strong>:</p>");
        body.append("<div style='background-color: #f0f0f0; padding: 15px; border-radius: 5px; margin: 15px 0;'>");
        body.append("<p><strong>Tender Reference:</strong> ").append(tender.getReferenceNumber()).append("</p>");
        body.append("<p><strong>Tender Title:</strong> ").append(tender.getTitle()).append("</p>");
        body.append("<p><strong>Your Bid Amount:</strong> M ").append(String.format("%,.2f", bid.getBidAmount())).append("</p>");
        body.append("</div>");
        body.append("<p>The winning supplier is: <strong>").append(winnerName).append("</strong></p>");
        body.append("<p>We encourage you to continue participating in future tender opportunities. Your interest in working with the Ministry of Public Works is greatly appreciated.</p>");
        body.append("<p>You can view the full award notice by logging into your ProcureGov account.</p>");
        body.append("<hr>");
        body.append("<p style='font-size: 12px; color: #666;'>This is an automated message from ProcureGov - Ministry of Public Works, Kingdom of Lesotho</p>");
        body.append("<p style='font-size: 12px; color: #666;'>Thank you for your participation.</p>");
        body.append("</div>");
        body.append("</body></html>");
        
        sendEmail(supplierEmail, subject, body.toString());
    }
    
    private static void sendEmail(String to, String subject, String htmlBody) {
        // Configure SMTP properties
        Properties props = new Properties();
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        
        // Create session with authentication
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, FROM_PASSWORD);
            }
        });
        
        // Enable debug for troubleshooting (remove in production)
        // session.setDebug(true);
        
        try {
            // Create email message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL, "ProcureGov - Ministry of Public Works"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setContent(htmlBody, "text/html; charset=utf-8");
            
            // Send email
            Transport.send(message);
            System.out.println("✓ Email sent successfully to: " + to);
            
        } catch (Exception e) {
            System.err.println("✗ Failed to send email to: " + to);
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Test method to verify email configuration
    public static void testEmailConfiguration() {
        System.out.println("Testing email configuration...");
        System.out.println("SMTP Host: " + SMTP_HOST);
        System.out.println("SMTP Port: " + SMTP_PORT);
        System.out.println("From Email: " + FROM_EMAIL);
        
        Properties props = new Properties();
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        
        try {
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(FROM_EMAIL, FROM_PASSWORD);
                }
            });
            
            // Try to connect to SMTP server
            Transport transport = session.getTransport("smtp");
            transport.connect(SMTP_HOST, Integer.parseInt(SMTP_PORT), FROM_EMAIL, FROM_PASSWORD);
            transport.close();
            
            System.out.println("✓ Email configuration is CORRECT!");
            System.out.println("You can send emails successfully.");
            
        } catch (Exception e) {
            System.err.println("✗ Email configuration FAILED!");
            System.err.println("Error: " + e.getMessage());
            System.err.println("\nPlease check:");
            System.err.println("1. Your app password is correct (use the 16-char code from Google)");
            System.err.println("2. 2-Factor Authentication is enabled on your Google account");
            System.err.println("3. The app password was generated for 'Mail'");
        }
    }
}

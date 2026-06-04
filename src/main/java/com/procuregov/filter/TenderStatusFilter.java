package com.procuregov.filter;

import com.procuregov.service.TenderStatusService;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

@WebFilter("/*")
public class TenderStatusFilter implements Filter {
    
    private TenderStatusService statusService;
    private long lastCheckTime = 0;
    private static final long CHECK_INTERVAL = 60000; // Check every minute
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        statusService = new TenderStatusService();
        System.out.println("TenderStatusFilter initialized - Auto-close check active");
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        try {
            long now = System.currentTimeMillis();
            if (now - lastCheckTime > CHECK_INTERVAL) {
                statusService.checkAndCloseExpiredTenders();
                lastCheckTime = now;
            }
        } catch (Exception e) {
            // Log error but don't break the request
            System.err.println("Error in TenderStatusFilter: " + e.getMessage());
            e.printStackTrace();
        }
        
        chain.doFilter(request, response);
    }
    
    @Override
    public void destroy() {
        // Cleanup
    }
}

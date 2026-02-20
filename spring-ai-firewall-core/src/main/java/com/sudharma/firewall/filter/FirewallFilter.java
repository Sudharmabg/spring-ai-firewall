package com.sudharma.firewall.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sudharma.firewall.audit.FirewallAuditLogger;
import com.sudharma.firewall.audit.FirewallEvent;
import com.sudharma.firewall.core.*;
import com.sudharma.firewall.rules.FirewallRule;
import com.sudharma.firewall.rules.RuleEngine;
import com.sudharma.firewall.scoring.RiskScoringEngine;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class FirewallFilter extends OncePerRequestFilter {
    
    private final RateLimiter rateLimiter;
    private final RiskScoringEngine scoringEngine;
    private final RuleEngine ruleEngine;
    private final FirewallAuditLogger auditLogger;
    private final ObjectMapper objectMapper;
    private final FirewallProperties properties;
    
    public FirewallFilter(RateLimiter rateLimiter, 
                         RiskScoringEngine scoringEngine,
                         RuleEngine ruleEngine,
                         FirewallAuditLogger auditLogger,
                         FirewallProperties properties) {
        this.rateLimiter = rateLimiter;
        this.scoringEngine = scoringEngine;
        this.ruleEngine = ruleEngine;
        this.auditLogger = auditLogger;
        this.properties = properties;
        this.objectMapper = new ObjectMapper();
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                   HttpServletResponse response, 
                                   FilterChain filterChain) throws ServletException, IOException {
        
        String ip = getClientIp(request);
        String path = request.getRequestURI();
        String apiKey = request.getHeader(properties.getApiKeyHeader());
        
        // Priority: API key > IP
        String rateLimitKey = apiKey != null ? "firewall:rate:apikey:" + apiKey : "firewall:rate:ip:" + ip;
        
        // Find matching rule
        FirewallRule rule = ruleEngine.findMatchingRule(path);
        int requestsPerMinute = rule != null && rule.getRequestsPerMinute() != null 
            ? rule.getRequestsPerMinute() 
            : properties.getRateLimit().getRequestsPerMinute();
        
        // Rate limiting
        if (!rateLimiter.isAllowed(rateLimitKey, requestsPerMinute)) {
            blockRequest(response, ip, path, 100, "Rate limit exceeded", 429);
            return;
        }
        
        // Build context
        long requestCount = rateLimiter.getRequestCount(rateLimitKey);
        double rateLimitUsage = (double) requestCount / requestsPerMinute;
        
        Map<String, String> headers = extractHeaders(request);
        FirewallRequestContext context = new FirewallRequestContext(
            ip, path, request.getMethod(), headers, apiKey, requestCount
        );
        
        // Risk scoring
        FirewallDecision decision = scoringEngine.evaluate(context, rule, rateLimitUsage);
        
        // Audit
        FirewallEvent event = new FirewallEvent(ip, path, decision.riskScore(), 
            Instant.now(), decision.reason(), decision.blocked());
        auditLogger.log(event);
        
        // Block if needed
        if (decision.blocked()) {
            blockRequest(response, ip, path, decision.riskScore(), decision.reason(), 403);
            return;
        }
        
        filterChain.doFilter(request, response);
    }
    
    private void blockRequest(HttpServletResponse response, String ip, String path, 
                             int score, String reason, int status) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        
        Map<String, Object> body = new HashMap<>();
        body.put("blocked", true);
        body.put("reason", reason);
        body.put("riskScore", score);
        
        response.getWriter().write(objectMapper.writeValueAsString(body));
        
        auditLogger.log(new FirewallEvent(ip, path, score, Instant.now(), reason, true));
    }
    
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        } else {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
    
    private Map<String, String> extractHeaders(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();
        var headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            headers.put(name.toLowerCase(), request.getHeader(name));
        }
        return headers;
    }
}

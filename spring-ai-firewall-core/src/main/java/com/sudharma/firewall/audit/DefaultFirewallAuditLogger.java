package com.sudharma.firewall.audit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DefaultFirewallAuditLogger implements FirewallAuditLogger {
    
    private static final Logger log = LoggerFactory.getLogger(DefaultFirewallAuditLogger.class);
    
    @Override
    public void log(FirewallEvent event) {
        if (event.blocked()) {
            log.warn("FIREWALL BLOCKED: ip={}, path={}, score={}, reason={}", 
                event.ip(), event.path(), event.riskScore(), event.reason());
        } else {
            log.info("FIREWALL ALLOWED: ip={}, path={}, score={}", 
                event.ip(), event.path(), event.riskScore());
        }
    }
}

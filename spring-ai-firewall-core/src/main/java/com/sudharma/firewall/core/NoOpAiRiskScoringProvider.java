package com.sudharma.firewall.core;

import org.springframework.stereotype.Component;

@Component
public class NoOpAiRiskScoringProvider implements AiRiskScoringProvider {
    
    @Override
    public int scoreRequest(FirewallRequestContext context) {
        return 0;
    }
}

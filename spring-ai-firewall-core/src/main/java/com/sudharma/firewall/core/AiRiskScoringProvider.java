package com.sudharma.firewall.core;

public interface AiRiskScoringProvider {
    int scoreRequest(FirewallRequestContext context);
}

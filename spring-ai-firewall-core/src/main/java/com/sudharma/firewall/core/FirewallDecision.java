package com.sudharma.firewall.core;

public record FirewallDecision(
    boolean blocked,
    int riskScore,
    String reason
) {}

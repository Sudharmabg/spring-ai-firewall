package com.sudharma.firewall.audit;

import java.time.Instant;

public record FirewallEvent(
    String ip,
    String path,
    int riskScore,
    Instant timestamp,
    String reason,
    boolean blocked
) {}

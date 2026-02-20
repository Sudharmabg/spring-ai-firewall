package com.sudharma.firewall.core;

import java.util.Map;

public record FirewallRequestContext(
    String ip,
    String path,
    String method,
    Map<String, String> headers,
    String apiKey,
    long requestCount
) {}

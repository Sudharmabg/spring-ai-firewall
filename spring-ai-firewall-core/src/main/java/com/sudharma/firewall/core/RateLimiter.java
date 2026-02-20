package com.sudharma.firewall.core;

public interface RateLimiter {
    boolean isAllowed(String key, int requestsPerMinute);
    long getRequestCount(String key);
}

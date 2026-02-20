package com.sudharma.firewall.filter;

import com.sudharma.firewall.rules.FirewallRule;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "firewall")
public class FirewallProperties {
    
    private boolean enabled = true;
    private String apiKeyHeader = "X-API-Key";
    private RateLimitConfig rateLimit = new RateLimitConfig();
    private List<FirewallRule> rules = new ArrayList<>();
    private AiConfig ai = new AiConfig();
    
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    
    public String getApiKeyHeader() { return apiKeyHeader; }
    public void setApiKeyHeader(String apiKeyHeader) { this.apiKeyHeader = apiKeyHeader; }
    
    public RateLimitConfig getRateLimit() { return rateLimit; }
    public void setRateLimit(RateLimitConfig rateLimit) { this.rateLimit = rateLimit; }
    
    public List<FirewallRule> getRules() { return rules; }
    public void setRules(List<FirewallRule> rules) { this.rules = rules; }
    
    public AiConfig getAi() { return ai; }
    public void setAi(AiConfig ai) { this.ai = ai; }
    
    public static class RateLimitConfig {
        private boolean enabled = true;
        private int requestsPerMinute = 100;
        
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        
        public int getRequestsPerMinute() { return requestsPerMinute; }
        public void setRequestsPerMinute(int requestsPerMinute) { 
            this.requestsPerMinute = requestsPerMinute; 
        }
    }
    
    public static class AiConfig {
        private boolean enabled = false;
        private String provider = "openai";
        private String apiKey;
        private String model = "gpt-4o-mini";
        private int timeoutSeconds = 2;
        private int cacheTtlMinutes = 3;
        
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        
        public String getProvider() { return provider; }
        public void setProvider(String provider) { this.provider = provider; }
        
        public String getApiKey() { return apiKey; }
        public void setApiKey(String apiKey) { this.apiKey = apiKey; }
        
        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }
        
        public int getTimeoutSeconds() { return timeoutSeconds; }
        public void setTimeoutSeconds(int timeoutSeconds) { this.timeoutSeconds = timeoutSeconds; }
        
        public int getCacheTtlMinutes() { return cacheTtlMinutes; }
        public void setCacheTtlMinutes(int cacheTtlMinutes) { this.cacheTtlMinutes = cacheTtlMinutes; }
    }
}

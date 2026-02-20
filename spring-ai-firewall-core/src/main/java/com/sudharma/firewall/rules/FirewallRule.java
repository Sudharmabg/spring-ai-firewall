package com.sudharma.firewall.rules;

public class FirewallRule {
    private String path;
    private Integer requestsPerMinute;
    private RuleAction action;
    private boolean sensitive;
    
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
    
    public Integer getRequestsPerMinute() { return requestsPerMinute; }
    public void setRequestsPerMinute(Integer requestsPerMinute) { 
        this.requestsPerMinute = requestsPerMinute; 
    }
    
    public RuleAction getAction() { return action; }
    public void setAction(RuleAction action) { this.action = action; }
    
    public boolean isSensitive() { return sensitive; }
    public void setSensitive(boolean sensitive) { this.sensitive = sensitive; }
    
    public enum RuleAction {
        ALLOW, BLOCK
    }
}

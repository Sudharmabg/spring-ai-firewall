package com.sudharma.firewall.rules;

import org.springframework.util.AntPathMatcher;
import java.util.List;

public class RuleEngine {
    
    private final List<FirewallRule> rules;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    
    public RuleEngine(List<FirewallRule> rules) {
        this.rules = rules != null ? rules : List.of();
    }
    
    public FirewallRule findMatchingRule(String path) {
        for (FirewallRule rule : rules) {
            if (pathMatcher.match(rule.getPath(), path)) {
                return rule;
            }
        }
        return null;
    }
}

package com.sudharma.firewall.autoconfig;

import com.sudharma.firewall.audit.DefaultFirewallAuditLogger;
import com.sudharma.firewall.audit.FirewallAuditLogger;
import com.sudharma.firewall.core.NoOpAiRiskScoringProvider;
import com.sudharma.firewall.detector.BotDetector;
import com.sudharma.firewall.filter.FirewallFilter;
import com.sudharma.firewall.filter.FirewallProperties;
import com.sudharma.firewall.redis.RedisRateLimiter;
import com.sudharma.firewall.rules.RuleEngine;
import com.sudharma.firewall.scoring.RiskScoringEngine;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@AutoConfiguration
@ConditionalOnProperty(prefix = "firewall", name = "enabled", matchIfMissing = true)
@EnableConfigurationProperties(FirewallProperties.class)
@ComponentScan(basePackages = {
    "com.sudharma.firewall.core",
    "com.sudharma.firewall.filter",
    "com.sudharma.firewall.detector",
    "com.sudharma.firewall.scoring",
    "com.sudharma.firewall.audit",
    "com.sudharma.firewall.redis"
})
public class FirewallAutoConfiguration {
    
    @Bean
    @ConditionalOnMissingBean
    public RuleEngine ruleEngine(FirewallProperties properties) {
        return new RuleEngine(properties.getRules());
    }
    
    @Bean
    @ConditionalOnMissingBean
    public FirewallAuditLogger firewallAuditLogger() {
        return new DefaultFirewallAuditLogger();
    }
}

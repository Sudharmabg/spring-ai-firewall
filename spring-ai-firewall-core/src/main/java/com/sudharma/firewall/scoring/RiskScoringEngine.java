package com.sudharma.firewall.scoring;

import com.sudharma.firewall.core.AiRiskScoringProvider;
import com.sudharma.firewall.core.FirewallDecision;
import com.sudharma.firewall.core.FirewallRequestContext;
import com.sudharma.firewall.detector.BotDetector;
import com.sudharma.firewall.rules.FirewallRule;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RiskScoringEngine {
    
    private final BotDetector botDetector;
    private final AiRiskScoringProvider aiProvider;
    
    public RiskScoringEngine(BotDetector botDetector, AiRiskScoringProvider aiProvider) {
        this.botDetector = botDetector;
        this.aiProvider = aiProvider;
    }
    
    public FirewallDecision evaluate(FirewallRequestContext context, FirewallRule rule, double rateLimitUsage) {
        int score = 0;
        StringBuilder reason = new StringBuilder();
        
        // Bot detection
        int botScore = botDetector.detectBotScore(
            context.headers().get("user-agent"),
            context.headers().get("accept")
        );
        score += botScore;
        if (botScore > 0) {
            reason.append("Bot signals detected. ");
        }
        
        // Rate limit threshold (80% = escalation)
        if (rateLimitUsage >= 0.8) {
            score += 50;
            reason.append("High request rate. ");
        }
        
        // Sensitive path
        if (rule != null && rule.isSensitive()) {
            score += 30;
            reason.append("Sensitive endpoint. ");
        }
        
        // AI scoring
        int aiScore = aiProvider.scoreRequest(context);
        score += aiScore;
        if (aiScore > 0) {
            reason.append("AI anomaly detected. ");
        }
        
        // Decision
        boolean blocked = score > 70;
        if (score >= 30 && score <= 70) {
            reason.append("Soft challenge. ");
        }
        
        return new FirewallDecision(blocked, score, reason.toString().trim());
    }
}

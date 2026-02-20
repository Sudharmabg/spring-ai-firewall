package com.sudharma.firewall.detector;

import org.springframework.stereotype.Component;

@Component
public class BotDetector {
    
    private static final String[] HEADLESS_MARKERS = {
        "HeadlessChrome", "PhantomJS", "Selenium", "Puppeteer", 
        "WebDriver", "Playwright", "Nightmare"
    };
    
    public int detectBotScore(String userAgent, String accept) {
        int score = 0;
        
        if (userAgent == null || userAgent.isBlank()) {
            score += 20;
        } else {
            for (String marker : HEADLESS_MARKERS) {
                if (userAgent.contains(marker)) {
                    score += 40;
                    break;
                }
            }
        }
        
        if (accept == null || accept.isBlank() || accept.equals("*/*")) {
            score += 10;
        }
        
        return score;
    }
}

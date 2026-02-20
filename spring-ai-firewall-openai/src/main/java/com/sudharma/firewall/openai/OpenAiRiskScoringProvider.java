package com.sudharma.firewall.openai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sudharma.firewall.core.AiRiskScoringProvider;
import com.sudharma.firewall.core.FirewallRequestContext;
import com.sudharma.firewall.filter.FirewallProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class OpenAiRiskScoringProvider implements AiRiskScoringProvider {
    
    private static final Logger log = LoggerFactory.getLogger(OpenAiRiskScoringProvider.class);
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
    
    private final FirewallProperties properties;
    private final RestTemplate restTemplate;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    
    public OpenAiRiskScoringProvider(FirewallProperties properties, 
                                    StringRedisTemplate redisTemplate) {
        this.properties = properties;
        this.redisTemplate = redisTemplate;
        this.objectMapper = new ObjectMapper();
        this.restTemplate = new RestTemplate();
        this.restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().set("Authorization", "Bearer " + properties.getAi().getApiKey());
            return execution.execute(request, body);
        });
    }
    
    @Override
    public int scoreRequest(FirewallRequestContext context) {
        String cacheKey = "firewall:ai:score:" + context.ip() + ":" + context.path();
        
        // Check cache
        String cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return Integer.parseInt(cached);
        }
        
        try {
            int score = callOpenAi(context);
            
            // Cache result
            redisTemplate.opsForValue().set(cacheKey, String.valueOf(score), 
                Duration.ofMinutes(properties.getAi().getCacheTtlMinutes()));
            
            return score;
        } catch (Exception e) {
            log.warn("OpenAI call failed, failing open: {}", e.getMessage());
            return 0; // Fail open
        }
    }
    
    private int callOpenAi(FirewallRequestContext context) {
        String prompt = buildPrompt(context);
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", properties.getAi().getModel());
        requestBody.put("messages", new Object[]{
            Map.of("role", "system", "content", "You are an API security firewall. Return only a risk score 0-100."),
            Map.of("role", "user", "content", prompt)
        });
        requestBody.put("max_tokens", 10);
        requestBody.put("temperature", 0);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        
        ResponseEntity<String> response = restTemplate.exchange(
            OPENAI_API_URL, HttpMethod.POST, entity, String.class);
        
        return parseScore(response.getBody());
    }
    
    private String buildPrompt(FirewallRequestContext context) {
        return String.format(
            "Classify this API request as HUMAN or BOT. Return risk score 0-100.\n" +
            "IP: %s\nPath: %s\nMethod: %s\nUser-Agent: %s\nRequest count: %d",
            context.ip(), context.path(), context.method(), 
            context.headers().get("user-agent"), context.requestCount()
        );
    }
    
    private int parseScore(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            String content = root.path("choices").get(0).path("message").path("content").asText();
            
            // Extract number from response
            String numStr = content.replaceAll("[^0-9]", "");
            if (!numStr.isEmpty()) {
                int score = Integer.parseInt(numStr);
                return Math.min(100, Math.max(0, score));
            }
        } catch (Exception e) {
            log.warn("Failed to parse OpenAI response: {}", e.getMessage());
        }
        return 0;
    }
}

package com.sudharma.firewall.autoconfig;

import com.sudharma.firewall.core.AiRiskScoringProvider;
import com.sudharma.firewall.filter.FirewallProperties;
import com.sudharma.firewall.openai.OpenAiRiskScoringProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.StringRedisTemplate;

@AutoConfiguration
@ConditionalOnProperty(prefix = "firewall.ai", name = "enabled", havingValue = "true")
public class OpenAiFirewallAutoConfiguration {
    
    @Bean
    @Primary
    @ConditionalOnMissingBean(name = "openAiRiskScoringProvider")
    public AiRiskScoringProvider openAiRiskScoringProvider(
            FirewallProperties properties,
            StringRedisTemplate redisTemplate) {
        return new OpenAiRiskScoringProvider(properties, redisTemplate);
    }
}

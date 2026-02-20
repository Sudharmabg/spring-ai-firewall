Project: spring-ai-firewall (Spring Boot Starter + Redis + OpenAI Plugin + Maven Central)
You are a senior Java backend engineer and an experienced open-source maintainer.
Generate a complete GitHub-ready project called:
spring-ai-firewall
This project must be a production-style Spring Boot Starter Library that provides plug-and-play AI-inspired API firewall protection.
It should work like VelocityGate, but enhanced with:
Distributed Redis rate limiting
Risk scoring decision engine
Bot/headless request detection heuristics
YAML-configurable rules
Audit logging hooks
Optional OpenAI-powered anomaly scoring plugin
Example demo consumer app
Maven Central publishing support
The output must be a full multi-module Maven repository with correct Spring Boot 3 conventions.
GOAL
Build an open-source library that developers can integrate by adding one dependency:
<dependency>
  <groupId>io.github.sudharma</groupId>
  <artifactId>spring-ai-firewall-starter</artifactId>
  <version>1.0.0</version>
</dependency>

Once included, the firewall must automatically activate in the request chain.

REQUIRED FEATURES
1. Firewall Filter Layer (Highest Precedence)
Implement a servlet filter (OncePerRequestFilter preferred)
Runs before Spring Security
Blocks suspicious/bot traffic early
Returns HTTP 429 or 403 with JSON response
Example response:
{
  "blocked": true,
  "reason": "High risk bot traffic detected",
  "riskScore": 85
}
2. Distributed Rate Limiting using Redis
Must support:
Redis-backed token bucket or sliding window
Works across multiple app instances
Uses Spring Data Redis + Lettuce client
Configurable via YAML:
firewall:
  rate-limit:
    enabled: true
    requests-per-minute: 100

Redis keys should be based on:
Client IP
Optional API key header
Example key:
firewall:rate:192.168.1.10
3. Bot Detection Engine
Implement lightweight heuristics:
Signals
Missing User-Agent
Known headless browser strings
Suspicious Accept headers
Selenium/Puppeteer markers
Example:
if(userAgent.contains("HeadlessChrome")) score += 40;
4. Risk Scoring Engine (Core)
Compute a base risk score:
Signal
Score
Too many requests
+50
Missing UA
+20
Headless marker
+40
Path sensitive endpoint
+30

Decision thresholds:
Score < 30 → Allow
Score 30–70 → Soft Challenge (log only)
Score > 70 → Block
Return decision record:
public record FirewallDecision(
    boolean blocked,
    int riskScore,
    String reason
) {}
5. YAML Rule Engine (Path-Based Policies)
Support per-path rules:
firewall:
  rules:
    - path: "/api/auth/**"
      requests-per-minute: 10
      action: BLOCK

    - path: "/api/public/**"
      requests-per-minute: 200
      action: ALLOW

Use AntPathMatcher.
6. Audit Logging Hook
Every blocked request should generate an event:
IP
Path
Risk score
Timestamp
Reason
Provide interface:
public interface FirewallAuditLogger {
    void log(FirewallEvent event);
}

Default: console logger.
OPENAI INTEGRATION MODULE
OpenAI support must be implemented as a separate optional module:
spring-ai-firewall-openai/

This ensures the library works fully without AI keys.
7. OpenAI Risk Scoring Provider (Plugin)
Design:
public interface AiRiskScoringProvider {
    int scoreRequest(FirewallRequestContext context);
}

Default implementation:
Returns 0
No AI dependency
OpenAI implementation:
Calls OpenAI API only if enabled
Config:
firewall:
  ai:
    enabled: true
    provider: openai
    api-key: ${OPENAI_API_KEY}
    model: gpt-4o-mini

8. OpenAI Request Analysis Prompt (Lightweight)
Send metadata only:
IP
Path
Headers summary
Request frequency
Example prompt:
You are an API security firewall.
Classify this request as HUMAN or BOT and return risk score 0-100.

OpenAI response should map into:
Additional risk score boost
Example:
riskScore += openAiScore;

9. Safe Defaults
Important:
AI module must be disabled by default
No OpenAI calls unless explicitly configured
README must warn: OpenAI usage is paid
FULL MAVEN MULTI-MODULE STRUCTURE
Repo layout:
spring-ai-firewall/
 ├── pom.xml (parent)
 ├── spring-ai-firewall-core/
 ├── spring-ai-firewall-redis/
 ├── spring-ai-firewall-openai/
 ├── spring-ai-firewall-autoconfigure/
 ├── spring-ai-firewall-starter/
 ├── example-demo-app/
 ├── README.md
 ├── LICENSE (MIT)

Modules:
core
Filter
Bot detector
Risk scoring
Rule engine interfaces
redis
RedisRateLimiter implementation
openai
OpenAI client + AI scoring provider
autoconfigure
Spring Boot auto config + properties
starter
Single dependency entry point
demo app
Example REST controller
SPRING BOOT STARTER AUTO CONFIGURATION
Provide:
FirewallAutoConfiguration
Conditional activation:
@ConditionalOnProperty(prefix="firewall", name="enabled", matchIfMissing=true)

AI auto-config must be conditional:
@ConditionalOnProperty(prefix="firewall.ai", name="enabled", havingValue="true")

Mandatory Boot 3 Imports File
Create:
META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports
Containing:
com.sudharma.firewall.autoconfig.FirewallAutoConfiguration
com.sudharma.firewall.openai.OpenAiFirewallAutoConfiguration
MAVEN CENTRAL PUBLISHING REQUIREMENTS
Configure for Maven Central release.
Required:
groupId: io.github.sudharma
Signed artifacts with GPG
Source + Javadoc jars
Nexus staging plugin
distributionManagement OSSRH
Proper metadata (license, developers, scm)
Include full release profile:
mvn clean deploy -P release
README REQUIREMENTS
README must include:
Overview
Features list
Architecture diagram (ASCII)
Installation from Maven Central:
<dependency>
  <groupId>io.github.sudharma</groupId>
  <artifactId>spring-ai-firewall-starter</artifactId>
  <version>1.0.0</version>
</dependency>

Redis setup instructions
OpenAI optional configuration example
Warning: OpenAI costs money
Demo instructions
Roadmap section
OUTPUT EXPECTATION
Generate all source code files with correct packages:
Base package:
com.sudharma.firewall

The result must compile successfully:
mvn clean install

Demo app must run with Redis.
DELIVERABLES
Provide:
Full Maven multi-module code
All pom.xml files
All Java classes
AutoConfiguration imports file
application.yml examples
docker-compose.yml
README.md
MIT LICENSE text
Maven Central publishing-ready configuration

| Decision Area | Final Choice                   |
| ------------- | ------------------------------ |
| Spring Boot   | 3.3.x                          |
| Java          | 17                             |
| Rate limiting | Redis Sliding Window           |
| Key priority  | API key > IP                   |
| Path rules    | First match wins               |
| Risk scoring  | 80% threshold-based escalation |
| OpenAI        | Optional REST client           |
| AI timeout    | 2 seconds fail-open            |
| AI caching    | Redis 2–5 min TTL              |
| Logging       | SLF4J default                  |
| Filter order  | HIGHEST_PRECEDENCE             |
| Publishing    | Maven Central                  |

# Spring AI Firewall

[![Maven Central](https://img.shields.io/maven-central/v/io.github.sudharma/spring-ai-firewall-starter.svg)](https://search.maven.org/artifact/io.github.sudharma/spring-ai-firewall-starter)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

Production-ready Spring Boot Starter for AI-powered API firewall protection with distributed rate limiting, bot detection, and risk scoring.

## Features

- 🚀 **Plug-and-Play** - Single dependency activation
- 🔥 **Distributed Rate Limiting** - Redis-backed sliding window across multiple instances
- 🤖 **Bot Detection** - Heuristic-based detection of headless browsers and automated tools
- 📊 **Risk Scoring Engine** - Multi-signal threat assessment with configurable thresholds
- 🎯 **Path-Based Rules** - Fine-grained YAML configuration per endpoint
- 📝 **Audit Logging** - SLF4J-based event logging with custom logger support
- 🧠 **Optional OpenAI Integration** - AI-powered anomaly detection (disabled by default)
- ⚡ **High Performance** - Runs before Spring Security with minimal overhead

## Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                     HTTP Request                            │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
         ┌─────────────────────────┐
         │   FirewallFilter        │ (Highest Precedence)
         │   - Extract IP/Headers  │
         │   - Apply Rate Limit    │
         └──────────┬──────────────┘
                    │
         ┌──────────▼──────────┐
         │   RuleEngine        │
         │   - Match Path      │
         │   - Get Limits      │
         └──────────┬──────────┘
                    │
         ┌──────────▼──────────────┐
         │   RedisRateLimiter      │
         │   - Sliding Window      │
         │   - Distributed State   │
         └──────────┬──────────────┘
                    │
         ┌──────────▼──────────────┐
         │   RiskScoringEngine     │
         │   - Bot Detection       │
         │   - Rate Analysis       │
         │   - AI Scoring (opt)    │
         └──────────┬──────────────┘
                    │
         ┌──────────▼──────────────┐
         │   Decision              │
         │   < 30: Allow           │
         │   30-70: Log            │
         │   > 70: Block (403)     │
         └─────────────────────────┘
```

## Installation

### Maven

```xml
<dependency>
  <groupId>io.github.sudharma</groupId>
  <artifactId>spring-ai-firewall-starter</artifactId>
  <version>1.0.0</version>
</dependency>
```

### Gradle

```gradle
implementation 'io.github.sudharma:spring-ai-firewall-starter:1.0.0'
```

## Quick Start

### 1. Setup Redis

**Option A: Cloud Redis (Recommended - No Installation)**

Sign up for free at [Upstash](https://upstash.com/) and get your Redis credentials.

**Option B: Local Redis**
```bash
docker-compose up -d
```

### 2. Configure Application

**Using Environment Variables (Recommended):**
```bash
# Set environment variables
export REDIS_HOST=your-redis-host
export REDIS_PORT=6379
export REDIS_PASSWORD=your-password
export REDIS_SSL=true

# Optional: Enable OpenAI
export OPENAI_API_KEY=sk-your-key-here
```

**Or in application.yml:**
```yaml
spring:
  data:
    redis:
      host: ${REDIS_HOST:localhost}
      port: ${REDIS_PORT:6379}
      password: ${REDIS_PASSWORD:}
      ssl:
        enabled: ${REDIS_SSL:false}

firewall:
  enabled: true
  rate-limit:
    requests-per-minute: 100
```

### 3. Run Your Application

The firewall automatically activates! No code changes needed.

## Configuration

### Basic Configuration

```yaml
firewall:
  enabled: true                    # Enable/disable firewall (default: true)
  api-key-header: X-API-Key        # Header name for API key (default: X-API-Key)
  rate-limit:
    enabled: true
    requests-per-minute: 100       # Global rate limit
```

### Path-Based Rules

```yaml
firewall:
  rules:
    - path: "/api/auth/**"
      requests-per-minute: 10
      action: BLOCK
      sensitive: true              # Adds +30 to risk score
      
    - path: "/api/public/**"
      requests-per-minute: 200
      action: ALLOW
      
    - path: "/api/admin/**"
      requests-per-minute: 5
      sensitive: true
```

Rules are evaluated in order - **first match wins**.

### Rate Limit Key Priority

1. **API Key** (if present in header) - `firewall:rate:apikey:{key}`
2. **IP Address** (fallback) - `firewall:rate:ip:{ip}`

### Risk Scoring Thresholds

| Score Range | Action | Description |
|------------|--------|-------------|
| 0-29 | ✅ Allow | Low risk, request proceeds |
| 30-70 | ⚠️ Log | Medium risk, logged but allowed |
| 71-100 | 🚫 Block | High risk, returns 403 |

### Risk Score Signals

| Signal | Score | Trigger |
|--------|-------|---------|
| High request rate | +50 | >80% of rate limit |
| Missing User-Agent | +20 | No UA header |
| Headless browser | +40 | HeadlessChrome, Puppeteer, etc. |
| Suspicious Accept | +10 | Missing or `*/*` |
| Sensitive endpoint | +30 | Path marked as sensitive |
| AI anomaly | 0-100 | OpenAI detection (optional) |

## OpenAI Integration (Optional)

⚠️ **WARNING: OpenAI API calls cost money (~$0.0001/request). Disabled by default.**

### Prerequisites

1. Get OpenAI API key from https://platform.openai.com/api-keys
2. Add OpenAI module dependency:

```xml
<dependency>
  <groupId>io.github.sudharma</groupId>
  <artifactId>spring-ai-firewall-openai</artifactId>
  <version>1.0.0</version>
</dependency>
```

### Enable AI Scoring

**Using Environment Variables (Recommended):**
```bash
export OPENAI_API_KEY=sk-your-key-here
```

**In application.yml:**
```yaml
firewall:
  ai:
    enabled: true
    provider: openai
    api-key: ${OPENAI_API_KEY}
    model: gpt-4o-mini
    timeout-seconds: 2             # Fail-open after timeout
    cache-ttl-minutes: 3           # Redis cache duration
```

### How It Works

1. Sends request metadata (IP, path, headers, frequency) to OpenAI
2. AI returns risk score 0-100 using machine learning
3. Score is cached in Redis for 3 minutes
4. On timeout/error, fails open (score = 0)

### AI vs Heuristics Comparison

| Detection Method | Bot Score | Accuracy | Cost |
|-----------------|-----------|----------|------|
| Heuristics Only | 40 | Good | Free |
| With AI | 75 | Excellent | ~$0.0001/req |

**Example:** A sophisticated bot mimicking Chrome:
- Heuristics: 10 (low risk, allowed)
- AI: 65 (detected pattern, blocked)

### Cost Optimization

- Results cached in Redis (3 min TTL)
- Only metadata sent (no request body)
- Uses lightweight `gpt-4o-mini` model
- 2-second timeout prevents delays
- Caching reduces cost by 95%+

**Cost Estimate:**
- 1,000 unique requests/day = $0.10/day
- 10,000 unique requests/day = $1.00/day
- With caching: ~$0.05-0.20/day for most apps

## Custom Audit Logger

Implement custom logging:

```java
@Component
public class CustomAuditLogger implements FirewallAuditLogger {
    
    @Override
    public void log(FirewallEvent event) {
        // Send to your monitoring system
        // Store in database
        // Trigger alerts
    }
}
```

## Demo Application

Run the included demo:

```bash
# Build project
mvn clean install

# Set environment variables
set REDIS_HOST=your-redis-host
set REDIS_PORT=6379
set REDIS_PASSWORD=your-password
set REDIS_SSL=true
set OPENAI_API_KEY=sk-your-key-here  # Optional

# Run demo
cd example-demo-app
mvn spring-boot:run
```

### Test Endpoints

**Test 1: Normal Request**
```bash
curl http://localhost:8080/api/public/hello
```
Expected: `{"message":"Hello from public endpoint"}`

**Test 2: Bot Detection**
```bash
curl -H "User-Agent: HeadlessChrome" -X POST http://localhost:8080/api/auth/login
```
Expected: Blocked with risk score 80-90

**Test 3: Rate Limiting (Windows)**
```bash
for /L %i in (1,1,15) do @curl -X POST http://localhost:8080/api/auth/login && @echo.
```
Expected: First 10 allowed, rest blocked

**Test 4: AI Detection (if enabled)**
```bash
curl -H "User-Agent: python-requests/2.28.0" -X POST http://localhost:8080/api/auth/login
```
Expected: AI detects suspicious pattern, blocked

**Test 5: API Key Rate Limiting**
```bash
curl -H "X-API-Key: test-key-123" http://localhost:8080/api/public/hello
```
Expected: Separate rate limit for this API key

### Expected Responses

**Allowed:**
```json
{
  "message": "Hello from public endpoint"
}
```

**Blocked:**
```json
{
  "blocked": true,
  "reason": "High risk bot traffic detected",
  "riskScore": 85
}
```

**Rate Limited:**
```json
{
  "blocked": true,
  "reason": "Rate limit exceeded",
  "riskScore": 100
}
```

## Requirements

- Java 17+
- Spring Boot 3.3.x
- Redis 6.0+ (Upstash, AWS ElastiCache, Azure Cache, or local)
- OpenAI API key (optional, for AI features)

## Security Best Practices

🔒 **Never commit credentials to Git!**

1. Use environment variables for sensitive data:
   ```bash
   export REDIS_PASSWORD=your-password
   export OPENAI_API_KEY=your-key
   ```

2. Use `.env` files (add to `.gitignore`):
   ```bash
   # .env
   REDIS_HOST=your-host
   REDIS_PASSWORD=your-password
   OPENAI_API_KEY=your-key
   ```

3. For production, use secret management:
   - AWS Secrets Manager
   - Azure Key Vault
   - HashiCorp Vault
   - Kubernetes Secrets

## Roadmap

- [x] Redis-backed distributed rate limiting
- [x] Bot detection with heuristics
- [x] Risk scoring engine
- [x] OpenAI integration
- [x] Path-based YAML rules
- [x] Audit logging
- [ ] Prometheus metrics integration
- [ ] WebFlux/Reactive support
- [ ] IP whitelist/blacklist
- [ ] CAPTCHA challenge integration
- [ ] GraphQL support
- [ ] Rate limit by user ID
- [ ] Distributed tracing (OpenTelemetry)
- [ ] Admin dashboard UI

## Contributing

Contributions welcome! Please open an issue or PR.

## License

MIT License - see [LICENSE](LICENSE) file for details.

## Support

- GitHub Issues: https://github.com/sudharma/spring-ai-firewall/issues
- Documentation: https://github.com/sudharma/spring-ai-firewall/wiki

---

**Built with ❤️ for the Spring Boot community**

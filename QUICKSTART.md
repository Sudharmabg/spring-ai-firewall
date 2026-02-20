# Quick Start Guide

## 5-Minute Setup

### Step 1: Start Redis
```bash
docker-compose up -d
```

### Step 2: Build the Project
```bash
mvn clean install
```

### Step 3: Run Demo App
```bash
cd example-demo-app
mvn spring-boot:run
```

### Step 4: Test It

**Normal Request:**
```bash
curl http://localhost:8080/api/public/hello
```

**Bot Detection (will be blocked):**
```bash
curl -H "User-Agent: HeadlessChrome" http://localhost:8080/api/auth/login
```

**Rate Limit Test:**
```bash
# Run this 15 times quickly - last few will be blocked
for i in {1..15}; do 
  curl http://localhost:8080/api/auth/login
  echo ""
done
```

## Use in Your Project

### 1. Add Dependency

```xml
<dependency>
  <groupId>io.github.sudharma</groupId>
  <artifactId>spring-ai-firewall-starter</artifactId>
  <version>1.0.0</version>
</dependency>
```

### 2. Configure Redis

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
```

### 3. Done!

The firewall is now active. Customize with:

```yaml
firewall:
  rate-limit:
    requests-per-minute: 100
  rules:
    - path: "/api/sensitive/**"
      requests-per-minute: 10
```

## Troubleshooting

**Redis Connection Error:**
- Ensure Redis is running: `docker ps`
- Check connection: `redis-cli ping`

**Firewall Not Working:**
- Check logs for `FirewallFilter` initialization
- Verify `firewall.enabled=true` in config

**Too Many Blocks:**
- Increase `requests-per-minute`
- Check bot detection thresholds
- Review audit logs

## Next Steps

- Read full [README.md](README.md)
- Customize [audit logging](#custom-audit-logger)
- Enable [OpenAI integration](#openai-integration) (optional)
- Deploy to production with proper Redis cluster

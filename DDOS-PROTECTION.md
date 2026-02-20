# DDoS Protection Capabilities

## Overview

Spring AI Firewall provides **application-layer (Layer 7) DDoS protection** as part of a comprehensive defense-in-depth strategy. This document explains what it can and cannot protect against, and how to architect a complete DDoS defense system.

---

##  What This Firewall DOES Protect Against

### Application-Layer (Layer 7) DDoS Attacks

| Attack Type | Protection Level | How It Works |
|-------------|------------------|--------------|
| **HTTP Flood** |  Excellent | Rate limiting blocks excessive requests per IP |
| **Slowloris** |  Good | Timeout mechanisms prevent connection exhaustion |
| **API Abuse** |  Excellent | Distributed rate limiting across instances |
| **Credential Stuffing** |  Excellent | Bot detection + rate limiting on auth endpoints |
| **Web Scraping** |  Excellent | Pattern detection blocks automated tools |
| **Single-IP Attacks** |  Excellent | Per-IP rate limiting stops individual attackers |

### Example: HTTP Flood Protection

```
Attacker sends 1,000 requests/second from single IP
→ Firewall rate limit: 100 requests/minute
→ After 100 requests: All subsequent requests blocked
→ Attack mitigated 
```

**Configuration:**
```yaml
firewall:
  rate-limit:
    requests-per-minute: 100
  rules:
    - path: "/api/auth/**"
      requests-per-minute: 10
      sensitive: true
```

---

## ⚠️ What It PARTIALLY Protects Against

### Distributed Application-Layer DDoS

| Attack Type | Protection Level | Limitation |
|-------------|------------------|------------|
| **Botnet Attacks** | ⚠️ Partial | Each bot IP under limit, but total volume high |
| **Low-and-Slow** | ⚠️ Partial | Requires careful tuning to detect |
| **Sophisticated Bots** | ⚠️ Partial | AI helps but not foolproof |

### Example: Distributed Botnet

```
Botnet with 10,000 IPs, each sending 50 requests/minute
→ Each IP under limit (100 req/min) 
→ Total: 500,000 requests/minute to your server ❌
→ Server overwhelmed despite per-IP limits
```

**Why?** The firewall can't distinguish between 10,000 legitimate users and a botnet without additional intelligence.

**Mitigation:** Combine with CDN/DDoS service (Cloudflare, AWS Shield)

---

## ❌ What It CANNOT Protect Against

### Network-Layer (Layer 3/4) DDoS Attacks

| Attack Type | Protection | Reason |
|-------------|-----------|---------|
| **SYN Flood** | ❌ No | Happens before TCP connection established |
| **UDP Flood** | ❌ No | Not HTTP traffic |
| **ICMP Flood (Ping)** | ❌ No | Network layer attack |
| **DNS Amplification** | ❌ No | Infrastructure level |
| **Volumetric Attacks** | ❌ No | Saturates network bandwidth before reaching app |

**Why?** These attacks occur at the network/transport layer, BEFORE your Spring Boot application even sees the request.

```
Network Stack:
┌─────────────────────────────────┐
│ Layer 7: Application (HTTP)     │ ← Spring AI Firewall protects here ✅
├─────────────────────────────────┤
│ Layer 4: Transport (TCP/UDP)    │ ← SYN Flood happens here ❌
├─────────────────────────────────┤
│ Layer 3: Network (IP)           │ ← ICMP Flood happens here ❌
├─────────────────────────────────┤
│ Layer 2: Data Link              │
├─────────────────────────────────┤
│ Layer 1: Physical                │
└─────────────────────────────────┘
```

---

## Complete DDoS Protection Architecture

### Recommended Multi-Layer Defense

```
┌─────────────────────────────────────────────────────────┐
│ Layer 1: CDN/DDoS Protection Service                   │
│ - Cloudflare (Free tier available)                     │
│ - AWS Shield Standard (Free with AWS)                  │
│ - Akamai, Imperva (Enterprise)                         │
│                                                         │
│ Blocks: Network-layer attacks, volumetric attacks      │
│ Filters: 95-99% of malicious traffic                   │
└──────────────────────┬──────────────────────────────────┘
                       │ Clean traffic only
                       ▼
┌─────────────────────────────────────────────────────────┐
│ Layer 2: Load Balancer with Rate Limiting              │
│ - AWS ALB/NLB                                           │
│ - NGINX Plus                                            │
│ - HAProxy                                               │
│                                                         │
│ Provides: Geographic distribution, connection limits   │
└──────────────────────┬──────────────────────────────────┘
                       │ Distributed load
                       ▼
┌─────────────────────────────────────────────────────────┐
│ Layer 3: Spring AI Firewall (Application Layer)      │
│ - Per-IP rate limiting                                 │
│ - Per-API-key rate limiting                            │
│ - Bot detection (heuristics + AI)                      │
│ - Risk scoring engine                                  │
│ - Path-based rules                                     │
│                                                         │
│ Provides: Smart filtering, bot detection, API abuse    │
└──────────────────────┬──────────────────────────────────┘
                       │ Legitimate traffic
                       ▼
┌─────────────────────────────────────────────────────────┐
│ Your Spring Boot Application                           │
└─────────────────────────────────────────────────────────┘
```

---

## When to Use This Firewall for DDoS

###  Excellent Use Cases

1. **Small-to-Medium Attacks** (<10,000 requests/second)
   - Single or few IPs attacking
   - Application-layer HTTP floods
   - API abuse and scraping

2. **Bot-Driven Attacks**
   - Credential stuffing
   - Account takeover attempts
   - Automated scraping

3. **As Part of Defense-in-Depth**
   - Layer 3 protection behind CDN
   - Smart filtering after load balancer
   - API-specific rate limiting

### ❌ Not Sufficient Alone For

1. **Large-Scale DDoS** (>100,000 requests/second)
   - Requires CDN/DDoS service
   - Network bandwidth saturation

2. **Distributed Botnets** (10,000+ IPs)
   - Each IP under rate limit
   - Total volume overwhelms server

3. **Network-Layer Attacks**
   - SYN flood, UDP flood
   - Requires infrastructure protection

4. **Volumetric Attacks** (100+ Gbps)
   - Saturates network bandwidth
   - Needs enterprise DDoS service

---

## Configuration for DDoS Scenarios

### Aggressive Rate Limiting

```yaml
firewall:
  enabled: true
  rate-limit:
    enabled: true
    requests-per-minute: 60  # Strict global limit
  
  rules:
    # Protect authentication endpoints
    - path: "/api/auth/**"
      requests-per-minute: 10
      sensitive: true
    
    # Protect API endpoints
    - path: "/api/**"
      requests-per-minute: 30
      sensitive: true
    
    # Allow health checks
    - path: "/health"
      requests-per-minute: 1000
    
    # Public content (less strict)
    - path: "/public/**"
      requests-per-minute: 100
```

### Enable AI for Sophisticated Attacks

```yaml
firewall:
  ai:
    enabled: true
    provider: openai
    api-key: ${OPENAI_API_KEY}
    model: gpt-4o-mini
    timeout-seconds: 2
    cache-ttl-minutes: 3
```

AI helps detect:
- Distributed attacks with varying patterns
- Sophisticated bots mimicking browsers
- Low-and-slow attacks
- Anomalous behavior patterns

---

## DDoS Protection Comparison

| Attack Scenario | This Firewall Alone | With CDN | With CDN + Firewall |
|----------------|---------------------|----------|---------------------|
| Single IP (1K req/sec) |  Blocked |  Blocked |  Blocked |
| Small botnet (100 IPs) | ⚠️ Partial |  Blocked |  Blocked |
| Large botnet (10K IPs) | ❌ Overwhelmed |  Blocked |  Blocked |
| SYN Flood | ❌ No protection |  Blocked |  Blocked |
| Volumetric (100 Gbps) | ❌ No protection |  Blocked |  Blocked |
| Sophisticated bots | ⚠️ Partial | ⚠️ Partial |  Blocked (AI) |

---

## Recommended DDoS Protection Services

### Free/Low-Cost Options

1. **Cloudflare Free Tier**
   - Free DDoS protection
   - CDN included
   - Easy setup
   - **Recommended for startups**

2. **AWS Shield Standard**
   - Free with AWS
   - Protects against common attacks
   - Integrated with AWS services

3. **Google Cloud Armor**
   - Pay-as-you-go
   - Integrated with GCP

### Enterprise Options

1. **Cloudflare Enterprise**
   - Advanced DDoS protection
   - 100 Tbps+ capacity
   - 24/7 support

2. **AWS Shield Advanced**
   - $3,000/month
   - DDoS response team
   - Cost protection

3. **Akamai Prolexic**
   - Enterprise-grade
   - Scrubbing centers worldwide

---

## Monitoring and Alerting

### Key Metrics to Monitor

```java
@Component
public class DDoSMonitoringLogger implements FirewallAuditLogger {
    
    private final MetricsService metrics;
    
    @Override
    public void log(FirewallEvent event) {
        // Track blocked requests
        if (event.blocked()) {
            metrics.increment("firewall.blocked", 
                "reason", event.reason(),
                "ip", event.ip());
        }
        
        // Alert on high block rate
        if (getBlockRatePerMinute() > 1000) {
            alerting.send("Possible DDoS attack detected");
        }
    }
}
```

### Alerts to Configure

1. **High block rate** (>1000 blocks/minute)
2. **Many unique IPs** (>100 IPs blocked/minute)
3. **Specific endpoint targeted** (>80% blocks on one path)
4. **Rate limit exhaustion** (>90% of limit used)

---

## Testing DDoS Resilience

### Load Testing Tools

```bash
# Apache Bench
ab -n 10000 -c 100 http://localhost:8080/api/endpoint

# wrk
wrk -t12 -c400 -d30s http://localhost:8080/api/endpoint

# Gatling (recommended for realistic scenarios)
```

### Simulate DDoS Attack

```bash
# Single IP attack (should be blocked)
for i in {1..1000}; do
  curl http://localhost:8080/api/auth/login &
done

# Check logs for blocks
grep "FIREWALL BLOCKED" application.log
```

---

## Best Practices

### 1. Use CDN for Public-Facing Apps
```
Always put Cloudflare or similar in front of your app
```

### 2. Implement Multiple Layers
```
CDN → Load Balancer → Firewall → Application
```

### 3. Monitor and Alert
```
Set up alerts for unusual traffic patterns
```

### 4. Regular Testing
```
Load test your application regularly
```

### 5. Have an Incident Response Plan
```
Document steps to take during an attack
```

---

## Summary

| Protection Layer | What It Does | Cost | Complexity |
|-----------------|--------------|------|------------|
| **Spring AI Firewall** | Application-layer filtering, bot detection | Free | Low |
| **Cloudflare Free** | Network + application DDoS protection | Free | Low |
| **AWS Shield Standard** | Network-layer DDoS protection | Free | Medium |
| **Enterprise DDoS Service** | Comprehensive protection | $$$$ | High |

### Recommended Setup by Scale

**Startup/Small App:**
```
Cloudflare Free + Spring AI Firewall
Cost: $0/month
Protection: Good for most attacks
```

**Growing Business:**
```
Cloudflare Pro + AWS ALB + Spring AI Firewall
Cost: ~$20-50/month
Protection: Excellent for 99% of attacks
```

**Enterprise:**
```
Cloudflare Enterprise + AWS Shield Advanced + Spring AI Firewall
Cost: $3,000+/month
Protection: Military-grade
```

---

## Conclusion

**Spring AI Firewall is excellent for:**
-  Application-layer DDoS protection
-  Bot detection and blocking
-  API abuse prevention
-  Smart filtering and risk scoring

**But should be combined with:**
- 🛡️ CDN/DDoS service (Cloudflare, AWS Shield)
- 🛡️ Load balancer with rate limiting
- 🛡️ Monitoring and alerting

**Bottom Line:** Use this firewall as **Layer 3 (application-level) protection** in a multi-layer defense strategy. It's not a replacement for infrastructure-level DDoS protection, but an essential component of comprehensive security.

---

**For questions or contributions, see [CONTRIBUTING.md](CONTRIBUTING.md)**

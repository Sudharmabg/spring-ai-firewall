# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2024-01-XX

### Added
- Initial release of Spring AI Firewall
- Servlet filter with highest precedence
- Redis-backed sliding window rate limiting
- Bot detection heuristics (headless browsers, missing UA)
- Risk scoring engine with configurable thresholds
- Path-based YAML rule engine with AntPathMatcher
- SLF4J audit logging with custom logger support
- Optional OpenAI integration for AI-powered anomaly detection
- AI response caching in Redis (2-5 min TTL)
- Fail-open behavior on AI timeout (2 seconds)
- Multi-module Maven structure
- Spring Boot 3.3.x support
- Java 17 compatibility
- Maven Central publishing configuration
- Comprehensive README with examples
- Demo application with sample endpoints
- Docker Compose for Redis
- MIT License

### Features
- Distributed rate limiting across multiple instances
- API key priority over IP-based rate limiting
- First-match-wins rule evaluation
- Risk score thresholds: <30 allow, 30-70 log, >70 block
- HTTP 429 for rate limits, 403 for risk blocks
- JSON error responses
- X-Forwarded-For IP extraction
- Configurable API key header name

### Configuration
- `firewall.enabled` - Enable/disable firewall (default: true)
- `firewall.rate-limit.requests-per-minute` - Global rate limit
- `firewall.rules` - Path-based rules with per-path limits
- `firewall.ai.enabled` - Enable OpenAI integration (default: false)
- `firewall.ai.model` - OpenAI model selection (default: gpt-4o-mini)
- `firewall.ai.timeout-seconds` - AI call timeout (default: 2)
- `firewall.ai.cache-ttl-minutes` - Cache duration (default: 3)

### Documentation
- Complete README with architecture diagram
- Quick start guide
- Configuration examples
- OpenAI integration guide with cost warnings
- Contributing guidelines
- Maven Central publishing instructions

## [Unreleased]

### Planned
- Prometheus metrics integration
- WebFlux/Reactive support
- IP whitelist/blacklist
- CAPTCHA challenge integration
- GraphQL support
- Rate limit by user ID
- Distributed tracing (OpenTelemetry)
- Admin dashboard UI
- Unit and integration tests
- Performance benchmarks

---

[1.0.0]: https://github.com/sudharma/spring-ai-firewall/releases/tag/v1.0.0

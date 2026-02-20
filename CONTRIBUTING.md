# Contributing to Spring AI Firewall

Thank you for your interest in contributing! This document provides guidelines for contributing to the project.

## Getting Started

1. Fork the repository
2. Clone your fork: `git clone https://github.com/YOUR_USERNAME/spring-ai-firewall.git`
3. Create a branch: `git checkout -b feature/your-feature-name`
4. Make your changes
5. Test thoroughly
6. Submit a pull request

## Development Setup

### Prerequisites
- Java 17+
- Maven 3.8+
- Docker (for Redis)
- IDE (IntelliJ IDEA recommended)

### Build
```bash
mvn clean install
```

### Run Tests
```bash
mvn test
```

### Run Demo
```bash
docker-compose up -d
cd example-demo-app
mvn spring-boot:run
```

## Code Style

- Follow standard Java conventions
- Use meaningful variable names
- Add JavaDoc for public APIs
- Keep methods focused and small
- Write tests for new features

## Module Structure

- `spring-ai-firewall-core` - Core firewall logic (no external dependencies)
- `spring-ai-firewall-redis` - Redis rate limiting implementation
- `spring-ai-firewall-openai` - Optional AI integration
- `spring-ai-firewall-autoconfigure` - Spring Boot auto-configuration
- `spring-ai-firewall-starter` - Starter dependency aggregator
- `example-demo-app` - Demo application

## Pull Request Guidelines

### Before Submitting
- [ ] Code compiles without errors
- [ ] All tests pass
- [ ] New features have tests
- [ ] Documentation updated (README, JavaDoc)
- [ ] Commit messages are clear

### PR Description Should Include
- What problem does this solve?
- How does it work?
- Any breaking changes?
- Screenshots (if UI changes)

## Feature Requests

Open an issue with:
- Clear description of the feature
- Use case / motivation
- Proposed implementation (optional)

## Bug Reports

Include:
- Spring Boot version
- Java version
- Steps to reproduce
- Expected vs actual behavior
- Stack trace (if applicable)

## Areas for Contribution

### High Priority
- [ ] Unit tests for all modules
- [ ] Integration tests
- [ ] Performance benchmarks
- [ ] Prometheus metrics
- [ ] WebFlux support

### Medium Priority
- [ ] IP whitelist/blacklist
- [ ] CAPTCHA integration
- [ ] GraphQL support
- [ ] Admin dashboard

### Documentation
- [ ] More examples
- [ ] Video tutorials
- [ ] Blog posts
- [ ] Translations

## Code Review Process

1. Maintainer reviews PR within 3-5 days
2. Feedback provided via comments
3. Author addresses feedback
4. Approved PRs merged to main
5. Released in next version

## Release Process

Maintainers handle releases:
1. Update version in all POMs
2. Update CHANGELOG
3. Tag release
4. Deploy to Maven Central
5. Create GitHub release

## Questions?

- Open a GitHub issue
- Start a discussion
- Email: sudharma@example.com

## License

By contributing, you agree that your contributions will be licensed under the MIT License.

---

Thank you for making Spring AI Firewall better! 🚀

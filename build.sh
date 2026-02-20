#!/bin/bash

echo "========================================"
echo "Spring AI Firewall - Build Verification"
echo "========================================"
echo ""

echo "[1/4] Checking Maven installation..."
if ! command -v mvn &> /dev/null; then
    echo "ERROR: Maven not found. Please install Maven 3.8+"
    exit 1
fi
mvn --version
echo ""

echo "[2/4] Checking Docker installation..."
if ! command -v docker &> /dev/null; then
    echo "WARNING: Docker not found. Redis will need to be started manually."
else
    echo "Starting Redis..."
    docker-compose up -d
fi
echo ""

echo "[3/4] Building project..."
mvn clean install -DskipTests
if [ $? -ne 0 ]; then
    echo "ERROR: Build failed!"
    exit 1
fi
echo ""

echo "[4/4] Build successful!"
echo ""
echo "========================================"
echo "Next Steps:"
echo "========================================"
echo "1. Start Redis: docker-compose up -d"
echo "2. Run demo: cd example-demo-app && mvn spring-boot:run"
echo "3. Test: curl http://localhost:8080/api/public/hello"
echo ""
echo "See QUICKSTART.md for detailed instructions."
echo "========================================"

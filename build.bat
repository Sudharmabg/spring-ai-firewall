@echo off
echo ========================================
echo Spring AI Firewall - Build Verification
echo ========================================
echo.

echo [1/4] Checking Maven installation...
call mvn --version
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Maven not found. Please install Maven 3.8+
    exit /b 1
)
echo.

echo [2/4] Checking Docker installation...
call docker --version
if %ERRORLEVEL% NEQ 0 (
    echo WARNING: Docker not found. Redis will need to be started manually.
) else (
    echo Starting Redis...
    call docker-compose up -d
)
echo.

echo [3/4] Building project...
call mvn clean install -DskipTests
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Build failed!
    exit /b 1
)
echo.

echo [4/4] Build successful!
echo.
echo ========================================
echo Next Steps:
echo ========================================
echo 1. Start Redis: docker-compose up -d
echo 2. Run demo: cd example-demo-app ^&^& mvn spring-boot:run
echo 3. Test: curl http://localhost:8080/api/public/hello
echo.
echo See QUICKSTART.md for detailed instructions.
echo ========================================

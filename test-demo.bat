@echo off
setlocal enabledelayedexpansion

echo ========================================
echo Spring AI Firewall - Demo Test Script
echo ========================================
echo.

set BASE_URL=http://localhost:8080

echo Test 1: Normal request to public endpoint
echo -------------------------------------------
curl -s %BASE_URL%/api/public/hello
echo.
echo.

echo Test 2: Bot detection (HeadlessChrome)
echo -------------------------------------------
curl -s -H "User-Agent: HeadlessChrome" %BASE_URL%/api/auth/login
echo.
echo.

echo Test 3: Rate limit test (15 requests to /api/auth/login)
echo -------------------------------------------
echo Sending 15 requests quickly...
for /L %%i in (1,1,15) do (
  echo Request %%i:
  curl -s %BASE_URL%/api/auth/login
  echo.
)
echo.

echo Test 4: Missing User-Agent
echo -------------------------------------------
curl -s -H "User-Agent:" %BASE_URL%/api/data
echo.
echo.

echo Test 5: With API Key (different rate limit)
echo -------------------------------------------
curl -s -H "X-API-Key: test-key-123" %BASE_URL%/api/public/hello
echo.
echo.

echo ========================================
echo Tests Complete!
echo ========================================
echo.
echo Check application logs for firewall decisions
echo.

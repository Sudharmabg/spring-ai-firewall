@echo off
echo Loading environment variables from .env file...

if not exist .env (
    echo ERROR: .env file not found!
    echo Please copy .env.example to .env and fill in your credentials.
    exit /b 1
)

for /f "tokens=1,2 delims==" %%a in (.env) do (
    if not "%%a"=="" if not "%%a:~0,1%"=="#" (
        set %%a=%%b
    )
)

echo Starting Spring AI Firewall Demo...
mvn spring-boot:run

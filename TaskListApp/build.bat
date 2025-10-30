@echo off
REM ============================================
REM Build Script for Windows
REM ============================================

echo ============================================
echo Building Spring Boot Application
echo ============================================
echo.

REM Check if Maven is installed
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Maven is not installed or not in PATH
    echo Please install Maven or use mvnw.cmd
    pause
    exit /b 1
)

echo Cleaning previous build...
mvn clean

echo.
echo Building application...
mvn package -DskipTests

echo.
if %errorlevel% equ 0 (
    echo ============================================
    echo BUILD SUCCESSFUL!
    echo ============================================
    echo.
    echo JAR file created at: target\tasklistapp-0.0.1-SNAPSHOT.jar
    echo.
    echo To run the application:
    echo   1. Run: run.bat
    echo   2. Or: java -jar target\tasklistapp-0.0.1-SNAPSHOT.jar
    echo.
    echo To run with tests:
    echo   mvn clean package
    echo.
) else (
    echo ============================================
    echo BUILD FAILED!
    echo ============================================
    echo Please check the error messages above
)

pause

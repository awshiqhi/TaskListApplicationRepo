@echo off
REM ============================================
REM Spring Boot Application Runner (Windows)
REM ============================================

echo Starting Spring Boot Application...
echo.

REM Check if Java is installed
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Java is not installed or not in PATH
    echo Please install Java 17 or higher
    pause
    exit /b 1
)

REM Navigate to target directory
cd target

REM Check if JAR exists
if not exist "tasklistapp-0.0.1-SNAPSHOT.jar" (
    echo ERROR: JAR file not found!
    echo Please run: mvn clean package
    pause
    exit /b 1
)

REM Run the application
echo Running application on http://localhost:8080
echo Press Ctrl+C to stop
echo.
java -jar tasklistapp-0.0.1-SNAPSHOT.jar

pause

#!/bin/bash
# ============================================
# Spring Boot Application Runner (Linux/Mac)
# ============================================

echo "Starting Spring Boot Application..."
echo ""

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "ERROR: Java is not installed or not in PATH"
    echo "Please install Java 17 or higher"
    exit 1
fi

# Check Java version
JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | awk -F '.' '{print $1}')
if [ "$JAVA_VERSION" -lt 17 ]; then
    echo "ERROR: Java 17 or higher is required"
    echo "Current version: $(java -version 2>&1 | head -n 1)"
    exit 1
fi

# Navigate to target directory
cd target

# Check if JAR exists
if [ ! -f "tasklistapp-0.0.1-SNAPSHOT.jar" ]; then
    echo "ERROR: JAR file not found!"
    echo "Please run: ./mvnw clean package"
    exit 1
fi

# Run the application
echo "Running application on http://localhost:8080"
echo "Press Ctrl+C to stop"
echo ""
java -jar tasklistapp-0.0.1-SNAPSHOT.jar

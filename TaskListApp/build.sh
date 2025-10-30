#!/bin/bash
# ============================================
# Build Script for Linux/Mac
# ============================================

echo "============================================"
echo "Building Spring Boot Application"
echo "============================================"
echo ""

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "ERROR: Maven is not installed or not in PATH"
    echo "Using Maven Wrapper instead..."
    
    # Use Maven Wrapper
    if [ -f "./mvnw" ]; then
        chmod +x ./mvnw
        MAVEN_CMD="./mvnw"
    else
        echo "ERROR: Neither Maven nor Maven Wrapper found"
        exit 1
    fi
else
    MAVEN_CMD="mvn"
fi

echo "Cleaning previous build..."
$MAVEN_CMD clean

echo ""
echo "Building application..."
$MAVEN_CMD package -DskipTests

echo ""
if [ $? -eq 0 ]; then
    echo "============================================"
    echo "BUILD SUCCESSFUL!"
    echo "============================================"
    echo ""
    echo "JAR file created at: target/tasklistapp-0.0.1-SNAPSHOT.jar"
    echo ""
    echo "To run the application:"
    echo "  1. Run: ./run.sh"
    echo "  2. Or: java -jar target/tasklistapp-0.0.1-SNAPSHOT.jar"
    echo ""
    echo "To run with tests:"
    echo "  $MAVEN_CMD clean package"
    echo ""
else
    echo "============================================"
    echo "BUILD FAILED!"
    echo "============================================"
    echo "Please check the error messages above"
    exit 1
fi

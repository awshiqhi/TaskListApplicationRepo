# Quick Start Guide

## 🚀 Fastest Way to Run the Application

### Prerequisites
- Java 17+ installed
- Port 8080 available

---

## Option 1: Using Pre-built Scripts (Recommended)

### Windows
```bash
# Build the application
build.bat

# Run the application
run.bat
```

### Linux/Mac
```bash
# Make scripts executable
chmod +x build.sh run.sh

# Build the application
./build.sh

# Run the application
./run.sh
```

---

## Option 2: Using Maven Commands

### Windows
```bash
# Build
mvn clean package -DskipTests

# Run
java -jar target\tasklistapp-0.0.1-SNAPSHOT.jar
```

### Linux/Mac
```bash
# Build
mvn clean package -DskipTests

# Run
java -jar target/tasklistapp-0.0.1-SNAPSHOT.jar
```

---

## Option 3: Using Docker

```bash
# Build and run with docker-compose
docker-compose up --build

# Build and run manually
docker build -t spring-boot-tasklistapp .
docker run -p 8080:8080 spring-boot-tasklistapp
```

---

## Option 4: Using IDE

1. Open project in your IDE (IntelliJ IDEA, Eclipse, VS Code)
2. Locate `TaskListAppApplication.java`
3. Click **Run** or press the green play button

---

## 🌐 Access the Application

Once started, open your browser:

- **Home/Login**: http://localhost:8080/req/login
- **Signup**: http://localhost:8080/req/signup
- **Task List**: http://localhost:8080/tasklist (after login)
- **H2 Console**: http://localhost:8080/h2-console

---

## 🔑 Default Login

- **Username**: `admin`
- **Password**: `admin123`

---

## ✅ Verify Installation

1. Open terminal
2. Check Java version:
   ```bash
   java -version
   ```
   Expected: Java 17 or higher

3. Check Maven (optional):
   ```bash
   mvn -version
   ```

---

## 🛠️ Troubleshooting

### Port 8080 Already in Use

**Windows:**
```bash
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

**Linux/Mac:**
```bash
lsof -ti:8080 | xargs kill -9
```

### Build Errors

```bash
# Clean and rebuild
mvn clean install -U
```

### Java Not Found

- Download Java 17 from: https://adoptium.net/
- Set JAVA_HOME environment variable

---

## 📚 Next Steps

- Read full [README.md](README.md) for architecture details
- Explore API endpoints
- Customize configuration in `application.yml`
- Check exception handling implementation

---

**Ready in under 5 minutes! 🎉**

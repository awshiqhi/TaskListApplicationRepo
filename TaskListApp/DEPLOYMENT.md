# Deployment Checklist

## 📋 Pre-Deployment Checklist

### ✅ Security
- [ ] Change default admin password (`admin`/`admin123`)
- [ ] Review security headers
- [ ] Disable H2 console in production
- [ ] Set `spring.jpa.show-sql=false`

### ✅ Database
- [ ] Switch from H2 to production database (PostgreSQL/MySQL)
- [ ] Configure database connection pooling
- [ ] Set `spring.jpa.hibernate.ddl-auto=validate` (not `update`)
- [ ] Create database backup strategy
- [ ] Configure database credentials via environment variables

### ✅ Configuration
- [ ] Create production `application-prod.yml`
- [ ] Set `SPRING_PROFILES_ACTIVE=prod`
- [ ] Configure proper logging levels
- [ ] Set up log rotation
- [ ] Configure email server settings (if using email features)
- [ ] Update CORS settings if needed

### ✅ Build & Package
- [ ] Run tests: `mvn clean test`
- [ ] Build JAR: `mvn clean package`
- [ ] Verify JAR size is reasonable
- [ ] Test JAR runs: `java -jar target/tasklistapp-0.0.1-SNAPSHOT.jar`

### ✅ Server Setup
- [ ] Install Java 17+ on server
- [ ] Configure firewall (allow port 8080 or your custom port)
- [ ] Set up reverse proxy (Nginx/Apache) if needed
- [ ] Configure system service for auto-restart
- [ ] Set up monitoring (health checks, metrics)
- [ ] Configure memory limits for JVM

### ✅ Monitoring
- [ ] Set up application logging
- [ ] Configure error alerting
- [ ] Enable Spring Boot Actuator endpoints
- [ ] Set up APM (Application Performance Monitoring)
- [ ] Configure health check endpoints

---

## 🚀 Deployment Steps

### Step 1: Build the Application
```bash
mvn clean package -DskipTests
```

### Step 2: Transfer to Server
```bash
scp target/tasklistapp-0.0.1-SNAPSHOT.jar user@server:/opt/app/
```

### Step 3: Configure Environment
Create `/opt/app/application-prod.yml`:
```yaml
server:
  port: 8080

spring:
  profiles:
    active: prod
  
  datasource:
    url: jdbc:postgresql://localhost:5432/taskdb
    username: ${DB_USER}
    password: ${DB_PASSWORD}
  
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
  
  h2:
    console:
      enabled: false

logging:
  level:
    root: INFO
    com.example.tasklistapp: DEBUG
  file:
    name: /var/log/app/application.log
```

### Step 4: Create Systemd Service
Create `/etc/systemd/system/spring-boot-tasklistapp.service`:
```ini
[Unit]
Description=Spring Boot Task Management Application
After=syslog.target network.target

[Service]
Type=simple
User=appuser
Group=appuser

WorkingDirectory=/opt/app
ExecStart=/usr/bin/java -Xms512m -Xmx1024m -jar /opt/app/tasklistapp-0.0.1-SNAPSHOT.jar --spring.config.location=/opt/app/application-prod.yml

SuccessExitStatus=143
StandardOutput=journal
StandardError=journal

Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

### Step 5: Start Service
```bash
sudo systemctl daemon-reload
sudo systemctl enable spring-boot-tasklistapp
sudo systemctl start spring-boot-tasklistapp
sudo systemctl status spring-boot-tasklistapp
```

### Step 6: Configure Nginx (Optional)
```nginx
server {
    listen 80;
    server_name yourdomain.com;

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

---

## 🔍 Post-Deployment Verification

### Test Endpoints
```bash
# Health check
curl http://localhost:8080/req/login

# API test
curl -X GET http://localhost:8080/tasks \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### Monitor Logs
```bash
# View logs
sudo journalctl -u spring-boot-tasklistapp -f

# Check log file
tail -f /var/log/app/application.log
```

### Verify Service Status
```bash
sudo systemctl status spring-boot-tasklistapp
```

---

## 🐳 Docker Deployment

### Build Image
```bash
docker build -t spring-boot-tasklistapp:latest .
```

### Run Container
```bash
docker run -d \
  --name spring-boot-tasklistapp \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DB_USER=dbuser \
  -e DB_PASSWORD=dbpass \
  --restart unless-stopped \
  spring-boot-tasklistapp:latest
```

### Using Docker Compose
```bash
docker-compose up -d
```

### Monitor Container
```bash
docker logs -f spring-boot-tasklistapp
docker stats spring-boot-tasklistapp
```

---

## 📊 Performance Tuning

### JVM Options
```bash
java -Xms512m -Xmx1024m \
     -XX:+UseG1GC \
     -XX:MaxGCPauseMillis=200 \
     -jar tasklistapp-0.0.1-SNAPSHOT.jar
```

### Database Connection Pool
Add to `application-prod.yml`:
```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
```

---

## 🛡️ Security Hardening

### Update Dependencies
```bash
mvn versions:display-dependency-updates
mvn versions:use-latest-releases
```

### Enable HTTPS
Add to `application-prod.yml`:
```yaml
server:
  ssl:
    key-store: classpath:keystore.p12
    key-store-password: ${KEYSTORE_PASSWORD}
    key-store-type: PKCS12
    key-alias: tomcat
```

---

## 🔄 Rolling Back

### Stop Service
```bash
sudo systemctl stop spring-boot-tasklistapp
```

### Replace JAR
```bash
cp /opt/app/backup/tasklistapp-0.0.1-SNAPSHOT.jar /opt/app/
```

### Restart Service
```bash
sudo systemctl start spring-boot-tasklistapp
```

---

## 📞 Support & Troubleshooting

### Common Issues

**Application won't start**
- Check Java version: `java -version`
- Verify port availability: `netstat -tulpn | grep 8080`
- Check logs: `journalctl -u spring-boot-tasklistapp -n 100`

**Database connection fails**
- Verify database is running
- Check connection credentials
- Test connectivity: `psql -h localhost -U dbuser -d taskdb`

**Out of Memory**
- Increase heap size: `-Xmx2048m`
- Check for memory leaks
- Review GC logs

---

**✅ Deployment Complete!**

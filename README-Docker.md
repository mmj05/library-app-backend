# Library App Backend - Docker Setup

This guide will help you containerize and run the Spring Boot Library Management System using Docker.

## Prerequisites

- Docker (version 20.10 or later)
- Docker Compose (version 2.0 or later)

## Quick Start

1. **Clone the repository** (if you haven't already):
   ```bash
   git clone <your-repo-url>
   cd library-app-backend
   ```

2. **Create environment file**:
   ```bash
   cp env.example .env
   ```

3. **Edit the `.env` file** with your preferred values:
   ```bash
   # Database Configuration
   DB_NAME=library_db
   DB_USERNAME=library_user
   DB_PASSWORD=your_secure_password
   DB_PORT=5432

   # JWT Configuration
   JWT_SECRET=your-very-long-and-secure-jwt-secret-key-here-change-this-in-production-minimum-256-bits
   JWT_EXPIRATION=86400000

   # Server Configuration
   SERVER_PORT=8080

   # pgAdmin Configuration (Optional)
   PGADMIN_EMAIL=admin@library.com
   PGADMIN_PASSWORD=admin123
   PGADMIN_PORT=5050
   ```

4. **Start the application**:
   ```bash
   docker-compose up -d
   ```

## Services

The Docker Compose setup includes:

### 1. PostgreSQL Database (`postgres`)
- **Container**: `library-postgres`
- **Port**: `5432` (configurable via `DB_PORT`)
- **Database**: `library_db` (configurable via `DB_NAME`)
- **Volume**: `postgres_data` for persistent storage

### 2. Spring Boot Application (`app`)
- **Container**: `library-app-backend`
- **Port**: `8080` (configurable via `SERVER_PORT`)
- **Depends on**: PostgreSQL database
- **Health check**: Available at `/actuator/health`

### 3. pgAdmin (Optional) (`pgadmin`)
- **Container**: `library-pgadmin`
- **Port**: `5050` (configurable via `PGADMIN_PORT`)
- **URL**: http://localhost:5050
- **Use this to manage your PostgreSQL database**

## Docker Commands

### Start all services
```bash
docker-compose up -d
```

### View logs
```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f app
docker-compose logs -f postgres
```

### Stop services
```bash
docker-compose down
```

### Stop and remove volumes (⚠️ This will delete your database data)
```bash
docker-compose down -v
```

### Rebuild the application
```bash
# If you make code changes
docker-compose build app
docker-compose up -d app
```

### Scale the application (if needed)
```bash
docker-compose up -d --scale app=3
```

## Health Checks

### Application Health
```bash
curl http://localhost:8080/actuator/health
```

### Database Health
```bash
# Check if PostgreSQL is ready
docker-compose exec postgres pg_isready -U library_user -d library_db
```

## Environment Variables

| Variable | Description | Default Value |
|----------|-------------|---------------|
| `DB_NAME` | PostgreSQL database name | `library_db` |
| `DB_USERNAME` | PostgreSQL username | `library_user` |
| `DB_PASSWORD` | PostgreSQL password | `library_password` |
| `DB_PORT` | PostgreSQL port | `5432` |
| `JWT_SECRET` | JWT secret key | ⚠️ Change in production |
| `JWT_EXPIRATION` | JWT expiration time (ms) | `86400000` (24 hours) |
| `SERVER_PORT` | Spring Boot server port | `8080` |
| `SPRING_PROFILES_ACTIVE` | Spring profiles | `docker` |
| `PGADMIN_EMAIL` | pgAdmin login email | `admin@library.com` |
| `PGADMIN_PASSWORD` | pgAdmin login password | `admin123` |
| `PGADMIN_PORT` | pgAdmin web interface port | `5050` |

## Production Considerations

### Security
1. **Change default passwords**: Update all default passwords in `.env`
2. **JWT Secret**: Use a strong, random JWT secret (minimum 256 bits)
3. **Database credentials**: Use strong, unique database credentials
4. **Network isolation**: Consider using custom networks for security

### Performance
1. **Resource limits**: Add resource limits to services:
   ```yaml
   services:
     app:
       deploy:
         resources:
           limits:
             memory: 512M
             cpus: '0.5'
   ```

2. **Database optimization**: Configure PostgreSQL for your workload
3. **JVM tuning**: Add JVM arguments for production:
   ```yaml
   services:
     app:
       environment:
         JAVA_OPTS: "-Xms256m -Xmx512m -XX:+UseG1GC"
   ```

### Monitoring
1. **Logs**: Configure log aggregation (ELK stack, etc.)
2. **Metrics**: Enable additional actuator endpoints
3. **Health checks**: Monitor application and database health

## Troubleshooting

### Common Issues

1. **Port conflicts**:
   ```bash
   # Check if ports are in use
   lsof -i :8080
   lsof -i :5432
   ```

2. **Database connection issues**:
   ```bash
   # Check if PostgreSQL is running
   docker-compose ps postgres
   
   # Check database logs
   docker-compose logs postgres
   ```

3. **Application startup issues**:
   ```bash
   # Check application logs
   docker-compose logs app
   
   # Check if database is ready
   docker-compose exec postgres pg_isready
   ```

4. **Build issues**:
   ```bash
   # Clean rebuild
   docker-compose build --no-cache app
   ```

### Reset Everything
```bash
# Stop containers, remove volumes, and restart
docker-compose down -v
docker-compose up -d
```

## API Endpoints

Once running, your API will be available at:
- **Base URL**: http://localhost:8080/api
- **Health Check**: http://localhost:8080/actuator/health
- **pgAdmin**: http://localhost:5050

## File Structure

```
library-app-backend/
├── Dockerfile              # Multi-stage build for Spring Boot app
├── docker-compose.yml      # Complete stack definition
├── .dockerignore           # Files to exclude from build context
├── env.example             # Environment variables template
├── README-Docker.md        # This file
└── src/                    # Your Spring Boot application
```

## Next Steps

1. Test the application endpoints
2. Set up CI/CD pipeline
3. Configure monitoring and logging
4. Plan for production deployment
5. Set up database backups 
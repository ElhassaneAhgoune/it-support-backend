Here's a Docker-focused README section based on your `docker-compose.yml`:

```markdown
# IT Support Ticket System - Docker Setup

## Prerequisites
- Docker 20.10+ 
- Docker Compose 1.29+

## Quick Start with Docker Compose

1. **Clone the repository**
   ```bash
   git clone https://github.com/ElhassaneAhgoune/it-support-backend.git 
   cd it-support-backend
   ```

2. **Start the system**
   ```bash
   docker-compose up -d
   ```

This will start:
- Oracle Database 21c
- Redis (for session management)
- MailHog (email testing interface)
- Spring Boot Application

## Service Details

### Container Overview
| Service               | Ports          | Purpose                          |
|-----------------------|----------------|----------------------------------|
| `oracle-db`           | 1521           | Oracle Database instance         |
| `mailhog`             | 1025, 8025     | Email testing interface          |
| `redis`               | 6379           | Session storage                  |
| `app-service`         | 8080           | Spring Boot application          |

### Environment Configuration
The application service is pre-configured with:
```yaml
SPRING_DATASOURCE_URL: jdbc:oracle:thin:@oracle-db:1521/user_authentication
SPRING_DATASOURCE_USERNAME: appuser
SPRING_DATASOURCE_PASSWORD: password
SPRING_REDIS_HOST: redis
SPRING_MAIL_HOST: mailhog
```

## Initial Setup Notes
1. **Database Persistence**  
   - Oracle data is persisted in `oracle-data` volume
   - Initial schema scripts should be placed in `./sql-scripts`

2. **Email Testing**  
   Access MailHog web interface at: `http://localhost:8025`

3. **Application Health**  
   The system will wait for Oracle DB to be healthy before starting the app service

## Common Commands

### Start services
```bash
docker-compose up -d
```

### Stop services
```bash
docker-compose down
```

### View logs
```bash
docker-compose logs -f app-service
```

### Rebuild application
```bash
docker-compose build app-service
```

## Troubleshooting
1. If Oracle DB fails to initialize:
   - Check initialization scripts in `./sql-scripts`
   - Verify credentials match between DB and application service

2. If services don't start in order:
   ```bash
   docker-compose down -v && docker-compose up -d
   ```

3. For persistent data issues:
   ```bash
   docker volume prune
   ```

> **Note**: For production deployments, always:
> - Change default credentials
> - Use proper SSL configuration
> - Remove MailHog and use real email service
```

This structure focuses on Docker-specific setup while maintaining essential information for developers. You can add additional sections for application-specific details (API documentation, features, etc.) below this Docker setup section.

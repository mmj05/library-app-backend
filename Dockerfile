# Use multi-stage build for smaller final image
FROM maven:3.9-eclipse-temurin-17-alpine AS build

# Set working directory
WORKDIR /app

# Copy pom.xml first for better Docker layer caching
COPY pom.xml .

# Download dependencies (this layer will be cached if pom.xml doesn't change)
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine

# Install curl for health checks (optional)
RUN apk add --no-cache curl

# Create app directory and non-root user for security
RUN groupadd -r appuser && useradd -r -g appuser appuser
WORKDIR /app
RUN chown appuser:appuser /app

# Copy the JAR file from build stage
COPY --from=build /app/target/*.jar app.jar

# Copy keystore if it exists (for HTTPS)
COPY --chown=appuser:appuser src/main/resources/keystore.p12 /app/keystore.p12 2>/dev/null || true

# Change to non-root user
USER appuser

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"] 
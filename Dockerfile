# Use Java 21 runtime
FROM eclipse-temurin:21-jre

# Set working directory
WORKDIR /app

# Copy Spring Boot JAR into container
COPY target/finsecure-pay-0.0.1-SNAPSHOT.jar app.jar

# Application port
EXPOSE 8080

# Run as non-root user
USER 10001

HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
    CMD curl --fail --silent http://127.0.0.1:8080/health > /dev/null || exit 1

# Start Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]

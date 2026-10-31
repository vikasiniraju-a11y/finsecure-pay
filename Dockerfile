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

# Start Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]

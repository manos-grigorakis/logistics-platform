# Base image
FROM eclipse-temurin:17-jre-jammy

# Working directory
WORKDIR /app

# Save jar to ARG
ARG JAR_FILE=target/*.jar

# Copy Spring Boot jar into container
COPY ${JAR_FILE} /app/app.jar

# Expose port
EXPOSE 8080

# Run application
CMD ["java", "-jar", "/app/app.jar"]
# === Build Stage ===
FROM maven:3.9.9-eclipse-temurin-17 AS builder

# Working directory
WORKDIR /app

# Copy and install dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy `src` to container's directory
COPY src ./src

# Build jar
RUN mvn clean package -DskipTests

# === Runtime Stage ===
# Base image
FROM eclipse-temurin:17-jre-jammy

# Working directory
WORKDIR /app

# Copy Spring Boot jar into container
COPY --from=builder app/target/*.jar /app/app.jar

# Install tools
RUN apt-get update && apt-get install -y wget && rm -rf /var/lib/apt/lists/*

# Expose ports
# Actuator
EXPOSE 9090

# Tomcat Server
EXPOSE 8080

# Run application
CMD ["java", "-jar", "/app/app.jar"]
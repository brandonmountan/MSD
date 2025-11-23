# Use OpenJDK 17 as base image
FROM openjdk:17-jdk-slim

# Set working directory in container
WORKDIR /app

# Copy Gradle wrapper files
COPY gradlew .
COPY gradle gradle

# Copy build files
COPY build.gradle.kts .
COPY settings.gradle.kts .

# Copy source code
COPY Application.kt .
COPY LdapClient.kt .

# Make gradlew executable
RUN chmod +x gradlew

# Build the application
RUN ./gradlew build --no-daemon

# Create logs directory with full permissions - ADD THIS LINE
RUN mkdir -p /app/logs && chmod -R 777 /app/logs

# Expose port 8080
EXPOSE 8080

# Run the application
CMD ["./gradlew", "run", "--no-daemon"]
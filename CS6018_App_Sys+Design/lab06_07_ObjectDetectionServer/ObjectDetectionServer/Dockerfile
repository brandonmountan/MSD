# Build stage
FROM gradle:8.5-jdk17 AS build
WORKDIR /app

# Copy gradle files
COPY build.gradle.kts settings.gradle.kts ./

# Copy source code
COPY src ./src

# Build the application (without Ktor plugin, just regular build)
RUN gradle build --no-daemon

# Runtime stage
FROM openjdk:17-slim
WORKDIR /app

# Copy the built jar from build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Create directory for photos
RUN mkdir -p /app/photos

# Expose port 8080
EXPOSE 8080

# Run the application
CMD ["java", "-cp", "app.jar", "com.example.ApplicationKt"]
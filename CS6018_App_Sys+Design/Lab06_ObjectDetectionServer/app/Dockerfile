# Build stage
FROM gradle:8.5-jdk17 AS build
WORKDIR /app

# Copy gradle files
COPY build.gradle.kts settings.gradle.kts gradle.properties ./
COPY gradle ./gradle

# Copy source code
COPY src ./src

# Build the application
RUN gradle buildFatJar --no-daemon

# Runtime stage
FROM openjdk:17-slim
WORKDIR /app

# Copy the built jar from build stage
COPY --from=build /app/build/libs/*-all.jar app.jar

# Create directory for photos
RUN mkdir -p /app/photos

# Expose port 8080
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "app.jar"]
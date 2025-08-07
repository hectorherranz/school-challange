# Build stage
FROM gradle:8.5-jdk21 AS build

WORKDIR /app
COPY . .

# Build the application
RUN gradle build --no-daemon

# Runtime stage
FROM openjdk:21-jre-slim

LABEL org.opencontainers.image.title="school-api" \
      org.opencontainers.image.source="https://github.com/hectorherranz/school-api" \
      org.opencontainers.image.created="$BUILD_DATE"

WORKDIR /app

# Copy the built JAR from build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

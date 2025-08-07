#####################  build stage  #####################
FROM gradle:8.12.1-jdk21 AS build
WORKDIR /app

# copy whole project – simplest path
COPY . .

# Build skip Spotless checks *and* the test task
RUN gradle build \
        -x spotlessCheck \
        -x spotlessApply \
        -x test \
        --no-daemon
##################### runtime stage #####################
FROM eclipse-temurin:21-jre
LABEL org.opencontainers.image.title="school-api"

RUN adduser --disabled-password --gecos "" app
WORKDIR /app

# install curl for the health-check
RUN apt-get update && apt-get install -y --no-install-recommends curl \
 && rm -rf /var/lib/apt/lists/*

COPY --from=build /app/build/libs/*.jar app.jar
RUN chown app:app app.jar && chmod 555 app.jar
USER app

EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=3s --start-period=20s --retries=3 \
  CMD curl -fs http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java","-jar","app.jar"]

# Build Stage
FROM eclipse-temurin:25-jdk-alpine AS build

WORKDIR /app

# Install required tools
RUN apk add --no-cache bash curl unzip

# Copy gradle wrapper files first for layer caching
COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle build.gradle
COPY settings.gradle* ./

# Make gradlew executable
RUN chmod +x gradlew

# Download dependencies (cached if gradle files unchanged)
RUN ./gradlew dependencies --no-daemon || true

# Copy source code
COPY src src

# Build the application
RUN ./gradlew build --no-daemon -x test

# Runtime Stage
FROM eclipse-temurin:25-jre-alpine

WORKDIR /app

# Create non-root user for security
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

# Install curl for healthcheck
RUN apk add --no-cache curl

# Copy built JAR from build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Change ownership to non-root user
RUN chown -R appuser:appgroup /app

# Expose application port
EXPOSE 8001


# JVM options optimized for containers
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:InitialRAMPercentage=50.0 -Djava.security.egd=file:/dev/./urandom"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

# Copyright (c) 2023 Cisco Systems, Inc. and its affiliates All rights reserved.
# Use of this source code is governed by a BSD-style
# license that can be found in the LICENSE file.

# Build stage
FROM --platform=linux/amd64 eclipse-temurin:25-jdk AS build

WORKDIR /app

# Copy Gradle files
COPY build.gradle settings.gradle gradle.properties ./
COPY gradle ./gradle
COPY gradlew ./

# Copy source code
COPY src ./src

# Build the application
RUN chmod +x gradlew && ./gradlew build -Dquarkus.package.jar.type=uber-jar -x test

# Runtime stage
FROM --platform=linux/amd64 eclipse-temurin:25-jre

WORKDIR /app

# Copy the built jar
COPY --from=build /app/build/*-runner.jar /app/loan-service.jar

# Expose port (matches Python service port)
EXPOSE 50053

# Set environment variables
ENV JAVA_OPTS="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/loan-service.jar"]

# Phase 1: Project Setup and Build Configuration

## Overview
Initialize the Java 25 Quarkus project with Gradle build configuration, establish project structure, and configure dependencies for MongoDB, gRPC, and REST.

## Changes Required:

### 1. Create Project Directory Structure
**Directory**: `loan-java/`

Create the following directory structure:
```
loan-java/
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── martianbank/
│       │           └── loan/
│       │               ├── LoanApplication.java
│       │               ├── service/
│       │               ├── resource/
│       │               ├── grpc/
│       │               ├── repository/
│       │               └── model/
│       ├── proto/
│       │   └── loan.proto
│       └── resources/
│           └── application.properties
└── Dockerfile
```

### 2. Gradle Build Configuration
**File**: `loan-java/build.gradle.kts`

```kotlin
plugins {
    java
    id("io.quarkus") version "3.17.5"
    jacoco
}

repositories {
    mavenCentral()
    mavenLocal()
}

val quarkusPlatformGroupId: String by project
val quarkusPlatformArtifactId: String by project
val quarkusPlatformVersion: String by project

dependencies {
    implementation(enforcedPlatform("${quarkusPlatformGroupId}:${quarkusPlatformArtifactId}:${quarkusPlatformVersion}"))

    // REST API
    implementation("io.quarkus:quarkus-rest")
    implementation("io.quarkus:quarkus-rest-jackson")

    // gRPC
    implementation("io.quarkus:quarkus-grpc")

    // MongoDB
    implementation("io.quarkus:quarkus-mongodb-client")

    // Health checks
    implementation("io.quarkus:quarkus-smallrye-health")

    // Arc (CDI)
    implementation("io.quarkus:quarkus-arc")

    // Testing
    testImplementation("io.quarkus:quarkus-junit5")
    testImplementation("io.rest-assured:rest-assured")
    testImplementation("org.mockito:mockito-core:5.14.2")
    testImplementation("org.mockito:mockito-junit-jupiter:5.14.2")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

tasks.withType<Test> {
    systemProperty("java.util.logging.manager", "org.jboss.logmanager.LogManager")
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = "0.90".toBigDecimal()
            }
        }
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-parameters")
}
```

### 3. Gradle Settings
**File**: `loan-java/settings.gradle.kts`

```kotlin
pluginManagement {
    val quarkusPluginVersion: String by settings
    val quarkusPluginId: String by settings
    repositories {
        mavenCentral()
        gradlePluginPortal()
        mavenLocal()
    }
    plugins {
        id(quarkusPluginId) version quarkusPluginVersion
    }
}

rootProject.name = "loan-java"
```

### 4. Gradle Properties
**File**: `loan-java/gradle.properties`

```properties
quarkusPluginId=io.quarkus
quarkusPluginVersion=3.17.5
quarkusPlatformGroupId=io.quarkus.platform
quarkusPlatformArtifactId=quarkus-bom
quarkusPlatformVersion=3.17.5
```

### 5. Application Configuration
**File**: `loan-java/src/main/resources/application.properties`

```properties
# Application
quarkus.application.name=loan-service

# HTTP Server - matches Python Flask port
quarkus.http.port=50053
quarkus.http.host=0.0.0.0

# gRPC Server - same port for simplicity, Quarkus can handle both
quarkus.grpc.server.port=50053
quarkus.grpc.server.host=0.0.0.0

# MongoDB Configuration
quarkus.mongodb.connection-string=${DB_URL:mongodb://root:example@localhost:27017}
quarkus.mongodb.database=bank

# Service Protocol (http or grpc)
service.protocol=${SERVICE_PROTOCOL:http}

# Logging
quarkus.log.level=DEBUG
quarkus.log.category."com.martianbank".level=DEBUG
```

### 6. Copy Proto File
**File**: `loan-java/src/main/proto/loan.proto`

Copy the existing proto file from `protobufs/loan.proto` with added Java package option:

```protobuf
// Copyright (c) 2023 Cisco Systems, Inc. and its affiliates All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.

syntax = "proto3";

option java_package = "com.martianbank.loan.grpc";
option java_outer_classname = "LoanProto";

package loan;

message LoanRequest {
  string name = 1;
  string email = 2;
  string account_type = 3;
  string account_number = 4;
  string govt_id_type = 5;
  string govt_id_number = 6;
  string loan_type = 7;
  double loan_amount = 8;
  double interest_rate = 9;
  string time_period = 10;
}

message LoanResponse {
  bool approved = 1;
  string message = 2;
}

message LoansHistoryRequest {
  string email = 1;
}

message Loan {
  string name = 1;
  string email = 2;
  string account_type = 3;
  string account_number = 4;
  string govt_id_type = 5;
  string govt_id_number = 6;
  string loan_type = 7;
  double loan_amount = 8;
  double interest_rate = 9;
  string time_period = 10;
  string status = 11;
  string timestamp = 12;
}

message LoansHistoryResponse {
  repeated Loan loans = 1;
}

service LoanService {
  rpc ProcessLoanRequest(LoanRequest) returns (LoanResponse);
  rpc getLoanHistory(LoansHistoryRequest) returns (LoansHistoryResponse);
}
```

### 7. Main Application Class
**File**: `loan-java/src/main/java/com/martianbank/loan/LoanApplication.java`

```java
package com.martianbank.loan;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;

@QuarkusMain
public class LoanApplication {
    public static void main(String[] args) {
        Quarkus.run(args);
    }
}
```

## Success Criteria:

### Automated Verification:
- [ ] Gradle build succeeds: `cd loan-java && ./gradlew build`
- [ ] Proto files compile: `cd loan-java && ./gradlew quarkusGenerateCode`
- [ ] Application starts without errors: `cd loan-java && ./gradlew quarkusDev`

**Implementation Note**: After completing this phase and all automated verification passes, proceed to Phase 1.5 for testing.

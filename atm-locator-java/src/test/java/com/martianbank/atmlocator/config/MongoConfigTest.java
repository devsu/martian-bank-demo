/**
 * Copyright (c) 2023 Cisco Systems, Inc. and its affiliates All rights reserved.
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */
package com.martianbank.atmlocator.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class MongoConfigTest {

    @Test
    void getDatabaseName_returnsConfiguredDatabaseName() {
        // Given
        MongoConfig config = new MongoConfig();
        ReflectionTestUtils.setField(config, "databaseName", "testdb");

        // When
        String dbName = config.getDatabaseName();

        // Then
        assertThat(dbName).isEqualTo("testdb");
    }

    @Test
    void buildConnectionString_withDatabaseHost_usesLocalFormat() {
        // Given
        MongoConfig config = new MongoConfig();
        ReflectionTestUtils.setField(config, "databaseHost", "localhost");
        ReflectionTestUtils.setField(config, "mongoUri", "");
        ReflectionTestUtils.setField(config, "databaseName", "test");

        // When
        String connectionString = ReflectionTestUtils.invokeMethod(config, "buildConnectionString");

        // Then
        assertThat(connectionString).isEqualTo("mongodb://localhost:27017/");
    }

    @Test
    void buildConnectionString_withMongoUri_usesAtlasFormat() {
        // Given
        MongoConfig config = new MongoConfig();
        ReflectionTestUtils.setField(config, "databaseHost", "");
        ReflectionTestUtils.setField(config, "mongoUri", "mongodb+srv://user:pass@cluster.mongodb.net/db");
        ReflectionTestUtils.setField(config, "databaseName", "test");

        // When
        String connectionString = ReflectionTestUtils.invokeMethod(config, "buildConnectionString");

        // Then
        assertThat(connectionString).isEqualTo("mongodb+srv://user:pass@cluster.mongodb.net/db");
    }

    @Test
    void buildConnectionString_withBothSet_databaseHostTakesPrecedence() {
        // Given
        MongoConfig config = new MongoConfig();
        ReflectionTestUtils.setField(config, "databaseHost", "mongo");
        ReflectionTestUtils.setField(config, "mongoUri", "mongodb+srv://user:pass@cluster.mongodb.net/db");
        ReflectionTestUtils.setField(config, "databaseName", "test");

        // When
        String connectionString = ReflectionTestUtils.invokeMethod(config, "buildConnectionString");

        // Then
        assertThat(connectionString).isEqualTo("mongodb://mongo:27017/");
    }

    @Test
    void buildConnectionString_withNeitherSet_usesDefaultLocalhost() {
        // Given
        MongoConfig config = new MongoConfig();
        ReflectionTestUtils.setField(config, "databaseHost", "");
        ReflectionTestUtils.setField(config, "mongoUri", "");
        ReflectionTestUtils.setField(config, "databaseName", "test");

        // When
        String connectionString = ReflectionTestUtils.invokeMethod(config, "buildConnectionString");

        // Then
        assertThat(connectionString).isEqualTo("mongodb://localhost:27017/");
    }

    @Test
    void maskPassword_masksCredentialsInUri() {
        // Given
        MongoConfig config = new MongoConfig();
        String uri = "mongodb://root:secretpassword@localhost:27017/";

        // When
        String masked = ReflectionTestUtils.invokeMethod(config, "maskPassword", uri);

        // Then
        assertThat(masked).isEqualTo("mongodb://***:***@localhost:27017/");
        assertThat(masked).doesNotContain("root");
        assertThat(masked).doesNotContain("secretpassword");
    }

    @Test
    void maskPassword_handlesUriWithoutCredentials() {
        // Given
        MongoConfig config = new MongoConfig();
        String uri = "mongodb://localhost:27017/";

        // When
        String masked = ReflectionTestUtils.invokeMethod(config, "maskPassword", uri);

        // Then
        assertThat(masked).isEqualTo("mongodb://localhost:27017/");
    }
}

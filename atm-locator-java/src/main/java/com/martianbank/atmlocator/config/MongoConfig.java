/**
 * Copyright (c) 2023 Cisco Systems, Inc. and its affiliates All rights reserved.
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */
package com.martianbank.atmlocator.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
@EnableMongoAuditing
public class MongoConfig extends AbstractMongoClientConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(MongoConfig.class);

    @Value("${spring.data.mongodb.uri:}")
    private String mongoUri;

    @Value("${DATABASE_HOST:}")
    private String databaseHost;

    @Value("${spring.data.mongodb.database:test}")
    private String databaseName;

    @Override
    protected String getDatabaseName() {
        return databaseName;
    }

    @Override
    @Bean
    public MongoClient mongoClient() {
        String connectionString = buildConnectionString();
        logger.info(" --- Connecting to MongoDB for atm-locator microservice --- ");
        logger.info("Connection string: {}", maskPassword(connectionString));

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .build();

        MongoClient client = MongoClients.create(settings);
        logger.info(" --- MongoDB Connected --- ");
        return client;
    }

    private String buildConnectionString() {
        // If DATABASE_HOST is set, use local MongoDB format (legacy compatibility)
        if (databaseHost != null && !databaseHost.isEmpty()) {
            logger.info("Connecting to local MongoDB at {} ...", databaseHost);
            return String.format("mongodb://%s:27017/", databaseHost);
        }

        // Otherwise use DB_URL (MongoDB Atlas or full connection string)
        if (mongoUri != null && !mongoUri.isEmpty()) {
            logger.info("Connecting to MongoDB Atlas (Cloud) ...");
            return mongoUri;
        }

        // Default fallback
        return "mongodb://localhost:27017/";
    }

    private String maskPassword(String uri) {
        // Mask password in logs for security
        return uri.replaceAll("://[^:]+:[^@]+@", "://***:***@");
    }

    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoClient(), getDatabaseName());
    }
}

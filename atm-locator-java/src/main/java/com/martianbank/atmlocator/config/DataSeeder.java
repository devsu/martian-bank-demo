/**
 * Copyright (c) 2023 Cisco Systems, Inc. and its affiliates All rights reserved.
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */
package com.martianbank.atmlocator.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.martianbank.atmlocator.model.Address;
import com.martianbank.atmlocator.model.Atm;
import com.martianbank.atmlocator.model.Coordinates;
import com.martianbank.atmlocator.model.Timings;
import com.martianbank.atmlocator.repository.AtmRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Seeds the database with ATM data on application startup.
 *
 * Mirrors legacy behavior from atm-locator/config/db.js:40-64:
 * 1. Read atm_data.json
 * 2. Process MongoDB extended JSON format ($oid, $date)
 * 3. Drop existing ATM collection
 * 4. Insert seed data
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);
    private static final String SEED_FILE = "atm_data.json";

    private final AtmRepository atmRepository;
    private final ObjectMapper objectMapper;

    public DataSeeder(AtmRepository atmRepository, ObjectMapper objectMapper) {
        this.atmRepository = atmRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void run(String... args) {
        logger.info("Seeding database with data from {} ...", SEED_FILE);

        try {
            List<Atm> atms = loadAndProcessSeedData();
            dropAndReseed(atms);
            logger.info("Database seeded with {} records.", atms.size());
        } catch (Exception e) {
            logger.error("Error seeding database: {}", e.getMessage());
        }
    }

    /**
     * Load seed data from JSON file and process MongoDB extended JSON format.
     */
    List<Atm> loadAndProcessSeedData() throws Exception {
        ClassPathResource resource = new ClassPathResource(SEED_FILE);
        InputStream inputStream = resource.getInputStream();

        List<JsonNode> jsonNodes = objectMapper.readValue(
                inputStream,
                new TypeReference<List<JsonNode>>() {}
        );

        List<Atm> atms = new ArrayList<>();
        for (JsonNode node : jsonNodes) {
            Atm atm = processAtmNode(node);
            atms.add(atm);
        }

        return atms;
    }

    /**
     * Process a single ATM JSON node, handling MongoDB extended JSON format.
     *
     * Legacy processing (db.js:48-53):
     * - _id: { $oid: "..." } -> ObjectId
     * - createdAt: { $date: "..." } -> Date
     * - updatedAt: { $date: "..." } -> Date
     */
    private Atm processAtmNode(JsonNode node) {
        Atm atm = new Atm();

        // Process _id ($oid format)
        JsonNode idNode = node.get("_id");
        if (idNode != null && idNode.has("$oid")) {
            atm.setId(idNode.get("$oid").asText());
        }

        // Basic fields
        atm.setName(node.get("name").asText());
        atm.setAtmHours(node.get("atmHours").asText());
        atm.setNumberOfATMs(node.get("numberOfATMs").asInt());
        atm.setIsOpen(node.get("isOpen").asBoolean());
        atm.setInterPlanetary(node.has("interPlanetary") ? node.get("interPlanetary").asBoolean() : false);

        // Process address
        JsonNode addressNode = node.get("address");
        if (addressNode != null) {
            Address address = new Address();
            address.setStreet(addressNode.get("street").asText());
            address.setCity(addressNode.get("city").asText());
            address.setState(addressNode.get("state").asText());
            address.setZip(addressNode.get("zip").asText());
            atm.setAddress(address);
        }

        // Process coordinates
        JsonNode coordsNode = node.get("coordinates");
        if (coordsNode != null) {
            Coordinates coordinates = new Coordinates();
            coordinates.setLatitude(coordsNode.get("latitude").asDouble());
            coordinates.setLongitude(coordsNode.get("longitude").asDouble());
            atm.setCoordinates(coordinates);
        }

        // Process timings
        JsonNode timingsNode = node.get("timings");
        if (timingsNode != null) {
            Timings timings = new Timings();
            timings.setMonFri(timingsNode.get("monFri").asText());
            timings.setSatSun(timingsNode.get("satSun").asText());
            if (timingsNode.has("holidays") && !timingsNode.get("holidays").isNull()) {
                timings.setHolidays(timingsNode.get("holidays").asText());
            }
            atm.setTimings(timings);
        }

        // Process timestamps ($date format)
        JsonNode createdAtNode = node.get("createdAt");
        if (createdAtNode != null && createdAtNode.has("$date")) {
            atm.setCreatedAt(Instant.parse(createdAtNode.get("$date").asText()));
        }

        JsonNode updatedAtNode = node.get("updatedAt");
        if (updatedAtNode != null && updatedAtNode.has("$date")) {
            atm.setUpdatedAt(Instant.parse(updatedAtNode.get("$date").asText()));
        }

        // Version field
        if (node.has("__v")) {
            atm.setVersion(node.get("__v").asInt());
        }

        return atm;
    }

    /**
     * Drop existing collection and insert seed data.
     *
     * Legacy behavior (db.js:55-60):
     * - ATM.collection.drop()
     * - ATM.insertMany(processedData)
     */
    private void dropAndReseed(List<Atm> atms) {
        try {
            atmRepository.deleteAll();
            logger.debug("Dropped existing ATM collection");
        } catch (Exception e) {
            logger.debug("Error dropping collection (may not exist): {}", e.getMessage());
        }

        atmRepository.saveAll(atms);
    }
}

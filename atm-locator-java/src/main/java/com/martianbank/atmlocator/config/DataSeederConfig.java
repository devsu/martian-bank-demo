package com.martianbank.atmlocator.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.martianbank.atmlocator.model.Address;
import com.martianbank.atmlocator.model.Atm;
import com.martianbank.atmlocator.model.Coordinates;
import com.martianbank.atmlocator.model.Timings;
import com.martianbank.atmlocator.repository.AtmRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Configuration class for seeding ATM data into MongoDB on application startup.
 * Loads ATM data from atm_data.json and populates the database.
 */
@Configuration
public class DataSeederConfig {

    private static final Logger logger = LoggerFactory.getLogger(DataSeederConfig.class);

    /**
     * Creates an ApplicationRunner that seeds the database with ATM data on startup.
     * This method:
     * 1. Loads atm_data.json from the classpath
     * 2. Clears existing ATM data from MongoDB
     * 3. Parses JSON to ATM entities
     * 4. Saves all ATMs to MongoDB
     *
     * @param atmRepository the repository for ATM data access
     * @param objectMapper the Jackson ObjectMapper for JSON parsing
     * @return an ApplicationRunner that performs database seeding
     */
    @Bean
    public ApplicationRunner dataSeeder(AtmRepository atmRepository, ObjectMapper objectMapper) {
        return (ApplicationArguments args) -> {
            logger.info("Starting ATM data seeding process...");

            try {
                // Load atm_data.json from classpath
                ClassPathResource resource = new ClassPathResource("atm_data.json");
                InputStream inputStream = resource.getInputStream();

                // Parse JSON array
                JsonNode rootNode = objectMapper.readTree(inputStream);

                if (!rootNode.isArray()) {
                    logger.error("atm_data.json must contain a JSON array");
                    return;
                }

                // Clear existing ATM data
                long existingCount = atmRepository.count();
                logger.info("Clearing {} existing ATM records from database", existingCount);
                atmRepository.deleteAll();

                // Parse JSON to ATM entities
                List<Atm> atms = new ArrayList<>();
                for (JsonNode atmNode : rootNode) {
                    Atm atm = parseAtmFromJson(atmNode);
                    atms.add(atm);
                }

                // Save all ATMs to MongoDB
                List<Atm> savedAtms = atmRepository.saveAll(atms);
                logger.info("Successfully seeded {} ATM records into database", savedAtms.size());

            } catch (Exception e) {
                logger.error("Failed to seed ATM data: {}", e.getMessage(), e);
            }
        };
    }

    /**
     * Parses a single ATM entity from a JSON node.
     * Handles MongoDB Extended JSON format (e.g., $oid for ObjectId).
     *
     * @param node the JSON node representing an ATM
     * @return the parsed ATM entity
     */
    private Atm parseAtmFromJson(JsonNode node) {
        // Parse address
        JsonNode addressNode = node.get("address");
        Address address = Address.builder()
                .street(getTextValue(addressNode, "street"))
                .city(getTextValue(addressNode, "city"))
                .state(getTextValue(addressNode, "state"))
                .zip(getTextValue(addressNode, "zip"))
                .build();

        // Parse coordinates
        JsonNode coordinatesNode = node.get("coordinates");
        Coordinates coordinates = Coordinates.builder()
                .latitude(getDoubleValue(coordinatesNode, "latitude"))
                .longitude(getDoubleValue(coordinatesNode, "longitude"))
                .build();

        // Parse timings
        JsonNode timingsNode = node.get("timings");
        Timings timings = Timings.builder()
                .monFri(getTextValue(timingsNode, "monFri"))
                .satSun(getTextValue(timingsNode, "satSun"))
                .holidays(getTextValue(timingsNode, "holidays"))
                .build();

        // Build ATM entity (let MongoDB generate new IDs)
        return Atm.builder()
                .name(getTextValue(node, "name"))
                .address(address)
                .coordinates(coordinates)
                .timings(timings)
                .atmHours(getTextValue(node, "atmHours"))
                .numberOfATMs(getIntValue(node, "numberOfATMs"))
                .isOpenNow(getBooleanValue(node, "isOpen"))
                .isInterPlanetary(getBooleanValue(node, "interPlanetary"))
                .build();
    }

    /**
     * Gets a text value from a JSON node, or null if not present.
     */
    private String getTextValue(JsonNode node, String fieldName) {
        if (node == null || !node.has(fieldName)) {
            return null;
        }
        JsonNode fieldNode = node.get(fieldName);
        return fieldNode.isNull() ? null : fieldNode.asText();
    }

    /**
     * Gets a double value from a JSON node, or null if not present.
     */
    private Double getDoubleValue(JsonNode node, String fieldName) {
        if (node == null || !node.has(fieldName)) {
            return null;
        }
        JsonNode fieldNode = node.get(fieldName);
        return fieldNode.isNull() ? null : fieldNode.asDouble();
    }

    /**
     * Gets an integer value from a JSON node, or null if not present.
     */
    private Integer getIntValue(JsonNode node, String fieldName) {
        if (node == null || !node.has(fieldName)) {
            return null;
        }
        JsonNode fieldNode = node.get(fieldName);
        return fieldNode.isNull() ? null : fieldNode.asInt();
    }

    /**
     * Gets a boolean value from a JSON node, or false if not present.
     */
    private Boolean getBooleanValue(JsonNode node, String fieldName) {
        if (node == null || !node.has(fieldName)) {
            return false;
        }
        JsonNode fieldNode = node.get(fieldName);
        return fieldNode.isNull() ? false : fieldNode.asBoolean();
    }
}

package com.martianbank.atmlocator.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI configuration for the ATM Locator service.
 * Configures Swagger UI and API documentation metadata.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Creates the OpenAPI specification with API metadata.
     *
     * @return configured OpenAPI instance with title, version, and description
     */
    @Bean
    public OpenAPI atmLocatorOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ATM Locator API")
                        .version("1.0.0")
                        .description("REST API for locating ATMs within the Martian Bank network. " +
                                "Provides endpoints to search for nearby ATMs based on geographic coordinates " +
                                "and retrieve detailed information about specific ATM locations."));
    }
}

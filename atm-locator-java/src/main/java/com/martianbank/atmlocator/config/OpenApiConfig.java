/**
 * Copyright (c) 2023 Cisco Systems, Inc. and its affiliates All rights reserved.
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */
package com.martianbank.atmlocator.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI configuration matching legacy Swagger setup.
 *
 * Legacy endpoints:
 * - Swagger UI: /docs
 * - OpenAPI JSON: /docs.json
 *
 * Configured via application.yml:
 * springdoc.api-docs.path: /docs.json
 * springdoc.swagger-ui.path: /docs
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI atmLocatorOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("atm-locator")
                        .version("1.0.0")
                        .description("API documentation for the atm-locator microservice")
                        .license(new License()
                                .name("BSD-style")
                                .url("https://github.com/cisco-open/martian-bank-demo/blob/main/LICENSE")));
    }
}

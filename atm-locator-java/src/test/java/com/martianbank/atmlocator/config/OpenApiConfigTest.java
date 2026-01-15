/**
 * Copyright (c) 2023 Cisco Systems, Inc. and its affiliates All rights reserved.
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */
package com.martianbank.atmlocator.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OpenApiConfigTest {

    private final OpenApiConfig config = new OpenApiConfig();

    @Test
    void atmLocatorOpenAPI_returnsOpenAPIInstance() {
        // When
        OpenAPI openAPI = config.atmLocatorOpenAPI();

        // Then
        assertThat(openAPI).isNotNull();
    }

    @Test
    void atmLocatorOpenAPI_hasCorrectTitle() {
        // When
        OpenAPI openAPI = config.atmLocatorOpenAPI();

        // Then
        assertThat(openAPI.getInfo().getTitle()).isEqualTo("atm-locator");
    }

    @Test
    void atmLocatorOpenAPI_hasCorrectVersion() {
        // When
        OpenAPI openAPI = config.atmLocatorOpenAPI();

        // Then
        assertThat(openAPI.getInfo().getVersion()).isEqualTo("1.0.0");
    }

    @Test
    void atmLocatorOpenAPI_hasDescription() {
        // When
        OpenAPI openAPI = config.atmLocatorOpenAPI();

        // Then
        assertThat(openAPI.getInfo().getDescription()).isNotNull();
        assertThat(openAPI.getInfo().getDescription()).contains("atm-locator");
    }

    @Test
    void atmLocatorOpenAPI_hasLicense() {
        // When
        OpenAPI openAPI = config.atmLocatorOpenAPI();

        // Then
        assertThat(openAPI.getInfo().getLicense()).isNotNull();
        assertThat(openAPI.getInfo().getLicense().getName()).isEqualTo("BSD-style");
    }

    @Test
    void atmLocatorOpenAPI_hasLicenseUrl() {
        // When
        OpenAPI openAPI = config.atmLocatorOpenAPI();

        // Then
        assertThat(openAPI.getInfo().getLicense().getUrl()).contains("LICENSE");
    }
}

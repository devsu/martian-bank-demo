/**
 * Copyright (c) 2023 Cisco Systems, Inc. and its affiliates All rights reserved.
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */
package com.martianbank.atmlocator.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;

class CorsConfigTest {

    private CorsConfig corsConfig;

    @BeforeEach
    void setUp() {
        corsConfig = new CorsConfig();
    }

    @Test
    void corsFilter_isNotNull() {
        // When
        CorsFilter filter = corsConfig.corsFilter();

        // Then
        assertThat(filter).isNotNull();
    }

    @Test
    void corsFilter_allowsAllOriginPatterns() throws Exception {
        // Given
        CorsFilter filter = corsConfig.corsFilter();

        // When
        CorsConfiguration config = extractCorsConfiguration(filter);

        // Then
        assertThat(config.getAllowedOriginPatterns()).contains("*");
    }

    @Test
    void corsFilter_allowsCredentials() throws Exception {
        // Given
        CorsFilter filter = corsConfig.corsFilter();

        // When
        CorsConfiguration config = extractCorsConfiguration(filter);

        // Then
        assertThat(config.getAllowCredentials()).isTrue();
    }

    @Test
    void corsFilter_allowsAllHeaders() throws Exception {
        // Given
        CorsFilter filter = corsConfig.corsFilter();

        // When
        CorsConfiguration config = extractCorsConfiguration(filter);

        // Then
        assertThat(config.getAllowedHeaders()).contains("*");
    }

    @Test
    void corsFilter_allowsAllMethods() throws Exception {
        // Given
        CorsFilter filter = corsConfig.corsFilter();

        // When
        CorsConfiguration config = extractCorsConfiguration(filter);

        // Then
        assertThat(config.getAllowedMethods()).contains("*");
    }

    /**
     * Helper method to extract CorsConfiguration from CorsFilter.
     * Uses reflection since CorsFilter doesn't expose its configuration directly.
     */
    private CorsConfiguration extractCorsConfiguration(CorsFilter filter) throws Exception {
        Field configSourceField = CorsFilter.class.getDeclaredField("configSource");
        configSourceField.setAccessible(true);
        UrlBasedCorsConfigurationSource source =
                (UrlBasedCorsConfigurationSource) configSourceField.get(filter);

        // Get configuration for the root path pattern
        return source.getCorsConfigurations().get("/**");
    }
}

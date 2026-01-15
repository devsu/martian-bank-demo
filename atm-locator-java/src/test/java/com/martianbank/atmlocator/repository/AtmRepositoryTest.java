/**
 * Copyright (c) 2023 Cisco Systems, Inc. and its affiliates All rights reserved.
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */
package com.martianbank.atmlocator.repository;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for AtmRepository interface.
 *
 * Since we're mocking the database, these tests verify the repository
 * interface contract rather than actual database operations.
 */
class AtmRepositoryTest {

    @Test
    void repository_hasFindByInterPlanetaryMethod() throws NoSuchMethodException {
        // Verify the method exists with correct signature
        Method method = AtmRepository.class.getMethod("findByInterPlanetary", Boolean.class);
        assertThat(method).isNotNull();
        assertThat(method.getReturnType().getSimpleName()).isEqualTo("List");
    }

    @Test
    void repository_hasFindByInterPlanetaryAndIsOpenMethod() throws NoSuchMethodException {
        // Verify the method exists with correct signature
        Method method = AtmRepository.class.getMethod(
                "findByInterPlanetaryAndIsOpen",
                Boolean.class,
                Boolean.class
        );
        assertThat(method).isNotNull();
        assertThat(method.getReturnType().getSimpleName()).isEqualTo("List");
    }

    @Test
    void repository_extendsMongoRepository() {
        // Verify interface hierarchy
        Class<?>[] interfaces = AtmRepository.class.getInterfaces();
        assertThat(interfaces).hasSize(1);
        assertThat(interfaces[0].getSimpleName()).isEqualTo("MongoRepository");
    }
}

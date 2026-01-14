package com.martianbank.loan;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class LoanApplicationTest {

    @ConfigProperty(name = "quarkus.application.name")
    String applicationName;

    @ConfigProperty(name = "quarkus.http.port")
    int httpPort;

    @Test
    void applicationContextLoads() {
        // If we get here, the application context loaded successfully
        assertTrue(true, "Application context should load");
    }

    @Test
    void applicationNameIsConfigured() {
        assertEquals("loan-service", applicationName);
    }

    @Test
    void httpPortIsConfigured() {
        assertEquals(50053, httpPort);
    }
}

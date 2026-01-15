/**
 * Copyright (c) 2023 Cisco Systems, Inc. and its affiliates All rights reserved.
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */
package com.martianbank.atmlocator.testutil;

import com.martianbank.atmlocator.model.Address;
import com.martianbank.atmlocator.model.Atm;
import com.martianbank.atmlocator.model.Coordinates;
import com.martianbank.atmlocator.model.Timings;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Factory class for creating test data objects.
 * Provides consistent test data across all test classes.
 */
public class TestDataFactory {

    public static final String TEST_ATM_ID = "64a6f1cc8c1899820dbdf25a";
    public static final String TEST_ATM_NAME = "Martian ATM (Highway)";

    /**
     * Creates a complete ATM entity with all fields populated.
     * Based on first record from atm_data.json
     */
    public static Atm createTestAtm() {
        Atm atm = new Atm();
        atm.setId(TEST_ATM_ID);
        atm.setName(TEST_ATM_NAME);
        atm.setAddress(createTestAddress());
        atm.setCoordinates(createTestCoordinates());
        atm.setTimings(createTestTimings());
        atm.setAtmHours("24 hours");
        atm.setNumberOfATMs(2);
        atm.setIsOpen(true);
        atm.setInterPlanetary(false);
        atm.setCreatedAt(Instant.parse("2023-07-06T16:54:36.22Z"));
        atm.setUpdatedAt(Instant.parse("2023-07-06T16:54:36.22Z"));
        atm.setVersion(0);
        return atm;
    }

    /**
     * Creates an ATM with interPlanetary = true
     */
    public static Atm createInterplanetaryAtm() {
        Atm atm = createTestAtm();
        atm.setId("64b072fd6981fda9e346bdde");
        atm.setName("Earthern ATM (Georgia Tech)");
        atm.setInterPlanetary(true);
        atm.getAddress().setCity("Atlanta");
        atm.getAddress().setState("Georgia");
        return atm;
    }

    /**
     * Creates an ATM with isOpen = false
     */
    public static Atm createClosedAtm() {
        Atm atm = createTestAtm();
        atm.setId("64a6f2268c1899820dbdf25c");
        atm.setName("Martian ATM (Claytor Lake)");
        atm.setIsOpen(false);
        return atm;
    }

    /**
     * Creates a list of 5 test ATMs for testing limit functionality.
     * Mix of open/closed and planetary/interplanetary.
     */
    public static List<Atm> createAtmList(int count) {
        List<Atm> atms = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Atm atm = createTestAtm();
            atm.setId("testId" + i);
            atm.setName("Test ATM " + i);
            atm.setIsOpen(i % 2 == 0);  // Alternate open/closed
            atms.add(atm);
        }
        return atms;
    }

    /**
     * Creates a list of non-interplanetary ATMs (Mars ATMs).
     */
    public static List<Atm> createMarsAtms(int count) {
        List<Atm> atms = createAtmList(count);
        atms.forEach(atm -> atm.setInterPlanetary(false));
        return atms;
    }

    /**
     * Creates a list of open, non-interplanetary ATMs.
     */
    public static List<Atm> createOpenMarsAtms(int count) {
        List<Atm> atms = createAtmList(count);
        atms.forEach(atm -> {
            atm.setInterPlanetary(false);
            atm.setIsOpen(true);
        });
        return atms;
    }

    /**
     * Creates a test Address object.
     */
    public static Address createTestAddress() {
        Address address = new Address();
        address.setStreet("14th Street, Martian Way");
        address.setCity("Musk City");
        address.setState("Mars");
        address.setZip("40411");
        return address;
    }

    /**
     * Creates a test Coordinates object.
     */
    public static Coordinates createTestCoordinates() {
        Coordinates coordinates = new Coordinates();
        coordinates.setLatitude(37.775);
        coordinates.setLongitude(-81.188);
        return coordinates;
    }

    /**
     * Creates a test Timings object with all fields.
     */
    public static Timings createTestTimings() {
        Timings timings = new Timings();
        timings.setMonFri("9:00 AM - 5:00 PM");
        timings.setSatSun("10:00 AM - 3:00 PM");
        timings.setHolidays("Closed on holidays");
        return timings;
    }

    /**
     * Creates a Timings object without holidays (optional field).
     */
    public static Timings createTimingsWithoutHolidays() {
        Timings timings = new Timings();
        timings.setMonFri("9:00 AM - 5:00 PM");
        timings.setSatSun("10:00 AM - 3:00 PM");
        timings.setHolidays(null);
        return timings;
    }
}

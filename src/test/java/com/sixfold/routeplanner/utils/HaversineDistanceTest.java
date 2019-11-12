package com.sixfold.routeplanner.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HaversineDistanceTest {

    /**
     * Tests distance between Big Ben in London and The Statue of Liberty in New York
     */
    @Test
    public void testCommonHaversineDistance() {
        assertEquals(5574.906161402871, HaversineDistance.distance(51.5007, 0.1246, 40.6892, 74.0455));
    }
}

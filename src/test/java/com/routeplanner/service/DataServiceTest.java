package com.routeplanner.service;

import com.routeplanner.BaseIntegrationTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DataServiceTest extends BaseIntegrationTest {

    @Test
    public void validateGraphGeneration() {
        createGraph();
        validateNodes();
        validateDistances();
    }

    private void validateNodes() {
        List<String> allNodes = repository.getAllNodes();
        assertEquals(5, allNodes.size());
        assertTrue(allNodes.containsAll(Arrays.asList("ODS", "LED", "MSQ", "KJA", "OVB")));
    }

    private void validateDistances() {
        List<Double> connectionDistances = repository.getDistances("ODS", "LED");
        assertEquals(2, connectionDistances.size());
        assertEquals(1487.31, connectionDistances.get(0));
    }
}

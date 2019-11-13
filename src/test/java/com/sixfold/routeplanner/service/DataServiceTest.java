package com.sixfold.routeplanner.service;

import com.sixfold.routeplanner.BaseIntegrationTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DataServiceTest extends BaseIntegrationTest {

    @Test
    public void validateGraphGeneration() throws IOException {
        createGraph();
        validateNodes();
        validateDistances();
        deleteCsv();
    }

    private void validateNodes() {
        List<String> allNodes = repository.getAllNodes();
        assertEquals(5, allNodes.size());
        assertTrue(allNodes.contains("ODS"));
        assertTrue(allNodes.contains("LED"));
        assertTrue(allNodes.contains("MSQ"));
        assertTrue(allNodes.contains("KJA"));
        assertTrue(allNodes.contains("OVB"));
    }

    private void validateDistances() {
        List<Double> connectionDistances = repository.getDistance("ODS", "LED");
        assertEquals(2, connectionDistances.size());
        assertEquals(1487.31, connectionDistances.get(0));
    }
}

package com.sixfold.routeplanner.service;

import com.sixfold.routeplanner.AppStatus;
import com.sixfold.routeplanner.BaseIntegrationTest;
import com.sixfold.routeplanner.dto.ShortestPathResponse;
import com.sixfold.routeplanner.exceptions.AppStatusException;
import com.sixfold.routeplanner.exceptions.ResultNotFoundException;
import com.sixfold.routeplanner.utils.StatusName;
import org.junit.jupiter.api.Test;

import javax.annotation.Resource;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class RouteServiceTest extends BaseIntegrationTest {

    @Resource
    private RouteService routeService;

    @Test
    public void getShortestPathThrowsExceptionWhenCalculatingGraph() {
        AppStatus.setStatus(StatusName.CALCULATING.name());
        assertThrows(AppStatusException.class, () -> routeService.getShortestPath("TLN", "IST", 4));
    }

    @Test
    public void getShortestPathThrowsExceptionWhenPathNotFound() {
        createGraph();
        assertThrows(ResultNotFoundException.class, () -> routeService.getShortestPath("AAA", "LLL", 4));
    }

    @Test
    public void getShortestPathWithOneStop() throws AppStatusException, ResultNotFoundException {
        createGraph();
        ShortestPathResponse resp = routeService.getShortestPath("ODS", "LED", 0);
        assertTrue(resp.getAirports().containsAll(Arrays.asList("ODS", "LED")));
        assertEquals(1, resp.getDistances().size());
        assertEquals(1487.31, resp.getTotalDistance());
    }

    @Test
    public void getShortestPathFourStops() throws AppStatusException, ResultNotFoundException {
        createGraph();
        ShortestPathResponse resp = routeService.getShortestPath("ODS", "LED", 2);
        assertTrue(resp.getAirports().containsAll(Arrays.asList("ODS", "LED")));
        assertEquals(3, resp.getDistances().size());
        assertEquals(7390.41, resp.getTotalDistance());
    }

}

package com.sixfold.routeplanner.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.IOException;

@SpringBootTest
public class DataServiceTest {

    @Resource
    private DataService dataService;

    @Resource
    private RouteService routeService;

    @Test
    public void test() throws IOException {
        dataService.saveAirportDataToGraph();
    }
}

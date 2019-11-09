package com.sixfold.routeplanner.service;

import com.sixfold.routeplanner.dto.Airport;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
public class DataServiceTest {

    @Resource
    private DataService dataService;

    @Resource
    private RouteService routeService;

    @Test
    public void test() {
        dataService.saveAirportData();
    }
}

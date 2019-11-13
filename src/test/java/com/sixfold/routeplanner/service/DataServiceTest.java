package com.sixfold.routeplanner.service;

import com.sixfold.routeplanner.BaseIntegrationTest;
import com.sixfold.routeplanner.TestRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.annotation.Resource;
import java.io.IOException;

public class DataServiceTest extends BaseIntegrationTest {

    @Resource
    private DataService dataService;

    @Test
    public void readCsvSuccessfully() {
        dataService.generateGraph();
        System.out.println(repository.getAllNodes());
    }
}

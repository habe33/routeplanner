package com.sixfold.routeplanner.cron;

import com.sixfold.routeplanner.service.CacheService;
import com.sixfold.routeplanner.service.DataService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;

@Log4j2
@Configuration
@EnableScheduling
public class Scheduler {

    private DataService dataService;

    @Autowired
    public Scheduler(CacheService cacheService, DataService dataService) {
        this.dataService = dataService;
    }

    @Scheduled(initialDelay = 0L, fixedRate = 4 * 60 * 60 * 1000)
    public void getAirportData() {
        dataService.saveAirportData();
    }

}

package com.sixfold.routeplanner.cron;

import com.sixfold.routeplanner.service.CacheService;
import com.sixfold.routeplanner.service.DataService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import static com.sixfold.routeplanner.service.DataService.AIRPORT_CACHE;

@Log4j2
@Configuration
@EnableScheduling
public class Scheduler {

    private CacheService cacheService;
    private DataService dataService;

    @Autowired
    public Scheduler(CacheService cacheService, DataService dataService) {
        this.cacheService = cacheService;
        this.dataService = dataService;
    }

    @Scheduled(fixedRate = 4 * 60 * 60 * 1000)
    public void clearAllCaches() {
        cacheService.evictAllCacheValues(AIRPORT_CACHE);
        //TODO empty neo4j database
        //TODO create new graph
    }

    @Scheduled(initialDelay = 0L, fixedRate = 4 * 60 * 60 * 1000)
    public void getAirportData() {
        dataService.saveAirportData();
    }

}

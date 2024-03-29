package com.routeplanner.cron;

import com.routeplanner.service.DataService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Log4j2
@Configuration
@EnableScheduling
@Profile("!test")
public class GraphScheduler {

    private DataService dataService;

    @Autowired
    public GraphScheduler(DataService dataService) {
        this.dataService = dataService;
    }

    @Scheduled(initialDelay = 0L, fixedRate = 24 * 60 * 60 * 1000)
    public void generateGraph() {
        dataService.generateGraph();
    }

}

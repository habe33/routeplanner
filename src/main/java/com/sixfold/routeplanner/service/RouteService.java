package com.sixfold.routeplanner.service;

import com.sixfold.routeplanner.AppStatus;
import com.sixfold.routeplanner.dto.ShortestPathResponse;
import com.sixfold.routeplanner.exceptions.AppStatusException;
import com.sixfold.routeplanner.repository.Neo4jRepository;
import com.sixfold.routeplanner.utils.StatusName;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class RouteService {

    private Neo4jRepository repository;

    @Autowired
    public RouteService(Neo4jRepository repository) {
        this.repository = repository;
    }

    public ShortestPathResponse getShortestPath(String startCode, String endCode) throws AppStatusException {
        if (AppStatus.getStatus().equals(StatusName.CALCULATING.name())) {
            throw new AppStatusException("Application is calculating new graph");
        }
        return repository.getKShortestPaths(startCode, endCode);
    }

}

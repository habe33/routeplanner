package com.sixfold.routeplanner.controller;

import com.sixfold.routeplanner.dto.ShortestPathResponse;
import com.sixfold.routeplanner.exceptions.AppStatusException;
import com.sixfold.routeplanner.service.RouteService;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@RestController("/api")
@Validated
public class RouteController {

    private RouteService routeService;

    @Autowired
    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @GetMapping("/shortest-path")
    public ShortestPathResponse getShortestPath(@RequestParam("startCode") @NotEmpty @NotNull @Length(max = 3) String startCode,
                                                @RequestParam("endCode") @NotEmpty @NotNull @Length(max = 3) String endCode) throws AppStatusException {
        return routeService.getShortestPath(startCode, endCode);
    }

}


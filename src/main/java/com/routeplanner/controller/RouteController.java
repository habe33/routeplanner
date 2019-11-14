package com.routeplanner.controller;

import com.routeplanner.exceptions.AppStatusException;
import com.routeplanner.service.RouteService;
import com.routeplanner.dto.ShortestPathResponse;
import com.routeplanner.exceptions.ResultNotFoundException;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@RestController
@Validated
public class RouteController {

    private RouteService routeService;

    @Autowired
    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @GetMapping("/shortest-path")
    public ShortestPathResponse getShortestPath(@NotEmpty @NotNull @Length(max = 3) @RequestParam("startCode") String startCode,
                                                @NotEmpty @NotNull @Length(max = 3) @RequestParam("endCode") String endCode,
                                                @NotNull @Max(4) @Min(0) @RequestParam("stops") Integer stops)
            throws AppStatusException, ResultNotFoundException {
        return routeService.getShortestPath(startCode, endCode, stops);
    }

}


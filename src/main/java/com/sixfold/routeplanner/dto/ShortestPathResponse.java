package com.sixfold.routeplanner.dto;

import lombok.Data;

import java.util.List;

@Data
public class ShortestPathResponse {

    private List<String> airports;
    private List<Double> distances;
    private Double totalDistance;
}

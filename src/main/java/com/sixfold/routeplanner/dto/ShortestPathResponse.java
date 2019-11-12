package com.sixfold.routeplanner.dto;

import lombok.Data;

@Data
public class ShortestPathResponse {

    private String[] places;
    private String[] costs;
    private double totalCost;
}

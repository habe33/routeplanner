package com.sixfold.routeplanner.dto;

import lombok.Data;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

import java.util.List;

@Data
@NodeEntity
public class ShortestPath {

    @Property(name = "places")
    private List<String> places;
    @Property(name = "costs")
    private List<Double> costs;
    @Property(name = "totalCost")
    private double totalCost;
}

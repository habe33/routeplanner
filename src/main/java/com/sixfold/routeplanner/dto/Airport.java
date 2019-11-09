package com.sixfold.routeplanner.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

@Data
@NodeEntity
@NoArgsConstructor
public class Airport {

    @Id
    @GeneratedValue
    private Long id;
    @Property(name = "iataCode")
    private String code;
    @Property(name = "lat")
    private float latitude;
    @Property(name = "long")
    private float longitude;

    public Airport(String code, float latitude, float longitude) {
        this.code = code;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}

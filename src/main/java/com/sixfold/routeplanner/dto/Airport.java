package com.sixfold.routeplanner.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@NoArgsConstructor
public class Airport {

    private String code;
    private float latitude;
    private float longitude;

    public Airport(String code, float latitude, float longitude) {
        this.code = code;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Airport airport = (Airport) o;
        return Objects.equals(getCode(), airport.getCode());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCode());
    }
}

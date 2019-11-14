package com.routeplanner.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Airport {

    private String code;
    private float latitude;
    private float longitude;

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

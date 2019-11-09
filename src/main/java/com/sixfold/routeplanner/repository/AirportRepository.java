package com.sixfold.routeplanner.repository;

import com.sixfold.routeplanner.dto.Airport;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface AirportRepository extends Neo4jRepository<Airport, String> {

    @Query("MATCH (a:Airport),(b:Airport) " +
            "WHERE a.iataCode = {0} AND b.iataCode = {1} " +
            "CREATE (a)-[r:FLY {distance: {2}}]->(b) " +
            "RETURN type(r)")
    void createRelationship(String code1, String code2, double distance);

    @Query("MATCH (a:Airport),(b:Airport) " +
            //"WHERE a.iataCode = {0} AND b.iataCode = {1} " +
            "WITH point({ longitude: a.longitude, latitude: a.latitude }) AS aPoint, point({ longitude: b.longitude, latitude: b.latitude }) AS bPoint " +
            "CREATE (a)-[r:FLY {distance: (round(distance(aPoint, bPoint)/1000))}]->(b) ")
    void createRelationships();
}

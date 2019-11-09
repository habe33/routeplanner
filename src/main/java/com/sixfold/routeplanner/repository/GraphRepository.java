package com.sixfold.routeplanner.repository;

import com.sixfold.routeplanner.dto.Airport;
import com.sixfold.routeplanner.dto.ShortestPath;
import org.neo4j.cypher.internal.javacompat.ExecutionResult;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.List;

public interface GraphRepository extends Neo4jRepository<Airport, String> {

    @Query("LOAD CSV FROM {0} AS line " +
            "MERGE (n:Airport {iataCode1 : line[0]}) " +
            "MERGE (m:Airport {iataCode2 : line[1]}) " +
            "MERGE (n)-[:FLY {dist : line[2]}]->(m);")
    void insertGraphFromCsv(String filePath);

    List<Airport> findAll();

    @Query("MATCH (start:Airport{iataCode:{0}}), (end:Airport{iataCode:{1}}) " +
            "CALL algo.kShortestPaths.stream(start, end, {2}, 'dist' ,{}) " +
            "YIELD index, nodeIds, costs " +
            "RETURN [node in algo.getNodesById(nodeIds) | node.name] AS places, " +
            "       costs, " +
            "       reduce(acc = 0.0, cost in costs | acc + cost) AS totalCost")
    ShortestPath getKShortestPaths(String iataCode1, String iataCode2, int k);
}

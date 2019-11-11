package com.sixfold.routeplanner.repository;

import lombok.extern.log4j.Log4j2;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Result;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.LinkedHashMap;
import java.util.Map;

@Log4j2
@Component
public class Neo4jRepository {

    @Resource
    private GraphDatabaseService graphDb;

    public void insertGraph(String filePath) {
        insertNodes(filePath);
        insertRelationships(filePath);
    }

    public void getKShortestPaths(String startCode, String endCode) {
        String query = "MATCH ( " +
                "(start:Airport{iataCode:'" + startCode + "'}), " +
                "(end:Airport{iataCode:'" + endCode + "'}) " +
                "CALL algo.kShortestPaths.stream(start, end, 10, 'dist', {}) " +
                "YIELD index, nodeIds, costs " +
                "WHERE length(costs) = 4 " +
                "RETURN [node in algo.getNodesById(nodeIds) | node.name] AS places, " +
                "costs, " +
                "toFloat(reduce(acc = 0.0, cost in costs | acc + cost)) AS totalCost " +
                "ORDER BY totalCost " +
                "LIMIT 1";
        Result res = graphDb.execute(query);
        String[] columns = {"places", "costs", "totalCost"};
        Map<String, Object> obj = getResultMap(res, columns);
        res.close();
        for (Map.Entry<String, Object> entry : obj.entrySet()) {
            System.out.println(entry.getKey() + ":" + entry.getValue().toString());
        }
    }

    private Map<String, Object> getResultMap(Result res, String [] columns) {
        Map<String, Object> obj = new LinkedHashMap<>();
        while (res.hasNext()) {
            Map<String, Object> row = res.next();
            for (String t : columns) {
                obj.put(t, null);
            }
            for (Map.Entry<String, Object> col : row.entrySet()) {
                obj.put(col.getKey(), col.getValue());
            }
        }
        return obj;
    }

    private void insertRelationships(String filePath) {
        log.info("Inserting relationships");
        graphDb.execute("LOAD CSV FROM 'file:///" + filePath + "' AS line " +
                "MATCH (n:Airport {iataCode : line[0]}) " +
                "MATCH (m:Airport {iataCode : line[1]}) " +
                "MERGE (n)-[:FLY {dist : toFloat(line[2])}]->(m); ");
    }

    private void insertNodes(String filePath) {
        log.info("Inserting nodes");
        graphDb.execute("LOAD CSV FROM 'file:///" + filePath + "' AS line " +
                "MERGE (n:Airport {iataCode : line[0]}) " +
                "MERGE (m:Airport {iataCode : line[1]}); ");
    }
}

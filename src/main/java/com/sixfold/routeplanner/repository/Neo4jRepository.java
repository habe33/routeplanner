package com.sixfold.routeplanner.repository;

import com.sixfold.routeplanner.dto.ShortestPathResponse;
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

    public void insertGraph(String nodesPath, String relationshipsPath) {
        insertNodes(nodesPath);
        insertRelationships(relationshipsPath);
        log.info("Finished generating graph!");
    }

    public ShortestPathResponse getKShortestPaths(String startCode, String endCode) {
        log.info("Calculating shortest path");
        String query = "MATCH " +
                "(start:Airport{iataCode:'" + startCode + "'}), " +
                "(end:Airport{iataCode:'" + endCode + "'}) " +
                "CALL algo.kShortestPaths.stream(start, end, 10, 'dist', {}) " +
                "YIELD index, nodeIds, costs " +
                "WHERE length(costs) = 6 " +
                "RETURN [node in algo.getNodesById(nodeIds) | node.iataCode] AS places, " +
                "costs, " +
                "toFloat(reduce(acc = 0.0, cost in costs | acc + cost)) AS totalCost " +
                "ORDER BY totalCost " +
                "LIMIT 1";
        Result res = graphDb.execute(query);
        String[] columns = {"places", "costs", "totalCost"};
        Map<String, Object> objMap = getResultMap(res, columns);
        res.close();
        return mapToResponse(objMap);
    }

    public void deleteGraph() {
        graphDb.execute("MATCH (n) " +
                "DETACH DELETE n");
    }

    private ShortestPathResponse mapToResponse(Map<String, Object> objMap) {
        ShortestPathResponse resp = new ShortestPathResponse();
        resp.setCosts((String[]) objMap.get("costs"));
        resp.setPlaces((String[]) objMap.get("places"));
        resp.setTotalCost((double) objMap.get("totalCost"));
        for (Map.Entry<String, Object> entry : objMap.entrySet()) {
            System.out.println(entry.getKey() + ":" + entry.getValue().toString());
        }
        System.out.println(resp);
        return resp;
    }

    private Map<String, Object> getResultMap(Result res, String[] columns) {
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
        log.info("Inserting relationships to DB");
        graphDb.execute("USING PERIODIC COMMIT 500 " +
                "LOAD CSV FROM 'file:///" + filePath + "' AS line " +
                "MATCH (n:Airport {iataCode : line[0]}) " +
                "MATCH (m:Airport {iataCode : line[1]}) " +
                "CREATE (n)-[:FLY {dist : toFloat(line[2])}]->(m); ");
    }

    private void insertNodes(String filePath) {
        log.info("Inserting nodes to DB");
        graphDb.execute("USING PERIODIC COMMIT 500 " +
                "LOAD CSV FROM 'file:///" + filePath + "' AS line " +
                "CREATE (n:Airport {iataCode : line[0]}); ");
    }
}

package com.sixfold.routeplanner;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class TestRepository {

    private GraphDatabaseService graphDb;

    @Autowired
    public TestRepository(GraphDatabaseService graphDb) {
        this.graphDb = graphDb;
    }

    public List<String> getAllNodes() {
        Result res = graphDb.execute("MATCH (n) " +
                "RETURN collect(n.iataCode) AS result; ");
        String[] columns =  {"result"};
        return (List<String>) getResultMap(res, columns).get("result");
    }

    public List<Double> getDistance(String startCode, String endCode) {
        Result res = graphDb.execute(
                "MATCH (start:Airport {iataCode : '" + startCode + "'})-[r]-(end:Airport{iataCode : '" + endCode + "'}) " +
                        "RETURN collect(r.dist) AS result");
        String[] columns = {"result"};
        return (List<Double>) getResultMap(res, columns).get("result");
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
}

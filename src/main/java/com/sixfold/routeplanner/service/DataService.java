package com.sixfold.routeplanner.service;

import com.sixfold.routeplanner.dto.Airport;
import com.sixfold.routeplanner.repository.GraphRepository;
import com.sixfold.routeplanner.utils.HaversinDistance;
import lombok.extern.log4j.Log4j2;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Service
public class DataService {

    public static final String AIRPORT_DATA_CSV = "C:/Temp/new.csv";
    @Value("${app.data.url}")
    private String dataUrl;

    private GraphRepository repository;

    @Resource
    private GraphDatabaseService graphDb;

    public final static String AIRPORT_CACHE = "airportData";
    private final static int AIRPORT_IATA_INDEX = 9;
    private final static int AIRPORT_LATITUDE_INDEX = 11;
    private final static int AIRPORT_LONGITUDE_INDEX = 12;

    @Autowired
    public DataService(GraphRepository repository) {
        this.repository = repository;
    }

    public void saveAirportData() {
        log.info("Creating graph");
        List<Airport> data = getAirportData();
        writeRelationshipsToCsv(data);
        log.info("Inserting graph");
        graphDb.execute("LOAD CSV FROM 'file:///C:/Temp/new.csv' AS line " +
                "            MERGE (n:Airport {iataCode : line[0]}) " +
                "            MERGE (m:Airport {iataCode : line[1]}) " +
                "            MERGE (n)-[:FLY {dist : toFloat(line[2])}]->(m);");
        log.info("Finished creating graph");
        System.out.println(repository.findAll());
        Result res = graphDb.execute("MATCH (start:Airport{iataCode:'OCA'}), (end:Airport{iataCode:'WLR'}) " +
                "CALL algo.kShortestPaths.stream(start, end, 5, 'dist', {}) " +
                "YIELD index, nodeIds, costs " +
                "RETURN [node in algo.getNodesById(nodeIds) | node.name] AS places, " +
                "      costs, " +
                "       toFloat(reduce(acc = 0.0, cost in costs | acc + cost)) AS totalCost");
        String test[] = {"places", "costs", "totalCost"};
        Map<String, Object> obj = new LinkedHashMap();
        while(res.hasNext()) {
            Map<String, Object> row = res.next();
            for (String t:test) {
                obj.put(t, null);
            }
            for(Map.Entry<String, Object> col : row.entrySet()) {
                obj.put(col.getKey(),col.getValue());
            }
        }
        res.close();
        for (Map.Entry<String, Object> entry : obj.entrySet()) {
            System.out.println(entry.getKey() + ":" + entry.getValue().toString());
        }

        //System.out.println(repository.getKShortestPaths("OCA", "WLR", 5));
        log.info("enough");
    }

    @Cacheable(value = AIRPORT_CACHE, key = "#root.methodName")
    public List<Airport> getAirportData() {
        log.info("Loading airport data from {}", dataUrl);
        List<Airport> airportData = new ArrayList<>();
        try {
            URL content = new URL(dataUrl);
            Scanner inputStream = getInputStream(content);
            int counter = 0;
            while (inputStream.hasNext()) {
                if (counter == 10) {
                    break;
                }
                String[] values = getSplittedValues(inputStream);
                if (values.length != 13) {
                    continue;
                }
                String iataCode = values[AIRPORT_IATA_INDEX];
                String latitude = values[AIRPORT_LATITUDE_INDEX];
                String longitude = values[AIRPORT_LONGITUDE_INDEX];
                if (!isValidValues(iataCode, latitude, longitude)) {
                    continue;
                }
                Airport node = new Airport(iataCode, Float.parseFloat(latitude), Float.parseFloat(longitude));
                airportData.add(node);
                counter++;
            }
        } catch (MalformedURLException e) {
            log.error("Could not get content from {}", dataUrl);
        } catch (IOException e) {
            log.error("Could not open content stream: {}", e.getMessage());
        }
        log.info("Loaded data successfully");
        log.info("Created {} nodes", airportData.size());
        return airportData.stream().distinct().collect(Collectors.toList());
    }

    private void writeRelationshipsToCsv(List<Airport> data) {
        log.info("Writing relationships to CSV");
        try {
            FileWriter csvWriter = new FileWriter("C:\\Temp\\new.csv");
            for (Airport node : data) {
                for (Airport n : data) {
                    if (!node.getCode().equals(n.getCode())) {
                        writeAirportData(csvWriter, node, n);
                    }
                }
            }
            csvWriter.flush();
            csvWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.info("Finished writing relationships");
    }

    private void writeAirportData(FileWriter csvWriter, Airport node, Airport n) throws IOException {
        csvWriter.append(n.getCode())
                .append(",")
                .append(node.getCode())
                .append(",")
                .append(String.format("%.2f", HaversinDistance.distance(node.getLatitude(), node.getLongitude(), n.getLatitude(), n.getLongitude())))
                .append("\n");
    }

    private Scanner getInputStream(URL content) throws IOException {
        Scanner inputStream = new Scanner(content.openStream());
        inputStream.useDelimiter("\n");
        inputStream.nextLine();
        return inputStream;
    }

    private boolean isValidValues(String iataCode, String latitude, String longitude) {
        return isValidIataCode(iataCode) && isValidLatitude(latitude) && isValidLongitude(longitude);
    }

    private boolean isValidIataCode(String iataCode) {
        return iataCode != null && !iataCode.isBlank();
    }

    private boolean isValidLatitude(String latitude) {
        return latitude != null && !latitude.isBlank();
    }

    private boolean isValidLongitude(String longitude) {
        return longitude != null && !longitude.isBlank();
    }

    private String[] getSplittedValues(Scanner inputStream) {
        String data = inputStream.next();
        return data.replace("\"", "").strip().split(",");
    }
}

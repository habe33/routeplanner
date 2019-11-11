package com.sixfold.routeplanner.service;

import com.sixfold.routeplanner.dto.Airport;
import com.sixfold.routeplanner.repository.Neo4jRepository;
import com.sixfold.routeplanner.utils.HaversinDistance;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Service
public class DataService {

    @Value("${app.data.url}")
    private String dataUrl;
    @Value("${app.data.nodes}")
    private String numOfNodes;
    private Neo4jRepository repository;

    private final static String AIRPORT_RELATIONSHIPS_CSV = "C:/Temp/relationships.csv";
    private final static String AIRPORT_NODES_CSV = "C:/Temp/nodes.csv";
    private final static int AIRPORT_IATA_INDEX = 9;
    private final static int AIRPORT_LATITUDE_INDEX = 11;
    private final static int AIRPORT_LONGITUDE_INDEX = 12;

    @Autowired
    public DataService(Neo4jRepository repository) {
        this.repository = repository;
    }

    public void generateGraph() {
        List<Airport> data = getAirportData();
        writeNodesToCsv(data);
        writeRelationshipsToCsv(data);
        repository.insertGraph(AIRPORT_NODES_CSV, AIRPORT_RELATIONSHIPS_CSV);
        //repository.getKShortestPaths("GDH", "QNY");
    }

    private List<Airport> getAirportData() {
        List<Airport> airportData = new ArrayList<>();
        try {
            URL content = new URL(dataUrl);
            Scanner inputStream = getInputStream(content);
            int counter = 0;
            while (inputStream.hasNext()) {
                if (counter == Integer.parseInt(numOfNodes)) {
                    log.debug("Loaded {} nodes from {}", numOfNodes, dataUrl);
                    break;
                }
                String[] values = getSplittedValues(inputStream);
                if (!isCorrectLength(values)) {
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
        airportData = airportData.stream().distinct().collect(Collectors.toList());
        return airportData;
    }

    private boolean isCorrectLength(String[] values) {
        return values.length == 13;
    }

    private void writeNodesToCsv(List<Airport> data) {
        log.debug("Writing nodes to {}", AIRPORT_NODES_CSV);
        try {
            FileWriter csvWriter = new FileWriter(AIRPORT_NODES_CSV);
            for (Airport node : data) {
                writeNodeData(csvWriter, node);
            }
            csvWriter.flush();
            csvWriter.close();
        } catch (IOException e) {
            log.error("Error writing data to file {} ", AIRPORT_NODES_CSV);
        }
    }

    private void writeRelationshipsToCsv(List<Airport> data) {
        log.debug("Writing relationships to {}", AIRPORT_RELATIONSHIPS_CSV);
        try {
            FileWriter csvWriter = new FileWriter(AIRPORT_RELATIONSHIPS_CSV);
            for (Airport node : data) {
                for (Airport n : data) {
                    if (!node.equals(n)) {
                        writeAirportData(csvWriter, node, n);
                    }
                }
            }
            csvWriter.flush();
            csvWriter.close();
        } catch (IOException e) {
            log.error("Error writing data to file {} ", AIRPORT_RELATIONSHIPS_CSV);
        }
    }

    private void writeNodeData(FileWriter csvWriter, Airport node) throws IOException {
        csvWriter.append(node.getCode()).append("\n");
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
        return iataCode != null && !iataCode.isEmpty();
    }

    private boolean isValidLatitude(String latitude) {
        return latitude != null && !latitude.isEmpty();
    }

    private boolean isValidLongitude(String longitude) {
        return longitude != null && !longitude.isEmpty();
    }

    private String[] getSplittedValues(Scanner inputStream) {
        String data = inputStream.next();
        return data.replace("\"", "").replace(" ", "").split(",");
    }
}

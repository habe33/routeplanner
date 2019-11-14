package com.routeplanner.service;

import com.routeplanner.repository.Neo4jRepository;
import com.routeplanner.AppStatus;
import com.routeplanner.dto.Airport;
import com.routeplanner.utils.HaversineDistance;
import com.routeplanner.utils.StatusName;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

@Log4j2
@Service
public class DataService {

    @Value("${app.data.url}")
    private String dataUrl;
    @Value("${app.data.csv.path.relationships}")
    private String airportRelationshipsCsvPath;
    @Value("${app.data.csv.path.nodes}")
    private String airportNodesCsvPath;

    private Neo4jRepository repository;
    private static final int AIRPORT_TYPE_INDEX = 1;
    private static final int AIRPORT_IATA_INDEX = 9;
    private static final int AIRPORT_LONGITUDE_INDEX = 11;
    private static final int AIRPORT_LATITUDE_INDEX = 12;
    private static final String LARGE_AIRPORT = "large_airport";

    @Autowired
    public DataService(Neo4jRepository repository) {
        this.repository = repository;
    }

    public void generateGraph() {
        AppStatus.setStatus(StatusName.CALCULATING.name());
        insertGraph();
        AppStatus.setStatus(StatusName.RUNNING.name());
    }

    private void insertGraph() {
        repository.deleteGraph();
        List<Airport> data = getAirportData();
        writeNodesToCsv(data);
        writeRelationshipsToCsv(data);
        repository.insertGraph(airportNodesCsvPath, airportRelationshipsCsvPath);
    }

    private void addAirportToList(List<Airport> airportData, String[] values) {
        if (isCorrectLength(values)) {
            String airportType = values[AIRPORT_TYPE_INDEX];
            String iataCode = values[AIRPORT_IATA_INDEX];
            String latitude = values[AIRPORT_LATITUDE_INDEX];
            String longitude = values[AIRPORT_LONGITUDE_INDEX];
            if (isValidValues(airportType, iataCode, latitude, longitude)) {
                Airport node = new Airport(iataCode, Float.parseFloat(latitude), Float.parseFloat(longitude));
                airportData.add(node);
            }
        }
    }

    private void writeNodesToCsv(List<Airport> data) {
        log.debug("Writing nodes to {}", airportNodesCsvPath);
        try (FileWriter csvWriter = new FileWriter(airportNodesCsvPath)) {
            for (Airport node : data) {
                writeNodeData(csvWriter, node);
            }
        } catch (IOException e) {
            log.error("Error writing data to file {} ", airportNodesCsvPath);
        }
    }

    private void writeRelationshipsToCsv(List<Airport> data) {
        log.debug("Writing relationships to {}", airportRelationshipsCsvPath);
        try (FileWriter csvWriter = new FileWriter(airportRelationshipsCsvPath)) {
            for (Airport node : data) {
                for (Airport n : data) {
                    if (!node.getCode().equals(n.getCode())) {
                        writeAirportData(csvWriter, node, n);
                    }
                }
            }
        } catch (IOException e) {
            log.error("Error writing data to file {} ", airportRelationshipsCsvPath);
        }
    }

    private void writeNodeData(FileWriter csvWriter, Airport node) throws IOException {
        csvWriter.append(node.getCode()).append("\n");
        csvWriter.flush();
    }

    private void writeAirportData(FileWriter csvWriter, Airport node, Airport n) throws IOException {
        csvWriter.append(n.getCode())
                .append(",")
                .append(node.getCode())
                .append(",")
                .append(String.format("%.2f", HaversineDistance.distance(node.getLatitude(), node.getLongitude(), n.getLatitude(), n.getLongitude())))
                .append("\n");
        csvWriter.flush();
    }

    private List<Airport> getAirportData() {
        List<Airport> airportData = new ArrayList<>();
        try {
            URL content = new URL(dataUrl);
            Scanner inputStream = getInputStream(content);
            boolean firstLine = true;
            while (inputStream.hasNext()) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                addAirportToList(airportData, getSplittedValues(inputStream.next()));
            }
        } catch (MalformedURLException e) {
            log.error("Could not get content from {}", dataUrl);
        } catch (IOException e) {
            log.error("Could not open content stream: {}", e.getMessage());
        }
        airportData = airportData.stream().distinct().collect(Collectors.toList());
        log.debug("Loaded {} large airport nodes from {}", airportData.size(), dataUrl);
        return airportData;
    }

    private Scanner getInputStream(URL content) throws IOException {
        Scanner inputStream = new Scanner(content.openStream());
        inputStream.useDelimiter("\n");
        inputStream.nextLine();
        return inputStream;
    }

    private String[] getSplittedValues(String data) {
        return data.replace("\"", "").replace(" ", "").split(",");
    }

    private boolean isCorrectLength(String[] values) {
        return values.length == 13;
    }

    private boolean isValidValues(String airportType, String iataCode, String latitude, String longitude) {
        return isValidAirportType(airportType) && isValidIataCode(iataCode) && isValidLatitude(latitude) && isValidLongitude(longitude);
    }

    private boolean isValidAirportType(String airportType) {
        return airportType != null && !airportType.isEmpty() && airportType.equals(LARGE_AIRPORT);
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
}

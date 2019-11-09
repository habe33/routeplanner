package com.sixfold.routeplanner.service;

import com.sixfold.routeplanner.dto.Airport;
import com.sixfold.routeplanner.repository.AirportRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

@Log4j2
@Service
public class DataService {

    @Value("${app.data.url}")
    private String dataUrl;

    private AirportRepository repository;

    public final static String AIRPORT_CACHE = "airportData";
    private final static int AIRPORT_IATA_INDEX = 9;
    private final static int AIRPORT_LATITUDE_INDEX = 11;
    private final static int AIRPORT_LONGITUDE_INDEX = 12;

    @Autowired
    public DataService(AirportRepository repository) {
        this.repository = repository;
    }

    public void saveAirportData() {
        log.info("Saving airport data to DB");
        List<Airport> data = getAirportData();
        repository.saveAll(data);
        log.info("Creating relationships");
        repository.createRelationships();
//        data.forEach(node -> {
//            data.stream().filter(n -> !node.getCode().equals(n.getCode())).forEach(n -> {
//                repository.createRelationships(node.getCode(), n.getCode());
//            });
//        });
        log.info("Finished creating relationships");
    }

    @Cacheable(value = AIRPORT_CACHE, key = "#root.methodName")
    public List<Airport> getAirportData() {
        log.info("Loading airport data from {}", dataUrl);
        List<Airport> airportData = new ArrayList<>();
        try {
            URL content = new URL(dataUrl);
            Scanner inputStream = getInputStream(content);
            while (inputStream.hasNext()) {
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

    public Optional<Airport> getNode(String iataCode) {
        return getAirportData().stream().filter(d -> d.getCode().equals(iataCode)).findAny();
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

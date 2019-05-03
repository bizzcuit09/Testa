package com.example.playlistgenerator.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class LocationService {

    private static final String API_KEY = "Apje4EpG8MDdJIUPhxKVpsu_DBQuyJQQ6zKBvULftlYaVTDrGm5OqAtM6vJrjKCy";

    private static final String ERROR_DURING_PARSING_THE_JSON_OBJECT = "Error during parsing the Json object";

    private RestTemplate restTemplate;

    @Autowired
    public LocationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public long getTravelDuration(String startPoint, String endPoint) {

        long[] travelDuration = new long[1];

        try {
            String origins = getCoordinatesAsString(startPoint);
            String destinations = getCoordinatesAsString(endPoint);

            String url = "https://dev.virtualearth.net/REST/v1/Routes/DistanceMatrix?origins="
                    + origins
                    + "&destinations="
                    + destinations
                    + "&travelMode=driving&key="
                    + API_KEY;

            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            ObjectMapper objectMapper = new ObjectMapper();

            if (response.getStatusCode() == HttpStatus.OK) {
                try {
                    JsonNode rootNode = objectMapper.readTree(response.getBody());

                    List<JsonNode> results = rootNode.findValues("results");
                    results.get(0).forEach(jsonNode ->
                            travelDuration[0] = jsonNode.path("travelDuration").asLong());

                } catch (IOException e) {
                    throw new IllegalArgumentException(ERROR_DURING_PARSING_THE_JSON_OBJECT);
                }
            }
        }
        catch (NoSuchFieldException e) {
            throw new IllegalArgumentException("Wrong input");
//            log.error(e.getMessage());
        }

        return travelDuration[0];
    }

    private String getCoordinatesAsString(String location) throws NoSuchFieldException {

        String url = "http://dev.virtualearth.net/REST/v1/Locations/"
                + location
                + "?key="
                + API_KEY;

        List<String> coordinates = new ArrayList<>();

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        ObjectMapper objectMapper = new ObjectMapper();

        if (response.getStatusCode() == HttpStatus.OK) {

            try {
                JsonNode rootNode = objectMapper.readTree(response.getBody());

                List<JsonNode> points = rootNode.findValues("geocodePoints");

                if (points.get(0).size() > 1) {
                    points.get(0).forEach(jsonNode ->
                            jsonNode.path("usageTypes").forEach(type -> {
                                if (type.asText().equals("Route")) {
                                    jsonNode.path("coordinates").forEach(coordinate ->
                                            coordinates.add(coordinate.asText()));
                                }
                            }));
                } else {
                    points.get(0).forEach(jsonNode ->
                            jsonNode.path("coordinates").forEach(coordinate ->
                                    coordinates.add(coordinate.asText())));
                }
            } catch (IOException e) {
//                log.error(ERROR_DURING_PARSING_THE_JSON_OBJECT);
            }
        }

        if (coordinates.isEmpty()) {
            throw new NoSuchFieldException("Failed to read the coordinates: " + location);
        }
        return String.join(",", coordinates);
    }
}